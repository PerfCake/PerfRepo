/* 
 * Copyright 2013 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
import org.jboss.qa.perfrepo.model.to.MetricReportTO;
import org.jboss.qa.perfrepo.model.to.MetricReportTO.DataPoint;
import org.jboss.qa.perfrepo.model.to.MetricReportTO.SeriesRequest;
import org.jboss.qa.perfrepo.model.to.MetricReportTO.SeriesResponse;
import org.jboss.qa.perfrepo.model.to.MetricReportTO.SortType;
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

   private static final int MAX_PROBLEMATIC_DATAPOINTS = 10;

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
         for (Value value : testExecution.getValues()) {
            value.setTestExecution(storedTestExecution);
            if (value.getMetricName() == null) {
               throw new IllegalArgumentException("Metric name is mandatory");
            }
            TestMetric testMetric = testMetricDAO.find(test, value.getMetricName());
            if (testMetric == null) {
               throw serviceException(METRIC_NOT_IN_TEST, "Test \"%s\" (%s) doesn't have metric \"%s\"", test.getName(), test.getId(), value.getMetricName());
            }
            value.setMetric(testMetric.getMetric());
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

   public List<TestExecution> getFullTestExecutions(List<Long> ids) {
      List<TestExecution> result = new ArrayList<TestExecution>();
      for (Long id : ids) {
         TestExecution testExecution = getFullTestExecution(id);
         if (testExecution != null) {
            result.add(testExecution);
         }
      }
      return result;
   }

   public List<Test> getFullTests(List<Long> ids) {
      List<Test> result = new ArrayList<Test>();
      for (Long id : ids) {
         result.add(getFullTest(id));
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
      TestExecution testExecution = testExecutionDAO.find(attachment.getTestExecution().getId());
      if (testExecution == null) {
         throw serviceException(TEST_EXECUTION_NOT_FOUND, "Trying to add attachment to non-existent test execution (id=%s)", attachment.getTestExecution()
               .getId());
      }
      checkUserCanChangeTest(testExecution.getTest());
      attachment.setTestExecution(testExecution);
      TestExecutionAttachment newAttachment = testExecutionAttachmentDAO.create(attachment);
      return newAttachment.getId();
   }

   @Override
   public TestExecutionAttachment getAttachment(Long id) {
      return testExecutionAttachmentDAO.find(id);
   }

   private SecurityException notInGroup(String userName, String groupId, String test, String testUid) {
      return new SecurityException(String.format("User %s is not in group %s that owns the test %s (uid=%s)", userName, groupId, test, testUid));
   }

   private SecurityException cantCreateTest(String userName, String groupId) {
      return new SecurityException(String.format("User %s is not in group %s. Can't create test with group id that you're not member of.", userName, groupId));
   }

   private ServiceException serviceException(int code, String msg, Object... args) {
      return new ServiceException(code, args, String.format(msg, args));
   }

   @Override
   public Test createTest(Test test) throws ServiceException {
      if (!userInfo.isUserInRole(test.getGroupId())) {
         throw cantCreateTest(userInfo.getUserName(), test.getGroupId());
      }
      if (testDAO.findByUid(test.getUid()) != null) {
         throw serviceException(TEST_UID_EXISTS, "Test with UID \"%s\" exists.", test.getUid());
      }
      Test createdTest = testDAO.create(test);
      //store metrics
      if (test.getTestMetrics() != null && test.getTestMetrics().size() > 0) {
         for (TestMetric tm : test.getTestMetrics()) {
            addMetric(test, tm.getMetric());
         }
      }
      return createdTest;
   }

   public Test getOrCreateTest(Test test) throws ServiceException {
      Test storedTest = testDAO.findByUid(test.getUid());
      if (storedTest == null) {
         storedTest = createTest(test);
      }
      return storedTest;
   }

   public Test getFullTest(Long id) {
      Test test = testDAO.findReadOnly(id);
      if (test == null) {
         return null;
      }
      // TODO: return by named query, with optimized fetching
      Collection<TestMetric> tms = test.getTestMetrics();
      if (tms != null) {
         List<Metric> metrics = new ArrayList<Metric>();
         for (TestMetric tm : tms) {
            Metric metric = tm.getMetric().clone();
            metric.setTestMetrics(null); // we don't need to infinitely recurse
            metrics.add(metric);
         }
         test.setMetrics(metrics);
      }
      return test;
   }

   public List<Test> getAllFullTests() {
      List<Test> r = testDAO.findAll();
      List<Test> rcopy = new ArrayList<Test>(r.size());
      for (Test t : r) {
         rcopy.add(getFullTest(t.getId()));
      }
      return rcopy;
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
      TestExecution freshTestExecution = testExecutionDAO.find(testExecution.getId());
      if (freshTestExecution == null) {
         throw serviceException(TEST_EXECUTION_NOT_FOUND, "Test execution with id %s doesn't exist", testExecution.getId());
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

   @Override
   public Metric getFullMetric(Long id) {
      Metric metric = metricDAO.findReadOnly(id);
      if (metric == null) {
         return null;
      }
      // TODO: read by named query with join fetches
      Collection<TestMetric> testMetrics = metric.getTestMetrics();
      List<Test> tests = new ArrayList<Test>();
      if (testMetrics != null) {
         for (TestMetric testMetric : testMetrics) {
            Test test = testMetric.getTest().clone();
            test.setTestMetrics(null);
            tests.add(test);
         }
      }
      metric.setTests(tests);
      return metric;
   }

   public List<Metric> getMetrics(String name, Test test) {
      Test t = testDAO.find(test.getId());
      return metricDAO.getMetricByNameAndGroup(name, t.getGroupId());
   }

   public List<Metric> getAvailableMetrics(Test test) {
      Test t = testDAO.find(test.getId());
      List<Metric> result = metricDAO.getMetricByGroup(t.getGroupId());
      result.removeAll(t.getSortedMetrics());
      return result;
   }

   public TestMetric addMetric(Test test, Metric metric) throws ServiceException {
      Test freshTest = checkUserCanChangeTest(test);
      return addMetricInternal(freshTest, metric);
   }

   // works with fresh test loaded from database + checked access rights
   private TestMetric addMetricInternal(Test test, Metric metric) throws ServiceException {
      if (metric.getId() != null) {
         // associating an existing metric with the test
         Metric freshMetric = metricDAO.find(metric.getId());
         if (freshMetric == null) {
            throw serviceException(METRIC_NOT_FOUND, "Metric %s doesn't exist anymore", metric.getId());
         }
         for (Test testForMetric : freshMetric.getTests()) {
            if (!testForMetric.getGroupId().equals(test.getGroupId())) {
               throw serviceException(METRIC_SHARING_ONLY_IN_GROUP, "Metric can be shared only between tests with same group id");
            }
            if (testForMetric.getId().equals(test.getId())) {
               throw serviceException(METRIC_EXISTS, "Test %s already contains metric %s", test.getUid(), freshMetric.getName());
            }
         }
         return createTestMetric(test, freshMetric);
      } else {
         // creating a new metric object
         if (metric.getName() == null) {
            throw new IllegalArgumentException("Metric name is mandatory");
         }
         // metric name needs to be unique in the metric space of a certain groupId
         // does it exist in a test with same group id (including the target test) ?
         List<Metric> existingMetricsForGroup = metricDAO.getMetricByNameAndGroup(metric.getName(), test.getGroupId());
         for (Metric existingMetric : existingMetricsForGroup) {
            if (existingMetric.getName().equals(metric.getName())) {
               throw serviceException(METRIC_EXISTS, "Test %s already contains metric %s", test.getUid(), metric.getName());
            }
         }
         Metric freshMetric = metricDAO.create(metric);
         return createTestMetric(test, freshMetric);
      }
   }

   private TestMetric createTestMetric(Test test, Metric metric) {
      Metric existingMetric = metricDAO.find(metric.getId());
      TestMetric tm = new TestMetric();
      tm.setMetric(existingMetric);
      tm.setTest(test);
      return testMetricDAO.create(tm);
   }

   public Metric updateMetric(Test test, Metric metric) throws ServiceException {
      Test freshTest = checkUserCanChangeTest(test);
      TestMetric freshTestMetric = testMetricDAO.find(freshTest, metric);
      if (freshTestMetric == null) {
         throw serviceException(METRIC_NOT_IN_TEST, "Test \"%s\" (%s) doesn't have metric \"%s\"", freshTest.getName(), freshTest.getId(), metric.getName());
      }
      return metricDAO.update(metric);
   }

   public List<Metric> getTestMetrics(Test test) {
      Test t = testDAO.find(test.getId());
      return t.getSortedMetrics();
   }

   public List<Metric> getAllFullMetrics() {
      List<Metric> r = metricDAO.getMetrics();
      List<Metric> rcopy = new ArrayList<Metric>(r.size());
      for (Metric m : r) {
         rcopy.add(getFullMetric(m.getId()));
      }
      return rcopy;
   }

   public void deleteMetric(Test test, Metric metric) throws ServiceException {
      Test freshTest = checkUserCanChangeTest(test);
      if (freshTest == null) {
         throw serviceException(TEST_NOT_FOUND, "Test with id=%s, doesn't exist.", test.getId());
      }
      TestMetric freshTestMetric = testMetricDAO.find(freshTest, metric.getName());
      Metric freshMetric = freshTestMetric.getMetric();
      if (freshMetric.getTestMetrics().size() == 1) {
         if (!freshMetric.getValues().isEmpty()) {
            throw serviceException(METRIC_HAS_VALUES, "Can't delete metric %s, some values still refer to it", freshMetric.getName());
         } else {
            testMetricDAO.delete(freshTestMetric);
            metricDAO.delete(freshMetric);
         }
      } else {
         testMetricDAO.delete(freshTestMetric);
      }
   }

   public void deleteTestMetric(TestMetric tm) {
      TestMetric existingTM = testMetricDAO.find(tm.getId());
      testMetricDAO.delete(existingTM);
      //TODO:check metric and delete
   }

   @Override
   public TestExecution getFullTestExecution(Long id) {
      TestExecution testExecution = testExecutionDAO.findReadOnly(id);
      if (testExecution == null) {
         return null;
      }
      // lazy fetching (clone still contains, JPA-Managed collections)
      // TODO: try alternative with findWithDepth and test performance
      testExecution.getParametersAsMap();
      testExecution.getSortedTags();
      List<Value> cloneValues = new ArrayList<Value>();
      for (Value v : testExecution.getValues()) {
         Value cloneValue = v.clone();
         cloneValues.add(cloneValue);
         boolean emptyParams = true;
         for (ValueParameter p : cloneValue.getParameters()) {
            emptyParams = false;
            p.getName();
            p.getParamValue();
         }
         if (emptyParams) {
            cloneValue.setParameters(null);
         }
      }
      testExecution.setValues(cloneValues);
      testExecution.setAttachments(testExecutionAttachmentDAO.findByExecution(id));
      return testExecution;
   }

   @Override
   public List<TestExecution> getAllFullTestExecutions() {
      List<TestExecution> r = testExecutionDAO.findAll();
      List<TestExecution> rcopy = new ArrayList<TestExecution>(r.size());
      for (TestExecution exec : r) {
         rcopy.add(getFullTestExecution(exec.getId()));
      }
      return rcopy;
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
         freshTest = testDAO.find(test.getId());
         if (freshTest == null) {
            throw serviceException(TEST_NOT_FOUND, "Test with id=%s, doesn't exist.", test.getId());
         }
      } else if (test.getUid() != null) {
         freshTest = testDAO.findByUid(test.getUid());
         if (freshTest == null) {
            throw serviceException(TEST_UID_NOT_FOUND, "Test with uid=%s, doesn't exist.", test.getUid());
         }
      } else {
         throw new IllegalArgumentException("Can't find test, id or uid needs to be supplied");
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
      TestExecution freshTE = testExecutionDAO.find(te.getId());
      tep.setTestExecution(freshTE);
      return testExecutionParameterDAO.create(tep);
   }

   public TestExecutionParameter getTestExecutionParameter(Long id) {
      return testExecutionParameterDAO.find(id);
   }

   public TestExecutionParameter updateTestExecutionParameter(TestExecutionParameter tep) {
      tep.setTestExecution(testExecutionDAO.find(tep.getTestExecution().getId()));
      return testExecutionParameterDAO.update(tep);
   }

   public void deleteTestExecutionParameter(TestExecutionParameter tep) {
      TestExecutionParameter tepRemove = testExecutionParameterDAO.find(tep.getId());
      testExecutionParameterDAO.delete(tepRemove);
   }

   public TestExecutionTag addTestExecutionTag(TestExecution te, TestExecutionTag teg) throws ServiceException {
      TestExecution freshTE = testExecutionDAO.find(te.getId());
      Tag tag = tagDAO.findByName(teg.getTag().getName());
      if (tag == null) {
         tag = tagDAO.create(teg.getTag());
      }
      teg.setTestExecution(freshTE);
      teg.setTag(tag);
      return testExecutionTagDAO.create(teg);
   }

   public void deleteTestExecutionTag(TestExecutionTag teg) {
      TestExecutionTag tegRemove = testExecutionTagDAO.find(teg.getId());
      testExecutionTagDAO.delete(tegRemove);
   }

   public ValueParameter addValueParameter(Value value, ValueParameter vp) {
      Value freshValue = valueDAO.find(value.getId());
      vp.setValue(freshValue);
      return valueParameterDAO.create(vp);
   }

   public ValueParameter updateValueParameter(ValueParameter vp) {
      vp.setValue(valueDAO.find(vp.getValue().getId()));
      return valueParameterDAO.update(vp);
   }

   public void deleteValueParameter(ValueParameter vp) {
      ValueParameter freshVP = valueParameterDAO.find(vp.getId());
      valueParameterDAO.delete(freshVP);
   }

   public Value getValue(Long id) {
      return valueDAO.getValue(id);
   }

   public Value addValue(TestExecution te, Value value) {
      TestExecution freshTestExecution = testExecutionDAO.find(te.getId());
      Metric metric = metricDAO.find(value.getMetric().getId());
      value.setTestExecution(freshTestExecution);
      value.setMetric(metric);
      return valueDAO.create(value);
   }

   public Value updateValue(Value value) {
      TestExecution freshTestExecution = testExecutionDAO.find(value.getTestExecution().getId());
      value.setTestExecution(freshTestExecution);
      return valueDAO.update(value);
   }

   public void deleteValue(Value value) {
      Value v = valueDAO.find(value.getId());
      for (ValueParameter vp : v.getParameters()) {
         valueParameterDAO.delete(vp);
      }
      valueDAO.delete(v);
   }

   protected List<Test> getAllSelectionTests() {
      return testDAO.findAllReadOnly();
   }

   protected List<Metric> getAllSelectionMetrics(Long testId) {
      return metricDAO.getMetricByTest(testId);
   }

   protected List<String> getAllSelectionExecutionParams(Long testId) {
      return testExecutionParameterDAO.getAllSelectionExecutionParams(testId);
   }

   public MetricReportTO.Response computeMetricReport(MetricReportTO.Request request) {
      MetricReportTO.Response response = new MetricReportTO.Response();
      if (request.getTestUid() == null) {
         // no test is chosen - pick a test
         response.setSelectionTests(getAllSelectionTests());
         return response;
      } else {
         Test freshTest = testDAO.findByUid(request.getTestUid());
         if (freshTest == null) {
            // test uid supplied but doesn't exist - pick another test
            response.setSelectionTests(getAllSelectionTests());
            return response;
         } else {
            freshTest = freshTest.clone();
            response.setSelectedTest(freshTest);
            if (request.getParamName() == null || !testExecutionParameterDAO.hasTestParam(freshTest.getId(), request.getParamName())) {
               response.setSelectionParam(getAllSelectionExecutionParams(freshTest.getId()));
               Collections.sort(response.getSelectionParams());
               return response;
            }
            response.setSelectedParam(request.getParamName());
            if (request.getSeriesSpecs() == null || request.getSeriesSpecs().isEmpty()) {
               response.setSelectionMetrics(getAllSelectionMetrics(freshTest.getId()));
               return response;
            }
            // selection metrics will be needed in series selectors even on success
            response.setSelectionMetrics(getAllSelectionMetrics(freshTest.getId()));
            for (SeriesRequest seriesRequest : request.getSeriesSpecs()) {
               if (seriesRequest.getName() == null) {
                  throw new IllegalArgumentException("series has null name");
               }
               if (seriesRequest.getMetricName() == null) {
                  return response;
               }
               TestMetric testMetric = testMetricDAO.find(freshTest, seriesRequest.getMetricName());
               if (testMetric == null) {
                  return response;
               }
               List<DataPoint> datapoints = testExecutionDAO.searchValues(freshTest.getId(), seriesRequest.getMetricName(), request.getParamName(),
                     seriesRequest.getTags());
               if (datapoints.isEmpty()) {
                  return response;
               }
               SeriesResponse seriesResponse = new SeriesResponse(seriesRequest.getName(), testMetric.getMetric().clone(), null);
               List<DataPoint> problematicDatapoints = new ArrayList<DataPoint>();
               if (SortType.NUMBER.equals(request.getSortType())) {
                  for (DataPoint dp : datapoints) {
                     try {
                        dp.param = Long.valueOf((String) dp.param);
                     } catch (NumberFormatException e) {
                        dp.value = DataPoint.CONVERSION;
                        problematicDatapoints.add(dp);
                        if (problematicDatapoints.size() >= MAX_PROBLEMATIC_DATAPOINTS) {
                           break;
                        }
                     }
                  }
                  if (!problematicDatapoints.isEmpty()) {
                     response.setSeries(null);
                     seriesResponse.setDatapoints(problematicDatapoints);
                     response.setProblematicSeries(seriesResponse);
                     return response;
                  } else {
                     // re-sort as numbers
                     Collections.sort(datapoints);
                  }
               }
               // the datapoints are sorted (either by long or string value)
               // let's find out whether the given test execution parameter was unique between executions
               for (int i = 1; i < datapoints.size(); i++) {
                  DataPoint dp = datapoints.get(i);
                  DataPoint dpPrev = datapoints.get(i - 1);
                  if (dpPrev.param.equals(dp.param)) {
                     if (!problematicDatapoints.isEmpty() && problematicDatapoints.get(problematicDatapoints.size() - 1) != dpPrev) {
                        dpPrev.value = DataPoint.CONFLICT;
                        problematicDatapoints.add(dpPrev);
                     }
                     dp.value = DataPoint.CONFLICT;
                     problematicDatapoints.add(dp);
                     if (problematicDatapoints.size() >= MAX_PROBLEMATIC_DATAPOINTS) {
                        break;
                     }
                  }
               }
               if (!problematicDatapoints.isEmpty()) {
                  response.setSeries(null);
                  seriesResponse.setDatapoints(problematicDatapoints);
                  response.setProblematicSeries(seriesResponse);
                  return response;
               }
               if (datapoints.size() > request.getLimitSize()) {
                  // return only last limitSize points
                  List<DataPoint> datapointsTail = new ArrayList<DataPoint>();
                  for (int i = datapoints.size() - request.getLimitSize(); i < datapoints.size(); i++) {
                     datapointsTail.add(datapoints.get(i));
                  }
                  seriesResponse.setDatapoints(datapointsTail);
               } else {
                  seriesResponse.setDatapoints(datapoints);
               }
               response.addSeries(seriesResponse);
            }

            return response;
         }
      }
   }

   @Override
   public TestExecutionParameter getFullParameter(Long paramId) {
      TestExecutionParameter p = testExecutionParameterDAO.find(paramId);
      if (p == null) {
         return null;
      }
      TestExecutionParameter pclone = p.clone();
      pclone.setTestExecution(p.getTestExecution().clone());
      return pclone;
   }
}
