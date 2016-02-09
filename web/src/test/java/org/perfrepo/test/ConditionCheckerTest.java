package org.perfrepo.test;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.perfrepo.model.Metric;
import org.perfrepo.model.TestExecution;
import org.perfrepo.model.Value;
import org.perfrepo.model.to.SearchResultWrapper;
import org.perfrepo.model.to.TestExecutionSearchTO;
import org.perfrepo.web.alerting.ConditionCheckerImpl;
import org.perfrepo.web.dao.TestExecutionDAO;
import org.perfrepo.web.service.UserService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link org.perfrepo.web.alerting.ConditionChecker}
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public class ConditionCheckerTest {

   private ConditionCheckerImpl conditionChecker;

   /**
    * Initial preparation for the test, mocking TestExecutionDAO.
    */
   @Before
   public void init() {
      conditionChecker = new ConditionCheckerImpl();

      TestExecutionDAO mockedTestExecutionDAO = mock(TestExecutionDAO.class);
      List<TestExecution> te1 = Arrays.asList(createTestExecution1());
      List<TestExecution> te2 = Arrays.asList(createTestExecution2());

      List<TestExecution> te1And2 = new ArrayList<>();
      te1And2.addAll(te1);
      te1And2.addAll(te2);

      TestExecutionSearchTO searchTe1 = createSearchCriteria(Arrays.asList(1L), null, null, null, null, null);
      TestExecutionSearchTO searchTe2 = createSearchCriteria(Arrays.asList(2L), null, null, null, null, null);
      TestExecutionSearchTO searchTe1And2 = createSearchCriteria(Arrays.asList(1L, 2L), null, null, null, null, null);

      TestExecutionSearchTO searchLast1 = createSearchCriteria(null, null, 2, 1, null, null);
      TestExecutionSearchTO searchLast10 = createSearchCriteria(null, null, 11, 10, null, null);
      TestExecutionSearchTO search5FromLast10 = createSearchCriteria(null, null, 11, 5, null, null);

      when(mockedTestExecutionDAO.searchTestExecutions(searchTe1, Arrays.asList("testuser"))).thenReturn(new SearchResultWrapper<>(te1, 0));
      when(mockedTestExecutionDAO.searchTestExecutions(searchTe2, Arrays.asList("testuser"))).thenReturn(new SearchResultWrapper<>(te2, 0));
      when(mockedTestExecutionDAO.searchTestExecutions(searchLast1, Arrays.asList("testuser"))).thenReturn(new SearchResultWrapper<>(te2, 0));
      when(mockedTestExecutionDAO.searchTestExecutions(searchLast10, Arrays.asList("testuser"))).thenReturn(new SearchResultWrapper<>(te1And2, 0));
      when(mockedTestExecutionDAO.searchTestExecutions(search5FromLast10, Arrays.asList("testuser"))).thenReturn(new SearchResultWrapper<>(te1And2, 0));
      when(mockedTestExecutionDAO.searchTestExecutions(searchTe1And2, Arrays.asList("testuser"))).thenReturn(new SearchResultWrapper<>(te1And2, 0));

      List<TestExecution> tesWithTags = Arrays.asList(createTestExecution3(), createTestExecution4());

      TestExecutionSearchTO searchTesWithTags = createSearchCriteria(null, "firstTag secondTag", null, null, null, null);
      TestExecutionSearchTO searchTesWithTagsAndLast1 = createSearchCriteria(null, "firstTag secondTag", 2, 1, null, null);
      TestExecutionSearchTO searchTesWithTagsAnd2FromLast3 = createSearchCriteria(null, "firstTag secondTag", 4, 2, null, null);

      when(mockedTestExecutionDAO.searchTestExecutions(searchTesWithTags, Arrays.asList("testuser"))).thenReturn(new SearchResultWrapper<>(tesWithTags, 0));
      when(mockedTestExecutionDAO.searchTestExecutions(searchTesWithTagsAndLast1, Arrays.asList("testuser"))).thenReturn(new SearchResultWrapper<>(te1, 0));
      when(mockedTestExecutionDAO.searchTestExecutions(searchTesWithTagsAnd2FromLast3, Arrays.asList("testuser"))).thenReturn(new SearchResultWrapper<>(tesWithTags, 0));

      Calendar calendar = Calendar.getInstance();
      calendar.set(2015, 0, 1, 0, 0, 0);
      Date dateFrom = calendar.getTime();

      calendar.set(2015, 1, 1, 0, 0, 0);
      Date dateTo = calendar.getTime();

      TestExecutionSearchTO searchTesWithOnlyDateFrom = createSearchCriteria(null, null, null, null, dateFrom, null);
      TestExecutionSearchTO searchTesWithOnlyDateTo = createSearchCriteria(null, null, null, null, null, dateTo);
      TestExecutionSearchTO searchTesWithDates = createSearchCriteria(null, null, null, null, dateFrom, dateTo);
      TestExecutionSearchTO searchTesWithTagsAndDates = createSearchCriteria(null, "firstTag secondTag", null, null, dateFrom, dateTo);

      List<TestExecution> tesWithTagsAndDates = Arrays.asList(createTestExecution1(), createTestExecution4());

      when(mockedTestExecutionDAO.searchTestExecutions(argThat(new SearchCriteriaMatcher(searchTesWithOnlyDateFrom)), eq(Arrays.asList("testuser")))).thenReturn(new SearchResultWrapper<>(tesWithTags, 0));
      when(mockedTestExecutionDAO.searchTestExecutions(argThat(new SearchCriteriaMatcher(searchTesWithOnlyDateTo)), eq(Arrays.asList("testuser")))).thenReturn(new SearchResultWrapper<>(tesWithTags, 0));
      when(mockedTestExecutionDAO.searchTestExecutions(argThat(new SearchCriteriaMatcher(searchTesWithDates)), eq(Arrays.asList("testuser")))).thenReturn(new SearchResultWrapper<>(tesWithTagsAndDates, 0));
      when(mockedTestExecutionDAO.searchTestExecutions(argThat(new SearchCriteriaMatcher(searchTesWithTagsAndDates)), eq(Arrays.asList("testuser")))).thenReturn(new SearchResultWrapper<>(tesWithTagsAndDates, 0));

      UserService mockedUserService = mock(UserService.class);
      when(mockedUserService.getLoggedUserGroupNames()).thenReturn(Arrays.asList("testuser"));

      conditionChecker.setTestExecutionDAO(mockedTestExecutionDAO);
      conditionChecker.setUserService(mockedUserService);
   }

   @Test
   public void testWrongSyntax() {
      String condition = "CONDITION x < 1 x = SELECT WHERE id = 1";
      try {
         conditionChecker.checkCondition(condition, 0, createMetric());
         fail("Query without DEFINE should fail.");
      } catch (IllegalArgumentException ex) {} //expected

      condition = "x < 1 DEFINE x = SELECT WHERE id = 1";
      try {
         conditionChecker.checkCondition(condition, 0, createMetric());
         fail("Query without CONDITION should fail.");
      } catch (IllegalArgumentException ex) {} //expected

      condition = "CONDITION x < 1 DEFINE x = (SELECT WHERE id = 1) y = (SELECT WHERE id = 1)";
      try {
         conditionChecker.checkCondition(condition, 0, createMetric());
         fail("Query, where definitions are not separated by comma, should fail.");
      } catch (IllegalArgumentException ex) {} //expected
   }

   @Test(expected = IllegalArgumentException.class)
   public void testWrongSyntaxMultiSelectWithoutGroupFunction() {
      String condition = "CONDITION x == result DEFINE x = (SELECT WHERE id IN (1,2))";
      conditionChecker.checkCondition(condition, 0, createMetric());
   }

   @Test
   public void testSimpleSelect() {
      String condition = "CONDITION x > 10 DEFINE x = (SELECT WHERE id = 1)";
      assertTrue(conditionChecker.checkCondition(condition, 0, createMetric()));

      condition = "CONDITION x < 10 DEFINE x = (SELECT WHERE id = 1)";
      assertFalse(conditionChecker.checkCondition(condition, 0, createMetric()));
   }

   @Test
   public void testSimpleSelectSimpleLast() {
      String condition = "CONDITION x > 10 DEFINE x = (SELECT LAST 1)";
      assertTrue(conditionChecker.checkCondition(condition, 0, createMetric()));

      condition = "CONDITION x < 10 DEFINE x = (SELECT LAST 1)";
      assertFalse(conditionChecker.checkCondition(condition, 0, createMetric()));
   }


   @Test
   public void testSimpleSelectOptionalParentheses() {
      String condition = "CONDITION x > 10 DEFINE x = SELECT WHERE id = 1";
      assertTrue(conditionChecker.checkCondition(condition, 0, createMetric()));

      condition = "CONDITION x < 10 DEFINE x = SELECT WHERE id = 1";
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
   public void testGroupFunctionMin() {
      String condition = "CONDITION x == result DEFINE x = MIN(SELECT WHERE id IN (1,2))";
      assertTrue(conditionChecker.checkCondition(condition, 12, createMetric()));
   }

   @Test
   public void testGroupFunctionMax() {
      String condition = "CONDITION x == result DEFINE x = MAX(SELECT WHERE id IN (1,2))";
      assertTrue(conditionChecker.checkCondition(condition, 100, createMetric()));
   }

   @Test
   public void testSelectWithTags() {
      String condition = "CONDITION x == result DEFINE x = MAX(SELECT WHERE tags = \"firstTag secondTag\")";
      assertTrue(conditionChecker.checkCondition(condition, 1001, createMetric()));
   }

   @Test
   public void testSelectWithTagsAndSimpleLast() {
      String condition = "CONDITION x == result DEFINE x = MAX(SELECT WHERE tags = \"firstTag secondTag\" LAST 1)";
      assertTrue(conditionChecker.checkCondition(condition, 12, createMetric()));
   }

   @Test
   public void testSimpleSelectWithTagsAndSimpleLast() {
      String condition = "CONDITION x == result DEFINE x = (SELECT WHERE tags = \"firstTag secondTag\" LAST 1)";
      assertTrue(conditionChecker.checkCondition(condition, 12, createMetric()));
   }

   @Test
   public void testSelectWithTagsAndMultiLast() {
      String condition = "CONDITION x == result DEFINE x = MAX(SELECT WHERE tags = \"firstTag secondTag\" LAST 3, 2)";
      assertTrue(conditionChecker.checkCondition(condition, 1001, createMetric()));

      condition = "CONDITION x == result DEFINE x = MIN(SELECT WHERE tags = \"firstTag secondTag\" LAST 3, 2)";
      assertTrue(conditionChecker.checkCondition(condition, 150, createMetric()));
   }

   @Test
   public void testSelectWithOnlyDateFrom() {
      String condition = "CONDITION x == result DEFINE x = MAX(SELECT WHERE date >= \"2015-01-01 00:00\")";
      assertTrue(conditionChecker.checkCondition(condition, 1001, createMetric()));

      condition = "CONDITION x == result DEFINE x = MIN(SELECT WHERE date >= \"2015-01-01 00:00\")";
      assertTrue(conditionChecker.checkCondition(condition, 150, createMetric()));
   }

   @Test
   public void testSelectWithOnlyDateTo() {
      String condition = "CONDITION x == result DEFINE x = MAX(SELECT WHERE date <= \"2015-02-01 00:00\")";
      assertTrue(conditionChecker.checkCondition(condition, 1001, createMetric()));

      condition = "CONDITION x == result DEFINE x = MIN(SELECT WHERE date <= \"2015-02-01 00:00\")";
      assertTrue(conditionChecker.checkCondition(condition, 150, createMetric()));
   }

   @Test
   public void testSelectWithDates() {
      String condition = "CONDITION x == result DEFINE x = MAX(SELECT WHERE date >= \"2015-01-01 00:00\" AND date <= \"2015-02-01 00:00\")";
      assertTrue(conditionChecker.checkCondition(condition, 1001, createMetric()));

      condition = "CONDITION x == result DEFINE x = MIN(SELECT WHERE date >= \"2015-01-01 00:00\" AND date <= \"2015-02-01 00:00\")";
      assertTrue(conditionChecker.checkCondition(condition, 12, createMetric()));
   }

   @Test
   public void testSelectWithTagsAndDates() {
      String condition = "CONDITION x == result DEFINE x = MAX(SELECT WHERE tags = \"firstTag secondTag\" AND date >= \"2015-01-01 00:00\" AND date <= \"2015-02-01 00:00\")";
      assertTrue(conditionChecker.checkCondition(condition, 1001, createMetric()));

      condition = "CONDITION x == result DEFINE x = MIN(SELECT WHERE tags = \"firstTag secondTag\" AND date >= \"2015-01-01 00:00\" AND date <= \"2015-02-01 00:00\")";
      assertTrue(conditionChecker.checkCondition(condition, 12, createMetric()));
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

   private org.perfrepo.model.Test createTest() {
      org.perfrepo.model.Test test = new org.perfrepo.model.Test();
      test.setId(1L);
      test.setUid("test_test");

      return test;
   }

   private Metric createMetric() {
      Metric metric = new Metric();
      metric.setName("metric1");

      return metric;
   }

   private TestExecutionSearchTO createSearchCriteria(List<Long> ids, String tags, Integer limitFrom, Integer limitHowMany, Date dateFrom, Date dateTo) {
      TestExecutionSearchTO searchCriteria = new TestExecutionSearchTO();
      searchCriteria.setIds(ids);
      searchCriteria.setTags(tags);
      searchCriteria.setLimitFrom(limitFrom);
      searchCriteria.setLimitHowMany(limitHowMany);
      searchCriteria.setStartedFrom(dateFrom);
      searchCriteria.setStartedTo(dateTo);

      return searchCriteria;
   }

   /**
    * Helper class. When mocking objects with Mockito, Mockito by default uses Object.equals method to distinguish which
    * method to call. There is a problem with Date objects. Even is two Date objects are created with the same Date
    * (like year, month, day, hour and minute), calling equals returns false. This is the reason why this
    * ArgumentMatcher class is used, to do the matching manually.
    */
   private class SearchCriteriaMatcher extends ArgumentMatcher<TestExecutionSearchTO> {

      private TestExecutionSearchTO searchCriteria;

      public SearchCriteriaMatcher(TestExecutionSearchTO searchCriteria) {
         this.searchCriteria = searchCriteria;
      }

      @Override
      public boolean matches(Object o) {
         if (!(o instanceof TestExecutionSearchTO)) {
            return false;
         }

         TestExecutionSearchTO object = (TestExecutionSearchTO) o;

         if (!((searchCriteria.getTags() == null && object.getTags() == null) ||
                   (searchCriteria.getTags() != null && object.getTags() != null))) {
            return false;
         }

         Date otherDateFrom = object.getStartedFrom();
         Date otherDateTo = object.getStartedTo();

         if (!((searchCriteria.getStartedFrom() == null && otherDateFrom == null) ||
                   (searchCriteria.getStartedFrom() != null && otherDateFrom != null))) {
            return false;
         }

         if (!((searchCriteria.getStartedTo() == null && otherDateTo == null) ||
                   (searchCriteria.getStartedTo() != null && otherDateTo != null))) {
            return false;
         }

         Calendar thisCalendar = Calendar.getInstance();
         Calendar otherCalendar = Calendar.getInstance();

         if (searchCriteria.getStartedFrom() != null) {
            thisCalendar.setTime(searchCriteria.getStartedFrom());
            otherCalendar.setTime(otherDateFrom);

            if ((thisCalendar.get(Calendar.YEAR) != otherCalendar.get(Calendar.YEAR)) ||
                (thisCalendar.get(Calendar.MONTH) != otherCalendar.get(Calendar.MONTH)) ||
                (thisCalendar.get(Calendar.DAY_OF_MONTH) != otherCalendar.get(Calendar.DAY_OF_MONTH)) ||
                (thisCalendar.get(Calendar.HOUR) != otherCalendar.get(Calendar.HOUR)) ||
                (thisCalendar.get(Calendar.MINUTE) != otherCalendar.get(Calendar.MINUTE))) {
               return false;
            }
         }

         if (searchCriteria.getStartedTo() != null) {
            thisCalendar.setTime(searchCriteria.getStartedTo());
            otherCalendar.setTime(otherDateTo);

            if ((thisCalendar.get(Calendar.YEAR) != otherCalendar.get(Calendar.YEAR)) ||
                (thisCalendar.get(Calendar.MONTH) != otherCalendar.get(Calendar.MONTH)) ||
                (thisCalendar.get(Calendar.DAY_OF_MONTH) != otherCalendar.get(Calendar.DAY_OF_MONTH)) ||
                (thisCalendar.get(Calendar.HOUR) != otherCalendar.get(Calendar.HOUR)) ||
                (thisCalendar.get(Calendar.MINUTE) != otherCalendar.get(Calendar.MINUTE))) {
               return false;
            }
         }

         if (searchCriteria.getTags() != null && !searchCriteria.getTags().equals(object.getTags())) {
            return false;
         }

         return true;
      }
   }
}
