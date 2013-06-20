package org.jboss.qa.perfrepo.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.jboss.qa.perfrepo.dao.MetricDAO;
import org.jboss.qa.perfrepo.dao.TagDAO;
import org.jboss.qa.perfrepo.dao.TestDAO;
import org.jboss.qa.perfrepo.dao.TestExecutionAttachmentDAO;
import org.jboss.qa.perfrepo.dao.TestExecutionDAO;
import org.jboss.qa.perfrepo.dao.TestExecutionParameterDAO;
import org.jboss.qa.perfrepo.dao.TestExecutionTagDAO;
import org.jboss.qa.perfrepo.dao.TestMetricDAO;
import org.jboss.qa.perfrepo.dao.ValueDAO;
import org.jboss.qa.perfrepo.dao.ValueParameterDAO;
import org.jboss.qa.perfrepo.model.Metric;
import org.jboss.qa.perfrepo.model.Tag;
import org.jboss.qa.perfrepo.model.Test;
import org.jboss.qa.perfrepo.model.TestExecution;
import org.jboss.qa.perfrepo.model.TestExecutionAttachment;
import org.jboss.qa.perfrepo.model.TestExecutionParameter;
import org.jboss.qa.perfrepo.model.TestExecutionTag;
import org.jboss.qa.perfrepo.model.TestMetric;
import org.jboss.qa.perfrepo.model.Value;
import org.jboss.qa.perfrepo.model.ValueParameter;
import org.jboss.qa.perfrepo.model.to.TestExecutionSearchTO;
import org.jboss.qa.perfrepo.model.to.TestSearchTO;
import org.jboss.qa.perfrepo.security.Secure;
import org.jboss.qa.perfrepo.security.UserInfo;

/**
 * 
 * Implements {@link TestService}.
 * 
 * @author Pavel Drozd (pdrozd@redhat.com)
 * @author Michal Linhard (mlinhard@redhat.com)
 * 
 */
@Named
@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class TestServiceBean implements TestService {

   private static final Logger log = Logger.getLogger(TestService.class);

   @Inject
   private TestDAO testDAO;

   @Inject
   private TestExecutionDAO testExecutionDAO;

   @Inject
   private TestExecutionParameterDAO testExecutionParameterDAO;

   @Inject
   private TestExecutionAttachmentDAO testExecutionAttachmentDAO;

   @Inject
   private TagDAO tagDAO;

   @Inject
   private TestExecutionTagDAO testExecutionTagDAO;

   @Inject
   private ValueDAO valueDAO;

   @Inject
   private ValueParameterDAO valueParameterDAO;

   @Inject
   private MetricDAO metricDAO;

   @Inject
   private TestMetricDAO testMetricDAO;

   @Inject
   private UserInfo userInfo;

   @Override
   public TestExecution createTestExecution(TestExecution testExecution) throws ServiceException {
      // The test referred by test execution has to be an existing test
      Test test = checkUserCanChangeTest(testExecution.getTest());
      testExecution.setTest(test);
      TestExecution storedTestExecution = testExecutionDAO.create(testExecution);
      // execution params
      if (testExecution.getParameters() != null && testExecution.getParameters().size() > 0) {
         for (TestExecutionParameter param : testExecution.getParameters()) {
            param.setTestExecution(storedTestExecution);
            testExecutionParameterDAO.create(param);
         }
      }
      // tags
      if (testExecution.getTestExecutionTags() != null && testExecution.getTestExecutionTags().size() > 0) {
         for (TestExecutionTag teg : testExecution.getTestExecutionTags()) {
            Tag tag = tagDAO.findByName(teg.getTag().getName());
            if (tag == null) {
               tag = tagDAO.create(teg.getTag());
            }
            teg.setTag(tag);
            teg.setTestExecution(storedTestExecution);
            testExecutionTagDAO.create(teg);
         }
      }
      // values
      if (testExecution.getValues() != null && !testExecution.getValues().isEmpty()) {
         // TODO: Don't allow duplicit value for a metric
         for (Value value : testExecution.getValues()) {
            value.setTestExecution(storedTestExecution);
            if (value.getMetricName() == null) {
               throw serviceException("Metric name is mandatory");
            }
            Metric metric = metricDAO.findByName(value.getMetricName());
            if (metric == null) {
               throw serviceException("Referring to a non-existent metric: \"%s\"", value.getMetricName());
            }
            if (!testHasMetric(test, metric)) {
               throw serviceException("Metric \"%s\" (%s) doesn't belong to test \"%s\" (%s)", metric.getName(), metric.getId(), test.getName(), test.getId());
            }
            value.setMetric(metric);
            valueDAO.create(value);
            if (value.getParameters() != null && value.getParameters().size() > 0) {
               for (ValueParameter vp : value.getParameters()) {
                  vp.setValue(value);
                  valueParameterDAO.create(vp);
               }
            }
         }
      }
      log.debug("Created new test execution " + storedTestExecution.getId());
      return storedTestExecution;
   }

   private boolean testHasMetric(Test test, Metric metric) {
      return testMetricDAO.find(test, metric) != null;
   }

   public List<TestExecution> getTestExecutions(Collection<Long> ids) {
      List<TestExecution> result = new ArrayList<TestExecution>();
      for (Long id : ids) {
         TestExecution testExecution = testExecutionDAO.getFullTestExecution(id);
         result.add(testExecution);
      }
      return result;
   }
   
   
   public List<Test> searchTest(TestSearchTO search) {
      return testDAO.searchTests(search);
   }

   public List<TestExecution> searchTestExecutions(TestExecutionSearchTO search) {
      List<TestExecution> result = testExecutionDAO.searchTestExecutions(search);
      return result;
   }

   @Override
   public Long addAttachment(TestExecutionAttachment attachment) throws ServiceException {
      TestExecution testExecution = testExecutionDAO.get(attachment.getTestExecution().getId());
      if (testExecution == null) {
         throw serviceException("Trying to add attachment to non-existent test execution (id=%s)", attachment.getTestExecution().getId());
      }
      checkUserCanChangeTest(testExecution.getTest());
      attachment.setTestExecution(testExecution);
      TestExecutionAttachment newAttachment = testExecutionAttachmentDAO.create(attachment);
      return newAttachment.getId();
   }

   @Override
   public TestExecutionAttachment getAttachment(Long id) {
      return testExecutionAttachmentDAO.get(id);
   }

   private SecurityException notInGroup(String userName, String groupId, String test, String testUid) {
      return new SecurityException(String.format("User %s is not in group %s that owns the test %s (uid=%s)", userName, groupId, test, testUid));
   }

   private SecurityException cantCreateTest(String userName, String groupId) {
      return new SecurityException(String.format("User %s is not in group %s. Can't create test with group id that you're not member of.", userName, groupId));
   }

   private ServiceException serviceException(String msg, Object... args) {
      return new ServiceException(String.format(msg, args));
   }

   @Override
   public Test createTest(Test test) {
      if (!userInfo.isUserInRole(test.getGroupId())) {
         throw cantCreateTest(userInfo.getUserName(), test.getGroupId());
      }
      Test createdTest = testDAO.create(test);
      //store metrics
      if (test.getTestMetrics() != null && test.getTestMetrics().size() > 0) {
         for (TestMetric tm : test.getTestMetrics()) {
            Metric metric = addMetric(test, tm.getMetric());
            tm.setMetric(metric);
            tm.setTest(createdTest);
            testMetricDAO.create(tm);
         }
      }
      return createdTest;
   }

   public Test getOrCreateTest(Test test) {
      Test storedTest = testDAO.findByUid(test.getUid());
      if (storedTest == null) {
         storedTest = createTest(test);
      }
      return storedTest;
   }

   public Test getTest(Long id) {
      Test test = testDAO.get(id);
      Collection<TestMetric> tms = test.getTestMetrics();
      for (TestMetric tm : tms) {
         tm.getMetric();
      }
      return test;
   }

   public List<Test> findAllTests() {
      return testDAO.findAll();
   }

   @Secure
   public Test updateTest(Test test) {
      return testDAO.update(test);
   }

   public void deleteTest(Test test) throws ServiceException {
      Test freshTest = checkUserCanChangeTest(test);
      for (TestExecution testExecution : freshTest.getTestExecutions()) {
         deleteTestExecution(testExecution);
      }
      Iterator<TestMetric> allTestMetrics = freshTest.getTestMetrics().iterator();
      while (allTestMetrics.hasNext()) {
         TestMetric testMetric = allTestMetrics.next();
         Metric metric = testMetric.getMetric();
         List<Test> testsUsingMetric = testDAO.findByNamedQuery(Test.FIND_TESTS_USING_METRIC,
               Collections.<String, Object> singletonMap("metric", metric.getId()));
         allTestMetrics.remove();
         testMetricDAO.delete(testMetric);
         if (testsUsingMetric.size() == 0) {
            throw new IllegalStateException();
         } else if (testsUsingMetric.size() == 1) {
            if (testsUsingMetric.get(0).getId().equals(test.getId())) {
               metricDAO.delete(metric);
            } else {
               throw new IllegalStateException();
            }
         }
      }
      testDAO.delete(freshTest);
   }

   public void deleteTestExecution(TestExecution testExecution) throws ServiceException {
      TestExecution freshTestExecution = testExecutionDAO.get(testExecution.getId());
      if (freshTestExecution == null) {
         throw serviceException("Test execution with id %s doesn't exist", testExecution.getId());
      }
      checkUserCanChangeTest(freshTestExecution.getTest());
      for (TestExecutionParameter testExecutionParameter : freshTestExecution.getParameters()) {
         testExecutionParameterDAO.delete(testExecutionParameter);
      }
      for (Value value : freshTestExecution.getValues()) {
         for (ValueParameter valueParameter : value.getParameters()) {
            valueParameterDAO.delete(valueParameter);
         }
         valueDAO.delete(value);
      }
      Iterator<TestExecutionTag> allTestExecutionTags = freshTestExecution.getTestExecutionTags().iterator();
      while (allTestExecutionTags.hasNext()) {
         testExecutionTagDAO.delete(allTestExecutionTags.next());
         allTestExecutionTags.remove();
      }
      Iterator<TestExecutionAttachment> allTestExecutionAttachments = freshTestExecution.getAttachments().iterator();
      while (allTestExecutionAttachments.hasNext()) {
         testExecutionAttachmentDAO.delete(allTestExecutionAttachments.next());
         allTestExecutionAttachments.remove();
      }
      testExecutionDAO.delete(freshTestExecution);
   }

   public Metric getMetric(Long id) {
      return metricDAO.get(id);
   }

   public Metric addMetric(Test test, Metric metric) {
      //Test existingTest = testDAO.get(test.getId());
      Metric m = metricDAO.findByName(metric.getName());
      if (m == null) {
         m = metricDAO.create(metric);
      }
      return m;
   }

   @Secure
   public Metric updateMetric(Metric metric) {
      return metricDAO.update(metric);
   }
   
   public List<Metric> getTestMetrics(Test test) {
      Test t = testDAO.get(test.getId());
      return t.getSortedMetrics();
   }

   public List<Metric> getAllMetrics() {
      return metricDAO.getMetrics();
   }

   @Secure
   public void deleteMetric(Metric metric) {
      Metric m = metricDAO.get(metric.getId());
      metricDAO.delete(m);
   }
   
   public void deleteTestMetric(Test test, Metric metric) {
      TestMetric tm = testMetricDAO.find(test, metric);
      testMetricDAO.delete(tm);
   }

   @Override
   public TestExecution getTestExecution(Long id) {
      TestExecution testExecution = testExecutionDAO.getFullTestExecution(id);
      // fetch lazy collections
      testExecution.getParametersAsMap();
      //testExecution.getValuesAsMap();
      testExecution.getSortedTags();
      return testExecution;
   }

   @Override
   public List<TestExecution> findAllTestExecutions() {
      return testExecutionDAO.findAll();
   }

   @Override
   public List<TestExecution> findExecutionsByTest(Long testId) {
      return testExecutionDAO.findByTest(testId);
   }

   private Test checkUserCanChangeTest(Test test) throws ServiceException {
      // The test referred by test execution has to be an existing test
      if (test == null) {
         throw new NullPointerException("test");
      }
      Test freshTest = null;
      if (test.getId() != null) {
         freshTest = testDAO.get(test.getId());
         if (freshTest == null) {
            throw serviceException("Test with id=%s, doesn't exist.", test.getId());
         }
      } else if (test.getUid() != null) {
         freshTest = testDAO.findByUid(test.getUid());
         if (freshTest == null) {
            throw serviceException("Test with uid=%s, doesn't exist.", test.getUid());
         }
      } else {
         throw serviceException("Can't find test, id or uid needs to be supplied");
      }
      // user can only insert test executions for tests pertaining to his group
      if (!userInfo.isUserInRole(freshTest.getGroupId())) {
         throw notInGroup(userInfo.getUserName(), freshTest.getGroupId(), freshTest.getName(), freshTest.getUid());
      }
      return freshTest;
   }

   public TestExecution updateTestExecution(TestExecution testExecution) throws ServiceException {
      checkUserCanChangeTest(testExecution.getTest());
      return testExecutionDAO.update(testExecution);
   }
   
   public TestExecutionParameter addTestExecutionParameter(TestExecution te, TestExecutionParameter tep) throws ServiceException {
      TestExecution freshTE = testExecutionDAO.get(te.getId());
      tep.setTestExecution(freshTE);
      return testExecutionParameterDAO.create(tep);
   }
   
   public TestExecutionParameter getTestExecutionParameter(Long id) {
      return testExecutionParameterDAO.get(id);
   }
   
   public TestExecutionParameter updateTestExecutionParameter(TestExecutionParameter tep) {
      tep.setTestExecution(testExecutionDAO.get(tep.getTestExecution().getId()));
      return testExecutionParameterDAO.update(tep);
   }
   
   public void deleteTestExecutionParameter(TestExecutionParameter tep) {
      TestExecutionParameter tepRemove = testExecutionParameterDAO.get(tep.getId());
      testExecutionParameterDAO.delete(tepRemove);      
   }   
   
   public TestExecutionTag addTestExecutionTag(TestExecution te, TestExecutionTag teg) throws ServiceException {
      TestExecution freshTE = testExecutionDAO.get(te.getId());
      Tag tag = tagDAO.findByName(teg.getTag().getName());
      if (tag == null) {
         tag = tagDAO.create(teg.getTag());
      }
      teg.setTestExecution(freshTE);
      teg.setTag(tag);
      return testExecutionTagDAO.create(teg);
   }
 
   public void deleteTestExecutionTag(TestExecutionTag teg) {
      TestExecutionTag tegRemove = testExecutionTagDAO.get(teg.getId());
      testExecutionTagDAO.delete(tegRemove);
   }
   
   
   public ValueParameter addValueParameter(Value value, ValueParameter vp) {
      Value freshValue = valueDAO.get(value.getId());
      vp.setValue(freshValue);
      return valueParameterDAO.create(vp);
   }
   
   
   public ValueParameter updateValueParameter(ValueParameter vp) {
      vp.setValue(valueDAO.get(vp.getValue().getId()));
      return valueParameterDAO.update(vp);
   }
   
   
   public void deleteValueParameter(ValueParameter vp) {
      ValueParameter freshVP = valueParameterDAO.get(vp.getId());
      valueParameterDAO.delete(freshVP);
   }
   
   public Value getValue(Long id) {
      return valueDAO.getValue(id);
   }
   
   
   public Value addValue(TestExecution te, Value value) {
      TestExecution freshTestExecution = testExecutionDAO.get(te.getId());
      Metric metric = metricDAO.get(value.getMetric().getId());
      value.setTestExecution(freshTestExecution);
      value.setMetric(metric);
      return valueDAO.create(value);
   }
   
   public Value updateValue(Value value) {
      TestExecution freshTestExecution = testExecutionDAO.get(value.getTestExecution().getId());
      value.setTestExecution(freshTestExecution);
      return valueDAO.update(value);
   }
      
   
   
   public void deleteValue(Value value) {
      Value v = valueDAO.get(value.getId());
      for (ValueParameter vp: v.getParameters()) {
         valueParameterDAO.delete(vp);
      }      
      valueDAO.delete(v);
   }
   
   
   
}
