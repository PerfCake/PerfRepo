/**
 *
 * PerfRepo
 *
 * Copyright (C) 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.perfrepo.web.service;

import org.apache.log4j.Logger;

import org.perfrepo.model.*;
import org.perfrepo.model.Test;
import org.perfrepo.model.to.TestExecutionSearchTO;
import org.perfrepo.model.to.TestSearchTO;
import org.perfrepo.model.to.TestExecutionSearchTO.ParamCriteria;
import org.perfrepo.model.user.User;
import org.perfrepo.model.util.EntityUtils;
import org.perfrepo.model.util.EntityUtils.UpdateSet;
import org.perfrepo.web.dao.*;
import org.perfrepo.web.security.Secured;
import org.perfrepo.web.service.exceptions.ServiceException;
import org.perfrepo.web.util.MessageUtils;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.inject.Named;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * Implements {@link TestService}.
 *
 * @author Pavel Drozd (pdrozd@redhat.com)
 * @author Michal Linhard (mlinhard@redhat.com)
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

	@Inject
	private UserDAO userDAO;

   @Inject
   private AlertingService alertingService;

	@Override
	@Secured
	public TestExecution createTestExecution(TestExecution testExecution) throws ServiceException {
		// The test referred by test execution has to be an existing test
		Test test = testDAO.get(testExecution.getTest().getId());
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
					throw new ServiceException(ServiceException.Codes.METRIC_NOT_IN_TEST, test.getName(), test.getId(), value.getMetricName());
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

      alertingService.processAlerts(clone);

		return clone;
	}

	@Override
	public List<TestExecution> getFullTestExecutions(Collection<Long> ids) {
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
		return testDAO.searchTests(search, userService.getLoggedUserGroupNames());
	}

	@Override
	public List<TestExecution> searchTestExecutions(TestExecutionSearchTO search) {
		// remove param criteria with empty param name
		if (search.getParameters() != null) {
			for (Iterator<ParamCriteria> allParams = search.getParameters().iterator(); allParams.hasNext(); ) {
				ParamCriteria param = allParams.next();
				if (param.isNameEmpty()) {
					allParams.remove();
				}
			}
		}

		List<TestExecution> result = testExecutionDAO.searchTestExecutions(search, userService.getLoggedUserGroupNames());
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
	@Secured
	public Long addAttachment(TestExecutionAttachment attachment) throws ServiceException {
		TestExecution exec = testExecutionDAO.get(attachment.getTestExecution().getId());
		if (exec == null) {
			throw new ServiceException(ServiceException.Codes.TEST_EXECUTION_NOT_FOUND_ADD_ATTACHMENT, attachment.getTestExecution().getId());
		}
		attachment.setTestExecution(exec);
		TestExecutionAttachment newAttachment = testExecutionAttachmentDAO.create(attachment);
		return newAttachment.getId();
	}

	@Override
	@Secured
	public void removeAttachment(TestExecutionAttachment attachment) throws ServiceException {
		TestExecution exec = testExecutionDAO.get(attachment.getTestExecution().getId());
		if (exec == null) {
			throw new ServiceException(ServiceException.Codes.TEST_EXECUTION_NOT_FOUND_REMOVE_ATTACHMENT, attachment.getTestExecution().getId());
		}
		TestExecutionAttachment freshAttachment = testExecutionAttachmentDAO.get(attachment.getId());
		if (freshAttachment != null) {
			testExecutionAttachmentDAO.remove(freshAttachment);
		}
	}

	@Override
	public TestExecutionAttachment getAttachment(Long id) {
		return testExecutionAttachmentDAO.get(id);
	}

	@Override
	public Test createTest(Test test) throws ServiceException {
		if (!userService.isLoggedUserInGroup(test.getGroupId())) {
			throw new SecurityException(MessageUtils.getMessage("serviceException.1600", userService.getLoggedUser()
					.getUsername(), test.getGroupId()));
		}
		if (testDAO.findByUid(test.getUid()) != null) {
			throw new ServiceException(ServiceException.Codes.TEST_UID_EXISTS, test.getUid());
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
		Test test = testDAO.get(id);
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
				metric.setAlerts(null);
				metrics.add(metric);
			}
			test.setMetrics(metrics);
		}

		Collection<User> subscribers = test.getSubscribers();
		if (subscribers != null) {
			List<User> subscribersClone = new ArrayList<>();
			for (User subscriber : subscribers) {
				subscribersClone.add(subscriber.clone());
			}
			test.setSubscribers(subscribersClone);
		}

      Collection<Alert> alerts = test.getAlerts();
      if (alerts != null) {
         List<Alert> alertsClone = new ArrayList<>();
         for (Alert alert : alerts) {
            List<Tag> tagsClone = new ArrayList<>();
            for(Tag tag: alert.getTags()) {
               tagsClone.add(tag.clone());
            }
            Alert alertClone = alert.clone();
            alertClone.setTags(tagsClone);
            alertsClone.add(alertClone);
         }
         test.setAlerts(alertsClone);
      }

		return test;
	}

	@Override
	public List<Test> getAllFullTests() {
		List<Test> r = testDAO.getAll();
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
	@Secured
	public void removeTest(Test test) throws ServiceException {
		Test freshTest = testDAO.get(test.getId());
      User currentUser = userService.getLoggedUser();
		for (TestExecution testExecution : freshTest.getTestExecutions()) {
			removeTestExecution(testExecution);
		}

		Iterator<TestMetric> allTestMetrics = freshTest.getTestMetrics().iterator();
		while (allTestMetrics.hasNext()) {
			TestMetric testMetric = allTestMetrics.next();
			Metric metric = testMetric.getMetric();
			List<Test> testsUsingMetric = testDAO.findByNamedQuery(Test.FIND_TESTS_USING_METRIC,
					Collections.<String, Object>singletonMap("metric", metric.getId()));
			allTestMetrics.remove();
			testMetricDAO.remove(testMetric);
			if (testsUsingMetric.size() == 0) {
				throw new IllegalStateException();
			} else if (testsUsingMetric.size() == 1) {
				if (testsUsingMetric.get(0).getId().equals(test.getId())) {
					metricDAO.remove(metric);
				} else {
					throw new IllegalStateException();
				}
			}
		}
		testDAO.remove(freshTest);
	}

	@Override
	@Secured
	public void removeTestExecution(TestExecution testExecution) throws ServiceException {
		TestExecution freshTestExecution = testExecutionDAO.get(testExecution.getId());
		if (freshTestExecution == null) {
			throw new ServiceException(ServiceException.Codes.TEST_EXECUTION_NOT_FOUND, testExecution.getId());
		}
		for (TestExecutionParameter testExecutionParameter : freshTestExecution.getParameters()) {
			testExecutionParameterDAO.remove(testExecutionParameter);
		}
		for (Value value : freshTestExecution.getValues()) {
			for (ValueParameter valueParameter : value.getParameters()) {
				valueParameterDAO.remove(valueParameter);
			}
			valueDAO.remove(value);
		}
		Iterator<TestExecutionTag> allTestExecutionTags = freshTestExecution.getTestExecutionTags().iterator();
		while (allTestExecutionTags.hasNext()) {
			testExecutionTagDAO.remove(allTestExecutionTags.next());
			allTestExecutionTags.remove();
		}
		Iterator<TestExecutionAttachment> allTestExecutionAttachments = freshTestExecution.getAttachments().iterator();
		while (allTestExecutionAttachments.hasNext()) {
			testExecutionAttachmentDAO.remove(allTestExecutionAttachments.next());
			allTestExecutionAttachments.remove();
		}
		testExecutionDAO.remove(freshTestExecution);
	}

	@Override
	public List<Metric> getAvailableMetrics(Test test) {
		Test t = testDAO.get(test.getId());
		return EntityUtils.removeAllById(metricDAO.getMetricByGroup(t.getGroupId()), t.getMetrics());
	}

	@Override
	@Secured
	public TestMetric addMetric(Test test, Metric metric) throws ServiceException {
		Test freshTest = testDAO.get(test.getId());
		if (metric.getId() != null) {
			// associating an existing metric with the test
			Metric freshMetric = metricDAO.get(metric.getId());
			if (freshMetric == null) {
				throw new ServiceException(ServiceException.Codes.METRIC_NOT_FOUND, metric.getId());
			}
			for (Test testForMetric : freshMetric.getTests()) {
				if (!testForMetric.getGroupId().equals(freshTest.getGroupId())) {
					throw new ServiceException(ServiceException.Codes.METRIC_SHARING_ONLY_IN_GROUP);
				}
				if (testForMetric.getId().equals(freshTest.getId())) {
					throw new ServiceException(ServiceException.Codes.METRIC_EXISTS, freshTest.getUid(), freshMetric.getName());
				}
			}
			return createTestMetric(freshTest, freshMetric);
		} else {
			// creating a new metric object
			if (metric.getName() == null) {
				throw new IllegalArgumentException("Metric name is mandatory");
			}
			// metric name needs to be unique in the metric space of a certain groupId
			// does it exist in a test with same group id (including the target test) ?
			List<Metric> existingMetricsForGroup = metricDAO.getMetricByNameAndGroup(metric.getName(),
					freshTest.getGroupId());
			for (Metric existingMetric : existingMetricsForGroup) {
				if (existingMetric.getName().equals(metric.getName())) {
					Metric freshMetric = metricDAO.get(existingMetric.getId());
					return createTestMetric(freshTest, freshMetric);
				}
			}
			Metric freshMetric = metricDAO.create(metric);
			return createTestMetric(freshTest, freshMetric);
		}
	}

	@Override
	@Secured
	public Metric updateMetric(Test test, Metric metric) throws ServiceException {
		Test freshTest = testDAO.get(test.getId());
		TestMetric freshTestMetric = testMetricDAO.find(freshTest, metric);
		if (freshTestMetric == null) {
			throw new ServiceException(ServiceException.Codes.METRIC_NOT_IN_TEST, freshTest.getName(), freshTest.getId(), metric.getName());
		}
		return metricDAO.update(metric);
	}

	@Override
	public List<Metric> getTestMetrics(Test test) {
		Test t = testDAO.get(test.getId());
		return t.getSortedMetrics();
	}

	@Override
	@Secured
	public void removeMetric(Test test, Metric metric) throws ServiceException {
		Test freshTest = testDAO.get(test.getId());
		if (freshTest == null) {
			throw new ServiceException(ServiceException.Codes.TEST_NOT_FOUND, test.getId());
		}
		TestMetric freshTestMetric = testMetricDAO.find(freshTest, metric.getName());
		Metric freshMetric = freshTestMetric.getMetric();
		if (freshMetric.getTestMetrics().size() == 1) {
			if (!freshMetric.getValues().isEmpty()) {
				throw new ServiceException(ServiceException.Codes.METRIC_HAS_VALUES, freshMetric.getName());
			} else {
				testMetricDAO.remove(freshTestMetric);
				metricDAO.remove(freshMetric);
			}
		} else {
			testMetricDAO.remove(freshTestMetric);
		}
	}

	@Override
	public Metric getFullMetric(Long id) {
		Metric metric = metricDAO.get(id);
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
	public TestExecution getFullTestExecution(Long id) {
		return cloneAndFetch(testExecutionDAO.get(id), true, true, true, true, true);
	}

	@Override
	public List<TestExecution> getAllFullTestExecutions() {
		List<TestExecution> r = testExecutionDAO.getAll();
		List<TestExecution> rcopy = new ArrayList<TestExecution>(r.size());
		for (TestExecution exec : r) {
			rcopy.add(getFullTestExecution(exec.getId()));
		}
		return rcopy;
	}

	@Override
	public List<TestExecution> getExecutionsByTest(Long testId) {
		return testExecutionDAO.getByTest(testId);
	}

	@Override
	@Secured
	public TestExecution updateTestExecution(TestExecution anExec) throws ServiceException {
		TestExecution execEntity = testExecutionDAO.get(anExec.getId());
		if (execEntity == null) {
			throw new ServiceException(ServiceException.Codes.TEST_EXECUTION_NOT_FOUND, anExec.getId());
		}
		for (TestExecutionTag interObj : execEntity.getTestExecutionTags()) {
			testExecutionTagDAO.remove(interObj);
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
	@Secured
	public TestExecutionParameter updateParameter(TestExecutionParameter tep) throws ServiceException {
		TestExecution exec = testExecutionDAO.get(tep.getTestExecution().getId());
		if (exec == null) {
			throw new ServiceException(ServiceException.Codes.TEST_EXECUTION_NOT_FOUND, tep.getTestExecution().getId());
		}
		if (testExecutionParameterDAO.hasTestParam(exec.getId(), tep)) {
			throw new ServiceException(ServiceException.Codes.PARAMETER_EXISTS, tep.getName());
		}

		return testExecutionParameterDAO.update(tep);
	}

	@Override
	public TestExecutionParameter getFullParameter(Long paramId) {
		TestExecutionParameter p = testExecutionParameterDAO.get(paramId);
		if (p == null) {
			return null;
		}

		TestExecutionParameter pclone = p.clone();
		pclone.setTestExecution(p.getTestExecution().clone());

		return pclone;
	}

	@Override
	@Secured
	public void removeParameter(TestExecutionParameter tep) throws ServiceException {
		TestExecution exec = testExecutionDAO.get(tep.getTestExecution().getId());
		if (exec == null) {
			throw new ServiceException(ServiceException.Codes.TEST_EXECUTION_NOT_FOUND, tep.getTestExecution().getId());
		}
		TestExecutionParameter tepRemove = testExecutionParameterDAO.get(tep.getId());
		testExecutionParameterDAO.remove(tepRemove);
	}

	@Override
	@Secured
	public Value addValue(Value value) throws ServiceException {
		TestExecution exec = testExecutionDAO.get(value.getTestExecution().getId());
		if (exec == null) {
			throw new ServiceException(ServiceException.Codes.TEST_EXECUTION_NOT_FOUND, value.getTestExecution().getId());
		}
		Metric metric = null;
		if (value.getMetric().getId() != null) {
			metric = metricDAO.get(value.getMetric().getId());
		} else {
			List<Metric> metrics = metricDAO.getMetricByNameAndGroup(value.getMetric().getName(), exec.getTest()
					.getGroupId());
			if (metrics.size() > 0) {
				metric = metricDAO.get(metrics.get(0).getId());
			}
		}
		if (metric == null) {
			throw new ServiceException(ServiceException.Codes.METRIC_NOT_FOUND, value.getMetric().getId());
		}
		value.setTestExecution(exec);
		value.setMetric(metric);
		// check if other values for given metric exist, if yes, we can only add one if both old and new one have at least one parameter
		List<Value> existingValuesForMetric = valueDAO.find(exec.getId(), metric.getId());
		if (!existingValuesForMetric.isEmpty()) {
			for (Value v : existingValuesForMetric) {
				if (!v.hasParameters()) {
					throw new ServiceException(ServiceException.Codes.UNPARAMETRIZED_MULTI_VALUE);
				}
			}
			if (!value.hasParameters()) {
				throw new ServiceException(ServiceException.Codes.UNPARAMETRIZED_MULTI_VALUE);
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
	@Secured
	public Value updateValue(Value value) throws ServiceException {
		TestExecution exec = testExecutionDAO.get(value.getTestExecution().getId());
		if (exec == null) {
			throw new ServiceException(ServiceException.Codes.TEST_EXECUTION_NOT_FOUND, value.getTestExecution().getId());
		}
		Value oldValue = valueDAO.get(value.getId());
		if (oldValue == null) {
			throw new ServiceException(ServiceException.Codes.VALUE_NOT_FOUND, value.getId());
		}
		Value freshValue = valueDAO.update(value);
		Value freshValueClone = freshValue.clone();
		freshValueClone.setMetric(freshValue.getMetric().clone());
		freshValueClone.getMetric().setTestMetrics(null);
		freshValueClone.getMetric().setValues(null);
		UpdateSet<ValueParameter> updateSet = EntityUtils.updateSet(oldValue.getParameters(), value.getParameters());
		if (!updateSet.removed.isEmpty()) {
			throw new ServiceException(ServiceException.Codes.STALE_COLLECTION, updateSet.removed);
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
			valueParameterDAO.remove(vp);
		}
		freshValueClone.setParameters(newParams.isEmpty() ? null : newParams);
		return freshValueClone;
	}

	@Override
	@Secured
	public void removeValue(Value value) throws ServiceException {
		TestExecution exec = testExecutionDAO.get(value.getTestExecution().getId());
		if (exec == null) {
			throw new ServiceException(ServiceException.Codes.TEST_EXECUTION_NOT_FOUND, value.getTestExecution().getId());
		}
		Value v = valueDAO.get(value.getId());
		for (ValueParameter vp : v.getParameters()) {
			valueParameterDAO.remove(vp);
		}
		valueDAO.remove(v);
	}

	@Override
	public List<Test> getAllTests() {
		return testDAO.getAll();
	}

	@Override
	public List<Metric> getAllMetrics(Long testId) {
		return metricDAO.getMetricByTest(testId);
	}

	@Override
	public List<String> getTestsByPrefix(String prefix) {
		List<Test> tests = testDAO.findByUIDPrefix(prefix);
		List<String> testuids = new ArrayList<String>();
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
		for (TestExecution testExecutionItem : testExecutions) {
			TestExecution testExecution = testExecutionDAO.get(testExecutionItem.getId());
			if (testExecution == null) {
				continue;
			}

			List<TestExecutionTag> testExecutionTags = new ArrayList<TestExecutionTag>();
			testExecutionTags.addAll(testExecution.getTestExecutionTags());
			for (String tagName : tags) {
				if (!testExecution.getTags().contains(tagName)) {
					Tag tag = tagDAO.findByName(tagName);
					if (tag == null) {
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
	public void removeTagsFromTestExecutions(Collection<String> tags, Collection<TestExecution> testExecutions) {
		for (TestExecution testExecutionItem : testExecutions) {
			TestExecution testExecution = testExecutionDAO.get(testExecutionItem.getId());
			if (testExecution == null) {
				continue;
			}

			List<TestExecutionTag> testExecutionTags = new ArrayList<TestExecutionTag>();
			for (TestExecutionTag testExecutionTag : testExecution.getTestExecutionTags()) {
				if (tags.contains(testExecutionTag.getTagName())) {
					testExecutionTagDAO.remove(testExecutionTag);
				} else {
					testExecutionTags.add(testExecutionTag);
				}
			}

			testExecution.setTestExecutionTags(testExecutionTags);
			testExecutionDAO.update(testExecution);
		}
	}

	@Override
	public Test getTest(Long id) {
		return testDAO.get(id);
	}

	@Override
	public void addSubscriber(User user, Test test) {
		Test freshTest = testDAO.get(test.getId());
		User freshUser = userDAO.get(user.getId());

		Collection<User> testSubscribers = freshTest.getSubscribers();
		if(testSubscribers.contains(freshUser)) {
			return;
		}

		testSubscribers.add(freshUser);
		testDAO.update(freshTest);
	}

	@Override
	public void removeSubscriber(User user, Test test) {
		Test freshTest = testDAO.get(test.getId());

		Collection<User> testSubscribers = new ArrayList<>(freshTest.getSubscribers());
		for (User testSubscriber : freshTest.getSubscribers()) {
			if (testSubscriber.getId().equals(user.getId())) {
				testSubscribers.remove(testSubscriber);
			}
		}

		test.setSubscribers(testSubscribers);
		testDAO.update(test);
	}

	@Override
	public boolean isUserSubscribed(User user, Test test) {
		Test freshTest = testDAO.get(test.getId());
		Collection<User> testSubscribers = freshTest.getSubscribers();
		for (User testSubscriber : testSubscribers) {
			if (testSubscriber.getId().equals(user.getId())) {
				return true;
			}
		}

		return false;
	}

	private TestMetric createTestMetric(Test test, Metric metric) {
		Metric existingMetric = metricDAO.get(metric.getId());
		TestMetric tm = new TestMetric();
		tm.setMetric(existingMetric);
		tm.setTest(test);
		return testMetricDAO.create(tm);
	}

	private TestExecution cloneAndFetch(TestExecution exec, boolean fetchTest, boolean fetchParameters,
										boolean fetchTags, boolean fetchValues,
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
}
