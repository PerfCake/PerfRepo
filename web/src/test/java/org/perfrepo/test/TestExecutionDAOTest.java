package org.perfrepo.test;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.perfrepo.model.Entity;
import org.perfrepo.model.Metric;
import org.perfrepo.model.MetricComparator;
import org.perfrepo.model.Test;
import org.perfrepo.model.TestExecution;
import org.perfrepo.model.Value;
import org.perfrepo.web.dao.DAO;
import org.perfrepo.web.dao.TestDAO;
import org.perfrepo.web.dao.TestExecutionDAO;

import javax.inject.Inject;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests for {@link org.perfrepo.web.dao.TestExecutionDAO}
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 *
 */
@RunWith(Arquillian.class)
public class TestExecutionDAOTest {

   @Inject
   private TestExecutionDAO testExecutionDao;

   @Inject
   private TestDAO testDAO;

   @Inject
   private UserTransaction userTransaction;

   private Test test;
   private TestExecution te1;
   private TestExecution te2;
   private TestExecution te3;
   private TestExecution te4;
   private Calendar calendar = Calendar.getInstance();


   @Deployment
   public static Archive<?> createDeployment() {
      WebArchive war = ShrinkWrap.create(WebArchive.class, "test.war");
      war.addPackages(true, DAO.class.getPackage());
      war.addPackages(true, Entity.class.getPackage());
      war.addAsResource("test-persistence.xml", "META-INF/persistence.xml");
      war.addAsWebInfResource(EmptyAsset.INSTANCE, ArchivePaths.create("beans.xml"));
      return war;
   }

   @Before
   public void init() throws SystemException, NotSupportedException {
      userTransaction.begin();

      test = testDAO.create(createTest());
      te1 = testExecutionDao.create(createTestExecution1());
      te2 = testExecutionDao.create(createTestExecution2());
      te3 = testExecutionDao.create(createTestExecution3());
      te4 = testExecutionDao.create(createTestExecution4());

      assertEquals(4, testExecutionDao.getAll().size());
   }

   @After
   public void destroy() throws HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException {
      testExecutionDao.remove(te1);
      testExecutionDao.remove(te2);
      testExecutionDao.remove(te3);
      testExecutionDao.remove(te4);

      assertTrue(testExecutionDao.getAll().isEmpty());

      testDAO.remove(test);
      userTransaction.commit();
   }

   @org.junit.Test
   public void testGetLastSimple() {
      List<TestExecution> result = testExecutionDao.getLast(2);
      assertEquals(2, result.size());

      for(TestExecution testExecution: result) {
         if(testExecution.getId() != te4.getId() && testExecution.getId() != te3.getId()) {
            fail("TestExecutionDAO.getLast(int) returned unexpected test execution.");
         }
      }
   }

   @org.junit.Test
   public void testGetLastRange() {
      List<TestExecution> result = testExecutionDao.getLast(3,2);
      assertEquals(2, result.size());

      for(TestExecution testExecution: result) {
         if(testExecution.getId() != te2.getId() && testExecution.getId() != te3.getId()) {
            fail("TestExecutionDAO.getLast(int, int) returned unexpected test execution.");
         }
      }
   }

   @org.junit.Test
   public void testGetAllByPropertyIn() {
      Collection<Object> ids = new ArrayList<>();
      ids.add(te1.getId());
      ids.add(te4.getId());

      List<TestExecution> result = testExecutionDao.getAllByPropertyIn("id", ids);
      assertEquals(2, result.size());

      for(TestExecution testExecution: result) {
         if(testExecution.getId() != te1.getId() && testExecution.getId() != te4.getId()) {
            fail("TestExecutionDAO.getAllByPropertyIn returned unexpected test execution.");
         }
      }
   }

   @org.junit.Test
   public void testGetAllByPropertyIdBetween() {
      Comparable from = te2.getId();
      Comparable to = te4.getId();
      List<TestExecution> result = testExecutionDao.getAllByPropertyBetween("id", from, to);
      assertEquals(3, result.size());

      for(TestExecution testExecution: result) {
         if(testExecution.getId() != te2.getId() && testExecution.getId() != te3.getId() && testExecution.getId() != te4.getId()) {
            fail("TestExecutionDAO.getAllByPropertyIn returned unexpected test execution.");
         }
      }
   }

   @org.junit.Test
   public void testGetAllByPropertyDateBetween() {
      Comparable from = createStartDate(-7);
      Comparable to = createStartDate(-3);
      List<TestExecution> result = testExecutionDao.getAllByPropertyBetween("started", from, to);
      assertEquals(2, result.size());

      for(TestExecution testExecution: result) {
         if(testExecution.getId() != te2.getId() && testExecution.getId() != te3.getId()) {
            fail("TestExecutionDAO.getAllByPropertyIn returned unexpected test execution.");
         }
      }
   }

   private Test createTest() {
      return Test.builder()
            .name("test1")
            .groupId("perfrepouser")
            .uid("uid")
            .description("this is a test test")
            .metric("metric1", MetricComparator.HB, "this is a test metric 1")
            .build();
   }

   private TestExecution createTestExecution1() {
      Value value = new Value();
      value.setResultValue(10d);
      value.setMetric(createMetric());

      Collection<Value> values = new ArrayList<>();
      values.add(value);

      TestExecution te = new TestExecution();
      te.setStarted(createStartDate(-8));
      te.setName("test execution 1");
      te.setTest(test);
      te.setValues(values);

      return te;
   }

   private TestExecution createTestExecution2() {
      Value value = new Value();
      value.setResultValue(20d);
      value.setMetric(createMetric());

      Collection<Value> values = new ArrayList<>();
      values.add(value);

      TestExecution te = new TestExecution();
      te.setStarted(createStartDate(-6));
      te.setName("test execution 2");
      te.setTest(test);
      te.setValues(values);

      return te;
   }

   private TestExecution createTestExecution3() {
      Value value = new Value();
      value.setResultValue(30d);
      value.setMetric(createMetric());

      Collection<Value> values = new ArrayList<>();
      values.add(value);

      TestExecution te = new TestExecution();
      te.setStarted(createStartDate(-4));
      te.setName("test execution 3");
      te.setTest(test);
      te.setValues(values);

      return te;
   }

   private TestExecution createTestExecution4() {
      Value value = new Value();
      value.setResultValue(40d);
      value.setMetric(createMetric());

      Collection<Value> values = new ArrayList<>();
      values.add(value);

      TestExecution te = new TestExecution();
      te.setStarted(createStartDate(-2));
      te.setName("test execution 4");
      te.setTest(test);
      te.setValues(values);

      return te;
   }

   private static Metric createMetric() {
      Metric metric = new Metric();
      metric.setName("metric1");

      return metric;
   }

   private Date createStartDate(int daysToShift) {
      calendar.setTime(new Date());
      calendar.add(Calendar.DATE, daysToShift);

      return calendar.getTime();
   }
}
