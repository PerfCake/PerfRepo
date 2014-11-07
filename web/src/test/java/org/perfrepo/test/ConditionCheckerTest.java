package org.perfrepo.test;

import org.perfrepo.model.Metric;
import org.perfrepo.model.TestExecution;
import org.perfrepo.model.Value;
import org.perfrepo.web.alerting.ConditionCheckerImpl;
import org.junit.Before;
import org.junit.Test;
import org.perfrepo.web.dao.TestExecutionDAO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link org.perfrepo.web.alerting.ConditionChecker}
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 *
 */
public class ConditionCheckerTest {

   private ConditionCheckerImpl conditionChecker;

   @Before
   public void init() {
      conditionChecker = new ConditionCheckerImpl();

      TestExecutionDAO mockedTestExecutionDAO = mock(TestExecutionDAO.class);
      List<TestExecution> returnedTestExecutionsFor1 = new ArrayList<>();
      returnedTestExecutionsFor1.add(createTestExecution1());

      List<TestExecution> returnedTestExecutionsFor2 = new ArrayList<>();
      returnedTestExecutionsFor2.add(createTestExecution2());

      List<TestExecution> returnedTestExecutionsForMultiSelect = new ArrayList<>();
      returnedTestExecutionsForMultiSelect.addAll(returnedTestExecutionsFor1);
      returnedTestExecutionsForMultiSelect.addAll(returnedTestExecutionsFor2);

      //number 1 is of type string because when we extract it from a AST, it's a string
      //and Mockito needs the parameters type to match, otherwise doesn't retrieve correct result
      when(mockedTestExecutionDAO.getAllByProperty("id", "1")).thenReturn(returnedTestExecutionsFor1);
      when(mockedTestExecutionDAO.getAllByProperty("id", "2")).thenReturn(returnedTestExecutionsFor2);
      when(mockedTestExecutionDAO.getLast(10)).thenReturn(returnedTestExecutionsForMultiSelect);
      when(mockedTestExecutionDAO.getLast(10, 5)).thenReturn(returnedTestExecutionsForMultiSelect);

      Collection<Object> inIds = new ArrayList<>();
      inIds.add("1");
      inIds.add("2");
      when(mockedTestExecutionDAO.getAllByPropertyIn("id", inIds)).thenReturn(returnedTestExecutionsForMultiSelect);

      List<TestExecution> returnedTestExecutionsForBetween = new ArrayList<>();
      returnedTestExecutionsForBetween.add(createTestExecution2());
      returnedTestExecutionsForBetween.add(createTestExecution3());
      returnedTestExecutionsForBetween.add(createTestExecution4());
      when(mockedTestExecutionDAO.getAllByPropertyBetween("id", 2L, 4L)).thenReturn(returnedTestExecutionsForBetween);

      conditionChecker.setTestExecutionDAO(mockedTestExecutionDAO);
   }

   @Test
   public void testWrongSyntax() {
      String condition = "CONDITION x < 1 x = SELECT WHERE id = 1";
      try {
         conditionChecker.checkCondition(condition, 0, createMetric());
         fail("Query without DEFINE should fail.");
      }
      catch (IllegalArgumentException ex) {} //expected

      condition = "x < 1 DEFINE x = SELECT WHERE id = 1";
      try {
         conditionChecker.checkCondition(condition, 0, createMetric());
         fail("Query without CONDITION should fail.");
      }
      catch (IllegalArgumentException ex) {} //expected
   }

   @Test
   public void testSimpleSelect() {
      String condition = "CONDITION x > 10 DEFINE x = (SELECT WHERE id = 1)";
      assertTrue(conditionChecker.checkCondition(condition, 0, createMetric()));

      condition = "CONDITION x < 10 DEFINE x = (SELECT WHERE id = 1)";
      assertFalse(conditionChecker.checkCondition(condition, 0, createMetric()));
   }

   @Test
   public void testManySimpleSelects() {
      String condition = "CONDITION x > 10 && result > 0.95*y DEFINE x = (SELECT WHERE id = 1), y = (SELECT WHERE id = 2)";
      assertTrue(conditionChecker.checkCondition(condition, 100, createMetric()));

      condition = "CONDITION x > 10 && result > (0.95*y)+10 DEFINE x = (SELECT WHERE id = 1), y = (SELECT WHERE id = 2)";
      assertFalse(conditionChecker.checkCondition(condition, 100, createMetric()));
   }

   @Test
   public void testMultiSelectSimpleLast() {
      String condition = "CONDITION x == result DEFINE x = AVG(SELECT LAST 10)";
      assertTrue(conditionChecker.checkCondition(condition, 56, createMetric()));
   }

   @Test
   public void testMultiSelectIntervalLast() {
      String condition = "CONDITION x == result DEFINE x = AVG(SELECT LAST 10, 5)";
      assertTrue(conditionChecker.checkCondition(condition, 56, createMetric()));
   }

   @Test
   public void testMultiSelectInWhere() {
      String condition = "CONDITION x == result DEFINE x = AVG(SELECT WHERE id IN (1,2))";
      assertTrue(conditionChecker.checkCondition(condition, 56, createMetric()));
   }

   @Test
   public void testMultiSelectBetweenWhereId() {
      String condition = "CONDITION x == result DEFINE x = AVG(SELECT WHERE id BETWEEN 2 AND 4)";
      assertTrue(conditionChecker.checkCondition(condition, 417, createMetric()));
   }

   @Test
   public void testGroupFunctionMin() {
      String condition = "CONDITION x == result DEFINE x = MIN(SELECT WHERE id IN (1,2))";
      assertTrue(conditionChecker.checkCondition(condition, 12, createMetric()));
   }

   @Test
   public void testGroupFunctionMax() {
      String condition = "CONDITION x == result DEFINE x = MAX(SELECT WHERE id IN (1,2))";
      assertTrue(conditionChecker.checkCondition(condition, 100, createMetric()));
   }

   private TestExecution createTestExecution1() {
      Value value = new Value();
      value.setResultValue(12d);
      value.setMetric(createMetric());

      Collection<Value> values = new ArrayList<>();
      values.add(value);

      TestExecution te = new TestExecution();
      te.setValues(values);

      return te;
   }

   private TestExecution createTestExecution2() {
      Value value = new Value();
      value.setResultValue(100d);
      value.setMetric(createMetric());

      Collection<Value> values = new ArrayList<>();
      values.add(value);

      TestExecution te = new TestExecution();
      te.setValues(values);

      return te;
   }

   private TestExecution createTestExecution3() {
      Value value = new Value();
      value.setResultValue(150d);
      value.setMetric(createMetric());

      Collection<Value> values = new ArrayList<>();
      values.add(value);

      TestExecution te = new TestExecution();
      te.setValues(values);

      return te;
   }

   private TestExecution createTestExecution4() {
      Value value = new Value();
      value.setResultValue(1001d);
      value.setMetric(createMetric());

      Collection<Value> values = new ArrayList<>();
      values.add(value);

      TestExecution te = new TestExecution();
      te.setValues(values);

      return te;
   }

   private Metric createMetric() {
      Metric metric = new Metric();
      metric.setName("metric1");

      return metric;
   }

}
