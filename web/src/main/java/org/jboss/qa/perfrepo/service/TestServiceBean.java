package org.jboss.qa.perfrepo.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.inject.Named;

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
import org.jboss.qa.perfrepo.model.TestExecutionSearchTO;
import org.jboss.qa.perfrepo.model.TestExecutionTag;
import org.jboss.qa.perfrepo.model.TestMetric;
import org.jboss.qa.perfrepo.model.Value;
import org.jboss.qa.perfrepo.model.ValueParameter;
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
public class TestServiceBean implements TestService {

   @Inject
   TestDAO testDAO;

   @Inject
   TestExecutionDAO testExecutionDAO;

   @Inject
   TestExecutionParameterDAO testExecutionParameterDAO;

   @Inject
   TestExecutionAttachmentDAO testExecutionAttachmentDAO;

   @Inject
   TagDAO tagDAO;

   @Inject
   TestExecutionTagDAO testExecutionTagDAO;

   @Inject
   ValueDAO valueDAO;

   @Inject
   ValueParameterDAO valueParameterDAO;

   @Inject
   MetricDAO metricDAO;

   @Inject
   TestMetricDAO testMetricDAO;

   @Inject
   UserInfo userInfo;

   @Override
   public TestExecution storeTestExecution(TestExecution testExecution) throws ServiceException {
      // The test referred by test execution has to be an existing test
      Test test = checkUserCanChangeTest(testExecution.getTest());
      testExecution.setTest(test);
      TestExecution storedTestExecution = testExecutionDAO.create(testExecution);
      // execution params
      if (testExecution.getTestExecutionParameters() != null && testExecution.getTestExecutionParameters().size() > 0) {
         for (TestExecutionParameter param : testExecution.getTestExecutionParameters()) {
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
            if (value.getValueParameters() != null && value.getValueParameters().size() > 0) {
               for (ValueParameter vp : value.getValueParameters()) {
                  vp.setValue(value);
                  valueParameterDAO.create(vp);
               }
            }
         }
      }
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
      Test test = checkUserCanChangeTest(testExecution.getTest());
      // reload test execution to check whether it exists
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

   private ServiceException serviceException(String msg, Object... args) {
      return new ServiceException(String.format(msg, args));
   }

   @Override
   public Test createTest(Test test) {
      //TODO: set guid
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
      return testDAO.get(id);
   }

   public List<Test> findAllTests() {
      return testDAO.findAll();
   }

   @Secure
   public Test updateTest(Test test) {
      return testDAO.update(test);
   }

   @Secure
   public void deleteTest(Test test) {
      Test t = testDAO.get(test.getId());
      //TODO: delete test executions
      testDAO.delete(t);
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

   public List<Metric> getAllMetrics() {
      return metricDAO.getMetrics();
   }

   @Secure
   public void deleteMetric(Metric metric) {
      Metric m = metricDAO.get(metric.getId());
      metricDAO.delete(m);
   }

   @Override
   public TestExecution getTestExecution(Long id) {
      return testExecutionDAO.getFullTestExecution(id);
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
      if (test.getUid() == null) {
         throw new NullPointerException("test.uid");
      }
      Test freshTest = testDAO.findByUid(test.getUid());
      if (freshTest == null) {
         throw serviceException("Test execution has to refer to an existing test! There is no test with uid=%s", test.getUid());
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
}
