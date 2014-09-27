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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.PersistenceException;

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
import org.jboss.qa.perfrepo.model.to.TestExecutionSearchTO.ParamCriteria;
import org.jboss.qa.perfrepo.model.to.TestSearchTO;
import org.jboss.qa.perfrepo.model.util.EntityUtils;
import org.jboss.qa.perfrepo.model.util.EntityUtils.UpdateSet;
import org.jboss.qa.perfrepo.security.Secured;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.jboss.qa.perfrepo.service.exceptions.ServiceException;

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
   private UserService userService;

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
               throw serviceException(ServiceException.Codes.METRIC_NOT_IN_TEST, "Test \"%s\" (%s) doesn't have metric \"%s\"", test.getName(), test.getId(), value.getMetricName());
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
      TestExecution clone = cloneAndFetch(storedTestExecution, true, true, true, true, true);
      log.debug("Created new test execution " + clone.getId());
      return clone;
   }

   @Override
   public List<TestExecution> getFullTestExecutionsByTestAndJob(Long testId, Long jobId) {
      List<TestExecution> result = new ArrayList<TestExecution>();
      for (TestExecution te : testExecutionDAO.findByTestAndJob(testId, jobId)) {
         TestExecution testExecution = getFullTestExecution(te.getId());
         if (testExecution != null) {
            result.add(testExecution);
         }
      }
      return result;
   }

   @Override
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

   @Override
   public List<Test> searchTest(TestSearchTO search) {
      return testDAO.searchTests(search);
   }

   @Override
   public List<TestExecution> searchTestExecutions(TestExecutionSearchTO search) {
      // remove param criteria with empty param name
      if (search.getParameters() != null) {
         for (Iterator<ParamCriteria> allParams = search.getParameters().iterator(); allParams.hasNext();) {
            ParamCriteria param = allParams.next();
            if (param.isNameEmpty()) {
               allParams.remove();
            }
         }
      }
      List<TestExecution> result = testExecutionDAO.searchTestExecutions(search, testExecutionParameterDAO);
      return result;
   }

   @Override
   public Test getTestByUID(String uid) {
	   return testDAO.findByUid(uid);
   }

   @Override
   public List<TestExecution> getTestExecutions(List<String> tags, List<String> testUIDs) {
	   List<TestExecution> result = new ArrayList<TestExecution>();
	   for (String tag : tags) {
		   result.addAll(testExecutionDAO.getTestExecutions(Arrays.asList(tag.split(" ")), testUIDs));
	   }
	   return result;
   }

   @Override
   public List<TestExecution> searchTestExecutionsGroupedByJobId(TestExecutionSearchTO search) {
      List<TestExecution> result = testExecutionDAO.searchTestExecutions(search, testExecutionParameterDAO);
      final HashSet<Long> jobIds = new HashSet<Long>();
      List<TestExecution> r = Lists.newArrayList(Iterables.filter(result, new Predicate<TestExecution>() {

         @Override
         public boolean apply(TestExecution te) {
            if (jobIds.contains(te.getJobId())) {
               return false;
            }
            jobIds.add(te.getJobId());
            TestExecutionDAO.fetchTags(te);
            return true;
         }
      }));
      return r;
   }

   @Override
   public Long addAttachment(TestExecutionAttachment attachment) throws ServiceException {
      TestExecution exec = testExecutionDAO.find(attachment.getTestExecution().getId());
      if (exec == null) {
         throw serviceException(ServiceException.Codes.TEST_EXECUTION_NOT_FOUND, "Trying to add attachment to non-existent test execution (id=%s)", attachment.getTestExecution()
               .getId());
      }
      checkUserCanChangeTest(exec.getTest());
      checkLocked(exec);
      attachment.setTestExecution(exec);
      TestExecutionAttachment newAttachment = testExecutionAttachmentDAO.create(attachment);
      return newAttachment.getId();
   }

   @Override
   public void deleteAttachment(TestExecutionAttachment attachment) throws ServiceException {
      TestExecution exec = testExecutionDAO.find(attachment.getTestExecution().getId());
      if (exec == null) {
         throw serviceException(ServiceException.Codes.TEST_EXECUTION_NOT_FOUND, "Trying to delete attachment of non-existent test execution (id=%s)", attachment.getTestExecution()
               .getId());
      }
      checkUserCanChangeTest(exec.getTest());
      checkLocked(exec);
      TestExecutionAttachment freshAttachment = testExecutionAttachmentDAO.find(attachment.getId());
      if (freshAttachment != null) {
         testExecutionAttachmentDAO.delete(freshAttachment);
      }
   }

   @Override
   public TestExecutionAttachment getAttachment(Long id) {
      return testExecutionAttachmentDAO.find(id);
   }

   @Override
   public Test createTest(Test test) throws ServiceException {
      if (!userService.isLoggedUserInGroup(test.getGroupId())) {
         throw cantCreateTest(userService.getLoggedUserName(), test.getGroupId());
      }
      if (testDAO.findByUid(test.getUid()) != null) {
         throw serviceException(ServiceException.Codes.TEST_UID_EXISTS, "Test with UID \"%s\" exists.", test.getUid());
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

   @Override
   public Test getFullTest(Long id) {
      Test test = testDAO.find(id);
      if (test == null) {
         return null;
      }
      test = test.clone();
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

   @Override
   public List<Test> getAllFullTests() {
      List<Test> r = testDAO.findAll();
      List<Test> rcopy = new ArrayList<Test>(r.size());
      for (Test t : r) {
         rcopy.add(getFullTest(t.getId()));
      }
      return rcopy;
   }

   @Secured
   @Override
   public Test updateTest(Test test) {
      return testDAO.update(test);
   }

   @Override
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

   @Override
   public void deleteTestExecution(TestExecution testExecution) throws ServiceException {
      TestExecution freshTestExecution = testExecutionDAO.find(testExecution.getId());
      if (freshTestExecution == null) {
         throw serviceException(ServiceException.Codes.TEST_EXECUTION_NOT_FOUND, "Test execution with id %s doesn't exist", testExecution.getId());
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
      Metric metric = metricDAO.find(id);
      if (metric == null) {
         return null;
      }
      metric = metric.clone();
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

   @Override
   public List<Metric> getAvailableMetrics(Test test) {
      Test t = testDAO.find(test.getId());
      return EntityUtils.removeAllById(metricDAO.getMetricByGroup(t.getGroupId()), t.getMetrics());
   }

   @Override
   public TestMetric addMetric(Test test, Metric metric) throws ServiceException {
      Test freshTest = checkUserCanChangeTest(test);
      return addMetricInternal(freshTest, metric);
   }

   @Override
   public Metric updateMetric(Test test, Metric metric) throws ServiceException {
      Test freshTest = checkUserCanChangeTest(test);
      TestMetric freshTestMetric = testMetricDAO.find(freshTest, metric);
      if (freshTestMetric == null) {
         throw serviceException(ServiceException.Codes.METRIC_NOT_IN_TEST, "Test \"%s\" (%s) doesn't have metric \"%s\"", freshTest.getName(), freshTest.getId(), metric.getName());
      }
      return metricDAO.update(metric);
   }

   @Override
   public List<Metric> getTestMetrics(Test test) {
      Test t = testDAO.find(test.getId());
      return t.getSortedMetrics();
   }

   @Override
   public List<Metric> getAllFullMetrics() {
      List<Metric> r = metricDAO.getMetrics();
      List<Metric> rcopy = new ArrayList<Metric>(r.size());
      for (Metric m : r) {
         rcopy.add(getFullMetric(m.getId()));
      }
      return rcopy;
   }

   @Override
   public void deleteMetric(Test test, Metric metric) throws ServiceException {
      Test freshTest = checkUserCanChangeTest(test);
      if (freshTest == null) {
         throw serviceException(ServiceException.Codes.TEST_NOT_FOUND, "Test with id=%s, doesn't exist.", test.getId());
      }
      TestMetric freshTestMetric = testMetricDAO.find(freshTest, metric.getName());
      Metric freshMetric = freshTestMetric.getMetric();
      if (freshMetric.getTestMetrics().size() == 1) {
         if (!freshMetric.getValues().isEmpty()) {
            throw serviceException(ServiceException.Codes.METRIC_HAS_VALUES, "Can't delete metric %s, some values still refer to it", freshMetric.getName());
         } else {
            testMetricDAO.delete(freshTestMetric);
            metricDAO.delete(freshMetric);
         }
      } else {
         testMetricDAO.delete(freshTestMetric);
      }
   }

   @Override
   public TestExecution getFullTestExecution(Long id) {
      return cloneAndFetch(testExecutionDAO.find(id), true, true, true, true, true);
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

   @Override
   public TestExecution updateTestExecution(TestExecution anExec) throws ServiceException {
      TestExecution execEntity = testExecutionDAO.find(anExec.getId());
      if (execEntity == null) {
         throw serviceException(ServiceException.Codes.TEST_EXECUTION_NOT_FOUND, "Test execution doesn't exist (id=%s)", anExec.getId());
      }
      checkUserCanChangeTest(execEntity.getTest());
      checkLocked(execEntity);
      for (TestExecutionTag interObj : execEntity.getTestExecutionTags()) {
         testExecutionTagDAO.delete(interObj);
      }
      execEntity.getTestExecutionTags().clear();
      // this is what can be updated here
      execEntity.setName(anExec.getName());
      execEntity.setStarted(anExec.getStarted());
      execEntity.setComment(anExec.getComment());
      for (String tag : new HashSet<String>(anExec.getTags())) {
         Tag tagEntity = tagDAO.findByName(tag);
         if (tagEntity == null) {
            Tag newTag = new Tag();
            newTag.setName(tag);
            tagEntity = tagDAO.create(newTag);
         }
         TestExecutionTag newTestExecutionTag = new TestExecutionTag();
         newTestExecutionTag.setTag(tagEntity);
         newTestExecutionTag.setTestExecution(execEntity);
         testExecutionTagDAO.create(newTestExecutionTag);
         execEntity.getTestExecutionTags().add(newTestExecutionTag);
      }
      TestExecution execClone = cloneAndFetch(execEntity, true, true, true, true, true);
      return execClone;
   }

   @Override
   public TestExecution setExecutionLocked(TestExecution anExec, boolean locked) throws ServiceException {
      TestExecution execEntity = testExecutionDAO.find(anExec.getId());
      if (execEntity == null) {
         throw serviceException(ServiceException.Codes.TEST_EXECUTION_NOT_FOUND, "Test execution doesn't exist (id=%s)", anExec.getId());
      }
      checkUserCanChangeTest(execEntity.getTest());
      execEntity.setLocked(locked);
      return getFullTestExecution(anExec.getId());
   }

   @Override
   public TestExecutionParameter updateParameter(TestExecutionParameter tep) throws ServiceException {
      TestExecution exec = testExecutionDAO.find(tep.getTestExecution().getId());
      if (exec == null) {
         throw serviceException(ServiceException.Codes.TEST_EXECUTION_NOT_FOUND, "Test execution doesn't exist (id=%s)", tep.getTestExecution().getId());
      }
      checkUserCanChangeTest(exec.getTest());
      checkLocked(exec);

      if(testExecutionParameterDAO.hasTestParam(exec.getTest().getId(), tep.getName())) {
         throw serviceException(ServiceException.Codes.PARAMETER_EXISTS, "parameter with name \"%s\" exists.", tep.getName());
      }

      return testExecutionParameterDAO.update(tep);
   }

   @Override
   public void deleteParameter(TestExecutionParameter tep) throws ServiceException {
      TestExecution exec = testExecutionDAO.find(tep.getTestExecution().getId());
      if (exec == null) {
         throw serviceException(ServiceException.Codes.TEST_EXECUTION_NOT_FOUND, "Test execution doesn't exist (id=%s)", tep.getTestExecution().getId());
      }
      checkUserCanChangeTest(exec.getTest());
      checkLocked(exec);
      TestExecutionParameter tepRemove = testExecutionParameterDAO.find(tep.getId());
      testExecutionParameterDAO.delete(tepRemove);
   }

   @Override
   public Value addValue(Value value) throws ServiceException {
      TestExecution exec = testExecutionDAO.find(value.getTestExecution().getId());
      if (exec == null) {
         throw serviceException(ServiceException.Codes.TEST_EXECUTION_NOT_FOUND, "Test execution doesn't exist (id=%s)", value.getTestExecution().getId());
      }
      checkUserCanChangeTest(exec.getTest());
      checkLocked(exec);
      Metric metric = null;
      if (value.getMetric().getId() != null) {
         metric = metricDAO.find(value.getMetric().getId());
      } else {
    	  List<Metric> metrics = metricDAO.getMetricByNameAndGroup(value.getMetric().getName(), exec.getTest().getGroupId());
    	  if (metrics.size() > 0) {
    		  metric = metricDAO.find(metrics.get(0).getId());
    	  }
      }
      if (metric == null) {
         throw serviceException(ServiceException.Codes.METRIC_NOT_FOUND, "Metric not found (id=%s)", value.getMetric().getId());
      }
      value.setTestExecution(exec);
      value.setMetric(metric);
      // check if other values for given metric exist, if yes, we can only add one if both old and new one have at least one parameter
      List<Value> existingValuesForMetric = valueDAO.find(exec.getId(), metric.getId());
      if (!existingValuesForMetric.isEmpty()) {
         for (Value v : existingValuesForMetric) {
            if (!v.hasParameters()) {
               throw serviceException(ServiceException.Codes.UNPARAMETRIZED_MULTI_VALUE, "If you create multiple values for same metric, they must be parametrized.");
            }
         }
         if (!value.hasParameters()) {
            throw serviceException(ServiceException.Codes.UNPARAMETRIZED_MULTI_VALUE, "If you create multiple values for same metric, they must be parametrized.");
         }
      }
      Value freshValue = valueDAO.create(value);
      Value freshValueClone = freshValue.clone();
      List<ValueParameter> newParams = new ArrayList<ValueParameter>();
      if (value.hasParameters()) {
         for (ValueParameter valueParameter : value.getParameters()) {
            valueParameter.setValue(freshValue);
            newParams.add(valueParameterDAO.create(valueParameter).clone());
            newParams.get(newParams.size() - 1).setValue(freshValueClone);
         }
      }
      freshValueClone.setParameters(newParams.isEmpty() ? null : newParams);
      return freshValueClone;
   }

   @Override
   public Value updateValue(Value value) throws ServiceException {
      TestExecution exec = testExecutionDAO.find(value.getTestExecution().getId());
      if (exec == null) {
         throw serviceException(ServiceException.Codes.TEST_EXECUTION_NOT_FOUND, "Test execution doesn't exist (id=%s)", value.getTestExecution().getId());
      }
      checkUserCanChangeTest(exec.getTest());
      checkLocked(exec);
      Value oldValue = valueDAO.find(value.getId());
      if (oldValue == null) {
         throw serviceException(ServiceException.Codes.VALUE_NOT_FOUND, "Value doesn't exist (id=%s)", value.getId());
      }
      Value freshValue = valueDAO.update(value);
      Value freshValueClone = freshValue.clone();
      freshValueClone.setMetric(freshValue.getMetric().clone());
      freshValueClone.getMetric().setTestMetrics(null);
      freshValueClone.getMetric().setValues(null);
      UpdateSet<ValueParameter> updateSet = EntityUtils.updateSet(oldValue.getParameters(), value.getParameters());
      if (!updateSet.removed.isEmpty()) {
         throw serviceException(ServiceException.Codes.STALE_COLLECTION, "Collection of value parameters contains stale ids: %s", updateSet.removed);
      }
      List<ValueParameter> newParams = new ArrayList<ValueParameter>();
      for (ValueParameter vp : updateSet.toAdd) {
         vp.setValue(freshValue);
         newParams.add(valueParameterDAO.create(vp).clone());
         newParams.get(newParams.size() - 1).setValue(freshValueClone);
      }
      for (ValueParameter vp : updateSet.toUpdate) {
         newParams.add(valueParameterDAO.update(vp).clone());
         newParams.get(newParams.size() - 1).setValue(freshValueClone);
      }
      for (ValueParameter vp : updateSet.toRemove) {
         valueParameterDAO.delete(vp);
      }
      freshValueClone.setParameters(newParams.isEmpty() ? null : newParams);
      return freshValueClone;
   }

   @Override
   public void deleteValue(Value value) throws ServiceException {
      TestExecution exec = testExecutionDAO.find(value.getTestExecution().getId());
      if (exec == null) {
         throw serviceException(ServiceException.Codes.TEST_EXECUTION_NOT_FOUND, "Test execution doesn't exist (id=%s)", value.getTestExecution().getId());
      }
      checkUserCanChangeTest(exec.getTest());
      checkLocked(exec);
      Value v = valueDAO.find(value.getId());
      for (ValueParameter vp : v.getParameters()) {
         valueParameterDAO.delete(vp);
      }
      valueDAO.delete(v);
   }

   @Override
   public List<Test> getAllSelectionTests() {
      return testDAO.findAll();
   }

   @Override
   public List<Metric> getAllSelectionMetrics(Long testId) {
      return metricDAO.getMetricByTest(testId);
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

   @Override
   public List<String> getTestsByPrefix(String prefix) {
	   List<Test> tests = testDAO.findByUIDPrefix(prefix);
	   List<String> testuids =  new ArrayList<String>();
	   for (Test test : tests) {
		   if (userService.isLoggedUserInGroup(test.getGroupId())) {
			   testuids.add(test.getUid());
		   }
	   }
	   return testuids;
   }

   @Override
   public List<String> getTagsByPrefix(String prefix) {
	   List<String> tags = new ArrayList<String>();
	   for (Tag tag : tagDAO.findByPrefix(prefix)) {
		   tags.add(tag.getName());
	   }
	   return tags;
   }

   @Override
   public void addTagsToTestExecutions(Collection<String> tags, Collection<TestExecution> testExecutions) {
      for(TestExecution testExecutionItem: testExecutions) {
         TestExecution testExecution = testExecutionDAO.find(testExecutionItem.getId());
         if(testExecution == null) {
            continue;
         }

         List<TestExecutionTag> testExecutionTags = new ArrayList<TestExecutionTag>();
         testExecutionTags.addAll(testExecution.getTestExecutionTags());
         for(String tagName: tags) {
            if(!testExecution.getTags().contains(tagName)) {
               Tag tag = tagDAO.findByName(tagName);
               if(tag == null) {
                  Tag newTag = new Tag();
                  newTag.setName(tagName);
                  tag = tagDAO.create(newTag);
               }

               TestExecutionTag newTestExecutionTag = new TestExecutionTag();
               newTestExecutionTag.setTag(tag);
               newTestExecutionTag.setTestExecution(testExecution);

               TestExecutionTag testExecutionTag = testExecutionTagDAO.create(newTestExecutionTag);
               testExecutionTags.add(testExecutionTag);
            }
         }

         testExecutionDAO.update(testExecution);
      }
   }

   @Override
   public void deleteTagsFromTestExecutions(Collection<String> tags, Collection<TestExecution> testExecutions) {
      for(TestExecution testExecutionItem: testExecutions) {
         TestExecution testExecution = testExecutionDAO.find(testExecutionItem.getId());
         if(testExecution == null) {
            continue;
         }

         List<TestExecutionTag> testExecutionTags = new ArrayList<TestExecutionTag>();
         for(TestExecutionTag testExecutionTag: testExecution.getTestExecutionTags()) {
            if(tags.contains(testExecutionTag.getTagName())) {
               testExecutionTagDAO.delete(testExecutionTag);
            }
            else {
               testExecutionTags.add(testExecutionTag);
            }
         }

         testExecution.setTestExecutionTags(testExecutionTags);
         testExecutionDAO.update(testExecution);
      }
   }

   @Override
   public Test getTest(Long id) {
      return testDAO.find(id);
   }

   // works with fresh test loaded from database + checked access rights
   private TestMetric addMetricInternal(Test test, Metric metric) throws ServiceException {
      if (metric.getId() != null) {
         // associating an existing metric with the test
         Metric freshMetric = metricDAO.find(metric.getId());
         if (freshMetric == null) {
            throw serviceException(ServiceException.Codes.METRIC_NOT_FOUND, "Metric %s doesn't exist anymore", metric.getId());
         }
         for (Test testForMetric : freshMetric.getTests()) {
            if (!testForMetric.getGroupId().equals(test.getGroupId())) {
               throw serviceException(ServiceException.Codes.METRIC_SHARING_ONLY_IN_GROUP, "Metric can be shared only between tests with same group id");
            }
            if (testForMetric.getId().equals(test.getId())) {
               throw serviceException(ServiceException.Codes.METRIC_EXISTS, "Test %s already contains metric %s", test.getUid(), freshMetric.getName());
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
               Metric freshMetric = metricDAO.find(existingMetric.getId());
               return createTestMetric(test, freshMetric);
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

   private SecurityException notInGroup(String userName, String groupId, String test, String testUid) {
      return new SecurityException(String.format("User %s is not in group %s that owns the test %s (uid=%s)", userName, groupId, test, testUid));
   }

   private SecurityException cantCreateTest(String userName, String groupId) {
      return new SecurityException(String.format("User %s is not in group %s. Can't create test with group id that you're not member of.", userName, groupId));
   }

   private ServiceException serviceException(int code, String msg, Object... args) {
      return new ServiceException(code, args, String.format(msg, args));
   }

   private TestExecution cloneAndFetch(TestExecution exec, boolean fetchTest, boolean fetchParameters, boolean fetchTags, boolean fetchValues,
                                       boolean fetchAttachments) {
      if (exec == null) {
         return null;
      }
      TestExecution clone = exec.clone();
      if (fetchTest) {
         TestExecutionDAO.fetchTest(clone);
      } else {
         clone.setTest(null);
      }
      if (fetchParameters) {
         TestExecutionDAO.fetchParameters(clone);
      } else {
         clone.setParameters(null);
      }
      if (fetchTags) {
         TestExecutionDAO.fetchTags(clone);
      } else {
         clone.setTestExecutionTags(null);
      }
      if (fetchValues) {
         TestExecutionDAO.fetchValues(clone);
      } else {
         clone.setValues(null);
      }
      if (fetchAttachments) {
         TestExecutionDAO.fetchAttachments(clone);
      } else {
         clone.setAttachments(null);
      }
      return clone;
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
            throw serviceException(ServiceException.Codes.TEST_NOT_FOUND, "Test with id=%s, doesn't exist.", test.getId());
         }
      } else if (test.getUid() != null) {
         freshTest = testDAO.findByUid(test.getUid());
         if (freshTest == null) {
            throw serviceException(ServiceException.Codes.TEST_UID_NOT_FOUND, "Test with uid=%s, doesn't exist.", test.getUid());
         }
      } else {
         throw new IllegalArgumentException("Can't find test, id or uid needs to be supplied");
      }
      // user can only insert test executions for tests pertaining to his group
      if (!userService.isLoggedUserInGroup(freshTest.getGroupId())) {
         throw notInGroup(userService.getLoggedUserName(), freshTest.getGroupId(), freshTest.getName(), freshTest.getUid());
      }
      return freshTest;
   }

   private void checkLocked(TestExecution exec) throws ServiceException {
      if (exec.isLocked()) {
         serviceException(ServiceException.Codes.EXECUTION_LOCKED, "Test execution (id=%s) is locked.", exec.getId());
      }
   }
}
