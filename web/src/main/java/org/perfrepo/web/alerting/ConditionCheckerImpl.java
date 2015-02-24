package org.perfrepo.web.alerting;

import com.google.common.collect.Lists;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;
import org.perfrepo.model.Metric;
import org.perfrepo.model.Test;
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

   private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy");

   @Inject
   private TestExecutionDAO testExecutionDAO;

   private String expression;
   private Map<String, Object> variables;
   private Test test;
   private Metric metric;

   @Override
   public boolean checkCondition(String condition, double currentResult, Test test, Metric metric) {
      expression = null;
      variables = new HashMap<>();
      variables.put("result", currentResult);
      this.test = test;
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
         throw new IllegalArgumentException("Error occurred while evaluating the expression.", e);
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

      List<TestExecution> testExecutions = null;
      Double variableValue = null;

      //there is a grouping function and multi-select will follow
      if(DslGroupingFunctions.contains(groupFunctionOrSelect.getText())) {
         DslGroupingFunctions groupingFunction = DslGroupingFunctions.parseString(groupFunctionOrSelect.getText());
         Tree select = groupFunctionOrSelect.getChild(0);

         testExecutions = handleSelect(select);
         if(testExecutions == null) {
            throw new IllegalArgumentException("Error occurred during getting test executions.");
         }

         List<Double> values = getValuesFromTestExecutions(testExecutions);
         variableValue = groupingFunction.compute(values);
      }
      else { //single select
         testExecutions = handleSelect(groupFunctionOrSelect);
         if(testExecutions == null || testExecutions.size() > 2) {
            throw new IllegalArgumentException("Error occurred or there is more than one test execution found, but no grouping function applied.");
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
      if(!select.getText().equalsIgnoreCase("SELECT")) {
         throw new IllegalArgumentException("Wrong syntax, expected SELECT.");
      }

      List<TestExecution> testExecutions = null;
      Tree whereOrLast = select.getChild(0);
      Map<String, Integer> parsedLast = null;

      if(select.getChildCount() == 1 && whereOrLast.getText().equalsIgnoreCase("LAST")) { //SELECT only with LAST
         parsedLast = processLast(whereOrLast);
         testExecutions = testExecutionDAO.getLast(parsedLast.get("lastFrom"), parsedLast.get("howMany"));
      }
      else if(select.getChildCount() == 2 && select.getChild(1).getText().equalsIgnoreCase("LAST")) { //SELECT with WHERE and LAST
         parsedLast = processLast(select.getChild(1));
         //this information about LAST will be used in retrieving of test executions
         //because there is also WHERE clause present
      }

      if(whereOrLast.getText().equalsIgnoreCase("WHERE")) { //WHERE
         Tree simpleOrInOrBetween = whereOrLast.getChild(0);

         if(simpleOrInOrBetween.getText().equalsIgnoreCase("=")) { //WHERE with '='
            String property = simpleOrInOrBetween.getChild(0).getText();
            String propertyValue = simpleOrInOrBetween.getChild(1).getText();

            testExecutions = callActionByPropertyName(property, propertyValue, parsedLast);
         }
         else if(simpleOrInOrBetween.getText().equalsIgnoreCase("IN")) { //IN where
            String propertyName = simpleOrInOrBetween.getChild(0).getText();
            Collection<Object> values = new ArrayList<>();

            for(int i = 1; i < simpleOrInOrBetween.getChildCount(); i++) { //starting from 1 because child(0) is property name
               values.add(simpleOrInOrBetween.getChild(i).getText());
            }

            testExecutions = testExecutionDAO.getAllByPropertyIn(propertyName, values);
         }
         else if(simpleOrInOrBetween.getText().equalsIgnoreCase("BETWEEN")) { //BETWEEN where
            //boundaries for between are stored directly as children
            //AND keyword is omitted from the AST
            String propertyName = simpleOrInOrBetween.getChild(0).getText();
            Comparable from = parseBetweenArgument(simpleOrInOrBetween.getChild(1).getText());
            Comparable to = parseBetweenArgument(simpleOrInOrBetween.getChild(2).getText());

            testExecutions = testExecutionDAO.getAllByPropertyBetween(propertyName, from, to);
         }
         else {
            throw new IllegalArgumentException("Wrong syntax, expected '=', IN or BETWEEN.");
         }
      }

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
      catch(NumberFormatException ex) {} //OK, continue

      try {
         return DATE_FORMAT.parse(argument);
      }
      catch(ParseException e) {} //OK, continue

      return argument;
   }

   /**
    * TODO: document this
    *
    * @param propertyName
    * @param propertyValue
    * @param parsedLast
    * @return
    */
   private List<TestExecution> callActionByPropertyName(String propertyName, String propertyValue, Map<String, Integer> parsedLast) {
      List<TestExecution> testExecutions = null;

      if(propertyName.equalsIgnoreCase("tags")) {
         List<String> tags = Arrays.asList(propertyValue.split(" "));

         if(parsedLast == null) { //LAST is not present, get all
            testExecutions = testExecutionDAO.getTestExecutions(tags, Lists.newArrayList(test.getUid()));
         }
         else { //LAST is present
            testExecutions = testExecutionDAO.getTestExecutions(tags, Lists.newArrayList(test.getUid()), parsedLast.get("lastFrom"), parsedLast.get("howMany"));
         }
      }
      else {
         testExecutions = testExecutionDAO.getAllByProperty(propertyName, propertyValue);
      }

      return testExecutions;
   }

   /**
    * TODO: document this
    *
    * @param last
    * @return
    */
   private Map<String, Integer> processLast(Tree last) {
      Map<String, Integer> result = new HashMap<>();

      if(last.getChildCount() == 1) { // LAST x | it's the same as LAST x, x
         int numberOfLast = Integer.valueOf(last.getChild(0).getText());
         result.put("lastFrom", numberOfLast);
         result.put("howMany", numberOfLast);
      }
      else if(last.getChildCount() == 2) { // LAST x, y
         int lastFrom = Integer.valueOf(last.getChild(0).getText());
         int howMany = Integer.valueOf(last.getChild(1).getText());
         result.put("lastFrom", lastFrom);
         result.put("howMany", howMany);
      }
      else {
         throw new IllegalArgumentException("Wrong syntax, LAST has to have exactly one or two arguments.");
      }

      return result;
   }
}
