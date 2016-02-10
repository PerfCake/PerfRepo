package org.perfrepo.web.alerting;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;
import org.perfrepo.model.Metric;
import org.perfrepo.model.TestExecution;
import org.perfrepo.model.Value;
import org.perfrepo.model.to.TestExecutionSearchTO;
import org.perfrepo.web.dao.TestExecutionDAO;
import org.perfrepo.web.service.UserService;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.inject.Named;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of @link{ConditionChecker}
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
@Named
@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ConditionCheckerImpl implements ConditionChecker {

   private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

   @Inject
   private TestExecutionDAO testExecutionDAO;

   @Inject
   private UserService userService;

   private String expression;
   private Map<String, Object> variables;
   private Metric metric;

   @Override
   public boolean checkCondition(String condition, double currentResult, Metric metric) {
      expression = null;
      variables = new HashMap<>();
      variables.put("result", currentResult);
      this.metric = metric;

      CommonTree ast = null;
      try {
         ast = parseTree(condition);
      } catch (NullPointerException ex) {
         throw new IllegalArgumentException("Condition cannot be empty.");
      }
      walkTree(ast);

      if (expression == null) {
         throw new IllegalStateException("Condition was not correctly parsed.");
      }

      ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");

      Object result;
      try {
         result = engine.eval(expression, new SimpleBindings(variables));
      } catch (ScriptException e) {
         throw new IllegalArgumentException("Error occurred while evaluating the expression.", e);
      }

      if (!(result instanceof Boolean)) {
         throw new IllegalStateException("Result of the expression is not boolean.");
      }

      return ((Boolean) result).booleanValue();
   }

   /**
    * Walks through the AST and extract condition expression and it's parameters
    *
    * @param tree
    */
   private void walkTree(CommonTree tree) {
      if (tree == null) {
         throw new IllegalArgumentException("Tree cannot be null.");
      }

      if (!tree.getChild(0).getText().equalsIgnoreCase("CONDITION") || !tree.getChild(1).getText().equalsIgnoreCase("DEFINE")) {
         throw new IllegalArgumentException("Doesn't have exactly 1 CONDITION and 1 DEFINE statements.");
      }

      //<missing EOF>. I haven't figured out any better way to recognize this error.
      // TODO: figure something out
      if (tree.getChild(2).getText().contains("missing")) {
         throw new IllegalArgumentException("Unexpected end of condition.");
      }

      String equation = tree.getChild(0).getChild(0).getText(); //equation after condition
      expression = equation;

      //process variables
      Tree defineNode = tree.getChild(1);

      for (int i = 0; i < defineNode.getChildCount(); i++) {
         Tree variableAssignment = defineNode.getChild(i);
         processVariableAssignment(variableAssignment);
      }
   }

   /**
    * Processes every single assignment, e.g. x = (SELECT WHERE id = 1) or x = AVG(SELECT LAST 10) etc.
    *
    * @param assignmentRoot subtree with '=' char as a root
    */
   private void processVariableAssignment(Tree assignmentRoot) {
      if (!assignmentRoot.getText().equalsIgnoreCase("=")) {
         throw new IllegalArgumentException("Wrong syntax, expected '='.");
      }

      String variableName = assignmentRoot.getChild(0).getText();
      Tree groupFunctionOrSelect = assignmentRoot.getChild(1);
      if (groupFunctionOrSelect == null) {
         throw new IllegalArgumentException("Wrong syntax, expected SELECT or grouping function.");
      }

      List<TestExecution> testExecutions = null;
      Double variableValue = null;

      //there is a grouping function and multi-select will follow
      if (DslGroupingFunctions.contains(groupFunctionOrSelect.getText())) {
         DslGroupingFunctions groupingFunction = DslGroupingFunctions.parseString(groupFunctionOrSelect.getText());
         Tree select = groupFunctionOrSelect.getChild(0);

         testExecutions = handleSelect(select);
         if (testExecutions == null) {
            throw new IllegalArgumentException("Error occurred during getting test executions.");
         }

         List<Double> values = getValuesFromTestExecutions(testExecutions);
         variableValue = (values == null || values.isEmpty()) ? null : groupingFunction.compute(values);
      } else { //single select
         testExecutions = handleSelect(groupFunctionOrSelect);
         if (testExecutions == null || testExecutions.size() > 1) {
            throw new IllegalArgumentException("Error occurred or there is more than one test execution found, but no grouping function applied.");
         }
         if (testExecutions.isEmpty()) {
            throw new IllegalArgumentException("No test executions satisfying variable '" + variableName + "' was found.");
         }
         variableValue = getValueFromMetric(testExecutions.get(0));
      }

      variables.put(variableName, variableValue);
   }

   /**
    * Handles SELECT clause
    *
    * @param select root of the tree with SELECT as a root keyword
    * @return list of test executions according to the SELECT query
    */
   private List<TestExecution> handleSelect(Tree select) {
      if (!select.getText().equalsIgnoreCase("SELECT")) {
         throw new IllegalArgumentException("Wrong syntax, expected SELECT.");
      }

      TestExecutionSearchTO searchCriteria = new TestExecutionSearchTO();
      List<TestExecution> testExecutions = null;
      Tree whereOrLast = select.getChild(0);
      Map<String, Integer> parsedLast = null;

      if (select.getChildCount() == 1 && whereOrLast.getText().equalsIgnoreCase("LAST")) { //SELECT only with LAST
         parsedLast = processLast(whereOrLast);
         searchCriteria.setLimitFrom(parsedLast.get("lastFrom"));
         searchCriteria.setLimitHowMany(parsedLast.get("howMany"));
      } else if (select.getChildCount() == 2 && select.getChild(1).getText().equalsIgnoreCase("LAST")) { //SELECT with WHERE and LAST
         parsedLast = processLast(select.getChild(1));
         //this information about LAST will be used in retrieving of test executions
         //because there is also WHERE clause present
      }

      if (whereOrLast.getText().equalsIgnoreCase("WHERE")) { //WHERE
         for (int i = 0; i < whereOrLast.getChildCount(); i++) { //going through all AND clauses
            Tree simpleOrIn = whereOrLast.getChild(i);

            if (simpleOrIn.getText().equalsIgnoreCase("=") || simpleOrIn.getText().equalsIgnoreCase(">=") || simpleOrIn.getText().equalsIgnoreCase("<=")) { //WHERE with '=', '>=' or '<='
               String property = simpleOrIn.getChild(0).getText();
               String propertyValue = simpleOrIn.getChild(1).getText();

               addCriteriaByPropertyName(searchCriteria, property, propertyValue, parsedLast, simpleOrIn.getText());
            } else if (simpleOrIn.getText().equalsIgnoreCase("IN")) { //IN where
               String propertyName = simpleOrIn.getChild(0).getText();
               Collection<String> values = new ArrayList<>();

               for (int j = 1; j < simpleOrIn.getChildCount(); j++) { //starting from 1 because child(0) is property name
                  values.add(simpleOrIn.getChild(j).getText());
               }

               addCriteriaByPropertyIn(searchCriteria, propertyName, values);
            } else {
               throw new IllegalArgumentException("Wrong syntax, expected '=', '<=', '>=' or IN.");
            }
         }
      }

      testExecutions = testExecutionDAO.searchTestExecutions(searchCriteria, userService.getLoggedUserGroupNames()).getResult();

      return testExecutions;
   }

   /**
    * Extracts actual double values directly from test executions
    *
    * @param testExecutions
    * @return
    */
   private List<Double> getValuesFromTestExecutions(List<TestExecution> testExecutions) {
      List<Double> results = new ArrayList<>();
      for (TestExecution testExecution : testExecutions) {
         Double result = getValueFromMetric(testExecution);
         if (result != null) {
            results.add(result);
         }
      }

      return results;
   }

   /**
    * Helper method. Returns value from test execution assigned to the given metric. Works only for single-valued test
    * executions.
    *
    * @param testExecution
    * @return
    */
   private Double getValueFromMetric(TestExecution testExecution) {
      for (Value value : testExecution.getValues()) {
         if (value.getMetricName().equals(metric.getName())) {
            return value.getResultValue();
         }
      }

      return null;
   }

   /**
    * Parses the string in our DSL into Abstract Syntax Tree
    *
    * @param string
    * @return AST
    */
   private CommonTree parseTree(String string) {
      //lexer splits input into tokens
      ANTLRStringStream input = new ANTLRStringStream(string);
      TokenStream tokens = new CommonTokenStream(new AlertingDSLLexer(input));

      //parser generates abstract syntax tree
      AlertingDSLParser parser = new AlertingDSLParser(tokens);

      AlertingDSLParser.expression_return ret;
      try {
         ret = parser.expression();
      } catch (RecognitionException ex) {
         throw new IllegalStateException("Recognition exception is never thrown, only declared.");
      }

      return (CommonTree) ret.tree;
   }

   /**
    * Helper method. Retrieves test executions according to the name of the property that is in the WHERE condition,
    * e.g. there is a different process of retrieving test execution by ID and with specific tags
    *
    * @param propertyName  name of the property
    * @param propertyValue value that is supplied for the property, might have different meaning with different
    *                      property
    * @param parsedLast    if there is also LAST clause, it's parsed in this Map. See processLast() method for details.
    * @param operator      operator of the condition, e.g. '=', '>=', '<=' ...
    * @return
    */
   private void addCriteriaByPropertyName(TestExecutionSearchTO searchCriteria, String propertyName, String propertyValue, Map<String, Integer> parsedLast, String operator) {
      if (propertyName.equalsIgnoreCase("tags")) {
         searchCriteria.setTags(propertyValue);

         if (parsedLast != null) { //LAST is present
            searchCriteria.setLimitFrom(parsedLast.get("lastFrom"));
            searchCriteria.setLimitHowMany(parsedLast.get("howMany"));
         }
      } else if (propertyName.equalsIgnoreCase("id")) {
         List<Long> ids = Arrays.asList(Long.parseLong(propertyValue));
         searchCriteria.setIds(ids);
      } else if (propertyName.equalsIgnoreCase("date")) {
         Date parsedDate = null;
         try {
            parsedDate = DATE_FORMAT.parse(propertyValue);
         } catch (ParseException e) {
            throw new IllegalArgumentException("Date is in a wrong format. Accepted format is " + DATE_FORMAT.toPattern());
         }

         if (operator.equals(">=")) {
            searchCriteria.setStartedFrom(parsedDate);
         } else if (operator.equals("<=")) {
            searchCriteria.setStartedTo(parsedDate);
         }
      } else {
         throw new UnsupportedOperationException("Currently supported properties with operator '=' are 'id', 'tags', 'date'.");
      }
   }

   /**
    * Adds IDs to search criteria using the IN operator, i.e. ID must belong to the set of IDs provided.
    *
    * @param searchCriteria
    * @param propertyName
    * @param values
    * @return
    */
   private void addCriteriaByPropertyIn(TestExecutionSearchTO searchCriteria, String propertyName, Collection<String> values) {
      if (propertyName.equalsIgnoreCase("id")) {
         List<Long> ids = new ArrayList<>();
         for (String id : values) {
            ids.add(Long.parseLong(id));
         }
         searchCriteria.setIds(ids);

         return;
      }

      throw new UnsupportedOperationException("Only property 'id' is supported with IN operator right now.");
   }

   /**
    * Helper method. Parses the LAST clause and retrieves information about it. It retrieves a Map of size 2 with keys
    * "lastFrom" and "howMany". "lastFrom" is the first parameter of LAST, "howMany" is the second. See documentation
    * for alerting for details.
    *
    * @param last root of the LAST clause in AST
    * @return
    */
   private Map<String, Integer> processLast(Tree last) {
      Map<String, Integer> result = new HashMap<>();

      if (last.getChildCount() == 1) { // LAST x | it's the same as LAST x, x
         int numberOfLast = Integer.valueOf(last.getChild(0).getText());
         result.put("lastFrom", numberOfLast);
         result.put("howMany", numberOfLast);
      } else if (last.getChildCount() == 2) { // LAST x, y
         int lastFrom = Integer.valueOf(last.getChild(0).getText());
         int howMany = Integer.valueOf(last.getChild(1).getText());
         result.put("lastFrom", lastFrom);
         result.put("howMany", howMany);
      } else {
         throw new IllegalArgumentException("Wrong syntax, LAST has to have exactly one or two arguments.");
      }

      /**
       * -----IMPORTANT------
       * because we assume that we have already entered the test execution that we want to process alert
       * with, we don't want to include it into the last X test executions,
       * e.g. CONDITION result > x DEFINE x = (SELECT LAST 1) would always fail, since the last test
       * execution is the currently entered one.
       * As a solution we do lastFrom - 1
       **/
      result.put("lastFrom", result.get("lastFrom") + 1);

      return result;
   }

   public void setTestExecutionDAO(TestExecutionDAO testExecutionDAO) {
      this.testExecutionDAO = testExecutionDAO;
   }

   public void setUserService(UserService userService) {
      this.userService = userService;
   }

   @Override
   public Map<String, Object> getEvaluatedVariables() {
      return variables;
   }
}
