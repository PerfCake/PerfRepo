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
import org.perfrepo.web.dao.TestExecutionDAO;

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
import java.util.*;

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

   @Inject
   private TestExecutionDAO testExecutionDAO;

   private String expression;
   private Map<String, Object> variables;
   private Metric metric;

   @Override
   public boolean checkCondition(String condition, double currentResult, Metric metric) {
      expression = null;
      variables = new HashMap<>();
      variables.put("result", currentResult);
      this.metric = metric;

      CommonTree ast = parseTree(condition);
      walkTree(ast);

      if(expression == null) {
         throw new IllegalStateException("Condition was not correctly parsed.");
      }

      ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");

      Object result;
      try {
         result = engine.eval(expression, new SimpleBindings(variables));
      } catch (ScriptException e) {
         throw new IllegalArgumentException("Error occured while evaluating the expression.", e);
      }

      if(!(result instanceof Boolean)) {
         throw new IllegalStateException("Result of the expression is not boolean.");
      }

      return ((Boolean) result).booleanValue();
   }

   public void setTestExecutionDAO(TestExecutionDAO testExecutionDAO) {
      this.testExecutionDAO = testExecutionDAO;
   }

   /**
    * Walks through the AST and extract condition expression and it's parameters
    *
    * @param tree
    */
   private void walkTree(CommonTree tree) {
      if(tree == null) {
         throw new IllegalArgumentException("Tree cannot be null.");
      }

      if(!tree.getChild(0).getText().equalsIgnoreCase("CONDITION") || !tree.getChild(1).getText().equalsIgnoreCase("DEFINE")) {
         throw new IllegalArgumentException("Doesn't have exactly 1 CONDITION and 1 DEFINE statements ");
      }

      String equation = tree.getChild(0).getChild(0).getText(); //equation after condition
      expression = equation;

      //process variables
      Tree defineNode = tree.getChild(1);

      for(int i = 0; i < defineNode.getChildCount(); i++) {
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
      if(!assignmentRoot.getText().equalsIgnoreCase("=")) {
         throw new IllegalArgumentException("Wrong syntax, expected '='.");
      }

      String variableName = assignmentRoot.getChild(0).getText();
      Tree groupFunctionOrSelect = assignmentRoot.getChild(1);
      if(groupFunctionOrSelect == null) {
         throw new IllegalArgumentException("Wrong syntax, expected SELECT or grouping function.");
      }

      //there is a grouping function and multi-select will follow
      if(DslGroupingFunctions.contains(groupFunctionOrSelect.getText())) {
         handleMultiSelect(groupFunctionOrSelect, variableName);
      }
      else { //single select
         handleSimpleSelect(groupFunctionOrSelect, variableName);
      }
   }

   /**
    * Handles assignment with multiselect
    *
    * @param groupFunction AST subtree with group function as a root
    * @param variableName
    */
   private void handleMultiSelect(Tree groupFunction, String variableName) {
      DslGroupingFunctions groupingFunction = DslGroupingFunctions.parseString(groupFunction.getText());
      List<TestExecution> testExecutions;
      Tree select = groupFunction.getChild(0);

      if(!select.getText().equalsIgnoreCase("SELECT")) {
         throw new IllegalArgumentException("Wrong syntax, expected SELECT.");
      }

      Tree whereOrLast = select.getChild(0);
      if(whereOrLast.getText().equalsIgnoreCase("WHERE")) { //WHERE
         Tree inOrBetween = whereOrLast.getChild(0);
         if(inOrBetween.getText().equalsIgnoreCase("IN")) { //IN where
            String propertyName = inOrBetween.getChild(0).getText();
            Collection<Object> values = new ArrayList<>();

            for(int i = 1; i < inOrBetween.getChildCount(); i++) { //starting from 1 because child(0) is property name
               values.add(inOrBetween.getChild(i).getText());
            }

            testExecutions = testExecutionDAO.getAllByPropertyIn(propertyName, values);
         }
         else if(inOrBetween.getText().equalsIgnoreCase("BETWEEN")) { //BETWEEN where
            //boundaries for between are stored directly as children
            //AND keyword is omitted from the AST
            String propertyName = inOrBetween.getChild(0).getText();
            Comparable from = parseBetweenArgument(inOrBetween.getChild(1).getText());
            Comparable to = parseBetweenArgument(inOrBetween.getChild(2).getText());

            testExecutions = testExecutionDAO.getAllByPropertyBetween(propertyName, from, to);
         }
         else {
            throw new IllegalArgumentException("Wrong syntax, expected IN or BETWEEN.");
         }
      }
      else if(whereOrLast.getText().equalsIgnoreCase("LAST")) { //LAST
         if(whereOrLast.getChildCount() == 1) { // LAST x
            int numberOfLast = Integer.valueOf(whereOrLast.getChild(0).getText());
            testExecutions = testExecutionDAO.getLast(numberOfLast);
         }
         else if(whereOrLast.getChildCount() == 2) { // LAST x, y
            int lastFrom = Integer.valueOf(whereOrLast.getChild(0).getText());
            int lastTo = Integer.valueOf(whereOrLast.getChild(1).getText());
            testExecutions = testExecutionDAO.getLast(lastFrom, lastTo);
         }
         else {
            throw new IllegalArgumentException("Wrong syntax, LAST has to have exactly one or two arguments.");
         }
      }
      else {
         throw new IllegalArgumentException("Wrong syntax, expected WHERE or LAST.");
      }

      List<Double> values = getValuesFromTestExecutions(testExecutions);
      variables.put(variableName, groupingFunction.compute(values));
   }

   /**
    * Handles assignment with simple select
    *
    * @param select AST subtree with select clause as a root
    * @param variableName
    */
   private void handleSimpleSelect(Tree select, String variableName) {
      if(!select.getText().equalsIgnoreCase("SELECT")) {
         throw new IllegalArgumentException("Wrong syntax, expected SELECT.");
      }

      Tree where = select.getChild(0);
      if(!where.getText().equalsIgnoreCase("WHERE")) {
         throw new IllegalArgumentException("Wrong syntax, expected WHERE.");
      }

      Tree assignment = where.getChild(0);
      if(!assignment.getText().equalsIgnoreCase("=")) {
         throw new IllegalArgumentException("Wrong syntax, expected '='.");
      }

      String property = assignment.getChild(0).getText();
      String propertyValue = assignment.getChild(1).getText();

      List<TestExecution> testExecutions = testExecutionDAO.getAllByProperty(property, propertyValue);
      if(testExecutions == null || testExecutions.isEmpty()) {
         throw new IllegalArgumentException("Test execution with " + property + " = " + propertyValue + " doesn't exist.");
      }

      Double value = getValueFromMetric(testExecutions.get(0));
      variables.put(variableName, value);
   }

   /**
    * Extracts actual double values directly from test executions
    *
    * @param testExecutions
    * @return
    */
   private List<Double> getValuesFromTestExecutions(List<TestExecution> testExecutions) {
      List<Double> results = new ArrayList<>();
      for(TestExecution testExecution: testExecutions) {
         Double result = getValueFromMetric(testExecution);
         if(result != null) {
            results.add(result);
         }
      }

      return results;
   }

   /**
    * Helper method. Returns value from test execution assigned to the given metric. Works
    * only for single-valued test executions.
    *
    * @param testExecution
    * @return
    */
   private Double getValueFromMetric(TestExecution testExecution) {
      for(Value value: testExecution.getValues()) {
         if(value.getMetricName().equals(metric.getName())) {
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
      }
      catch(RecognitionException ex) {
         throw new IllegalStateException("Recognition exception is never thrown, only declared.");
      }

      return (CommonTree)ret.tree;
   }

   /**
    * Tries to parse an object to specific type for BETWEEN operator
    *
    * @param argument
    * @return
    */
   private Comparable parseBetweenArgument(String argument) {
      try {
         return Long.parseLong(argument);
      }
      catch (NumberFormatException ex) {} //OK, continue

      SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
      try {
         return formatter.parse(argument);
      }
      catch (ParseException e) {} //OK, continue

      return argument;
   }
}
