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

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.jboss.qa.perfrepo.model.Metric;
import org.jboss.qa.perfrepo.model.Test;
import org.jboss.qa.perfrepo.model.TestExecution;
import org.jboss.qa.perfrepo.model.TestExecutionAttachment;
import org.jboss.qa.perfrepo.model.TestExecutionParameter;
import org.jboss.qa.perfrepo.model.TestExecutionTag;
import org.jboss.qa.perfrepo.model.TestMetric;
import org.jboss.qa.perfrepo.model.User;
import org.jboss.qa.perfrepo.model.UserProperty;
import org.jboss.qa.perfrepo.model.Value;
import org.jboss.qa.perfrepo.model.to.MetricReportTO;
import org.jboss.qa.perfrepo.model.to.TestExecutionSearchTO;
import org.jboss.qa.perfrepo.model.to.TestSearchTO;
import org.jboss.qa.perfrepo.service.ServiceException.Codes;

/**
 * 
 * Main facade to the test execution services.
 * 
 * @author Pavel Drozd (pdrozd@redhat.com)
 * @author Michal Linhard (mlinhard@redhat.com)
 */
public interface TestService extends Codes {

   /**
    * Stores a new test execution.
    * 
    * The new test execution needs to contain test UID and the referred test has to exist.
    * 
    * @param testExecution New test execution.
    * @return Created test execution. Contains database IDs.
    * @throws ServiceException
    */
   TestExecution createTestExecution(TestExecution testExecution) throws ServiceException;

   /**
    * Returns list of TestExecutionss according to criteria defined by TestExecutionSearchTO
    * 
    * @param search
    * @return
    */
   List<TestExecution> searchTestExecutions(TestExecutionSearchTO search);

   /**
    * Returns list of TestExecutionss according to criteria defined by TestExecutionSearchTO grouped
    * by job ID
    * 
    * @param search
    * @return
    */
   List<TestExecution> searchTestExecutionsGroupedByJobId(TestExecutionSearchTO search);

   /**
    * Returns list of Tests according to criteria defined by TestSearchTO
    * 
    * @param search
    * @return
    */
   List<Test> searchTest(TestSearchTO search);

   /**
    * Get list of full test executions.
    * 
    * @param ids
    * @return
    */
   List<TestExecution> getFullTestExecutions(List<Long> ids);

   /**
    * Get list of full tests.
    * 
    * @param ids
    * @return
    */
   List<Test> getFullTests(List<Long> ids);

   /**
    * Add attachment to the test execution. The {@link TestExecution} object referred by attachment
    * needs to be an empty object with only id set.
    * 
    * @param attachment
    * @return id of newly created attachment
    */
   Long addAttachment(TestExecutionAttachment attachment) throws ServiceException;

   /**
    * Delete attachment.
    * 
    * @param attachment
    * @throws ServiceException
    */
   void deleteAttachment(TestExecutionAttachment attachment) throws ServiceException;

   /**
    * Get test execution attachment by id.
    * 
    * @param id
    * @return
    */
   TestExecutionAttachment getAttachment(Long id);

   /**
    * Create a new test with collection of metrics. Eech metric will be created with
    * {@link TestService#storeMetric(Test, Metric)} method. Group id of the new test needs to be one
    * of the current user's roles.
    * 
    * @param test
    * @return
    * @throws ServiceException
    */
   Test createTest(Test test) throws ServiceException;

   /**
    * Delete a test execution with all it's subobjects.
    * 
    * @param testExecution
    * @throws ServiceException
    */
   void deleteTestExecution(TestExecution testExecution) throws ServiceException;

   /**
    * Get {@link TestExecution} with all details.
    * 
    * @param id
    * @return
    */
   TestExecution getFullTestExecution(Long id);

   List<TestExecution> getAllFullTestExecutions();

   List<TestExecution> findExecutionsByTest(Long testId);

   /**
    * Returns TestExecutions by Test id and job Id
    * 
    * @param testId
    * @param jobId
    * @return
    */
   public List<TestExecution> getFullTestExecutionsByTestAndJob(Long testId, Long jobId);

   /**
    * Add metric to given test.
    * 
    * @param test
    * @param metric
    * @return
    * @throws ServiceException
    */
   TestMetric addMetric(Test test, Metric metric) throws ServiceException;

   /**
    * Update metric.
    * 
    * @param test
    * @param metric
    * @return Updated metric
    * @throws ServiceException
    */
   Metric updateMetric(Test test, Metric metric) throws ServiceException;

   /**
    * Get test with all metrics but without executions.
    * 
    * @param testId Test id
    * @return Test
    */
   Test getFullTest(Long testId);

   /**
    * Delete a test with all it's sub-objects. Use with caution!
    * 
    * @param test
    * @throws ServiceException
    */
   void deleteTest(Test test) throws ServiceException;

   Test updateTest(Test test);

   List<Test> getAllFullTests();

   Test getOrCreateTest(Test test) throws ServiceException;

   /**
    * Get metric with all associated tests (without details).
    * 
    * @param id
    * @return
    */
   Metric getFullMetric(Long id);

   List<Metric> getAllFullMetrics();

   /**
    * Returns all metric by name prefix, which belong tests with defined group id
    * 
    * @param name
    * @param test
    * @return
    */
   List<Metric> getMetrics(String name, Test test);

   /**
    * Returns metrics which belong tests with defined group id and are not defined on the defined
    * test
    * 
    * @param name
    * @param test
    * @return
    */
   List<Metric> getAvailableMetrics(Test test);

   /**
    * Returns all metrics, which are defined on the Test
    * 
    * @return
    */
   List<Metric> getTestMetrics(Test test);

   /**
    * Delete metric from the test.
    * 
    * @param test
    * @param metric
    * @throws ServiceException
    */
   void deleteMetric(Test test, Metric metric) throws ServiceException;

   /**
    * Removes metric from defined test. The method removes only relation between test and metric.
    * The metric itself is not deleted.
    * 
    * @param test
    * @param metric
    */
   void deleteTestMetric(TestMetric tm);

   /**
    * Update only attributes name and started and collection of tags.
    * 
    * @param testExecution
    * @return fresh full test execution
    * @throws ServiceException
    */
   TestExecution updateTestExecution(TestExecution testExecution) throws ServiceException;

   /**
    * Lock/unlock test execution.
    * 
    * @param anExec
    * @param locked
    * @return full test execution
    * @throws ServiceException
    */
   TestExecution setExecutionLocked(TestExecution anExec, boolean locked) throws ServiceException;

   /**
    * Updates or creates TestExecutionParameter
    * 
    * @param tep TestExecutionParameter to update
    * @return
    * @throws ServiceException
    */
   TestExecutionParameter updateParameter(TestExecutionParameter tep) throws ServiceException;

   /**
    * Removes TestExecutionParameter
    * 
    * @param tep
    * @return
    * @throws ServiceException
    */
   void deleteParameter(TestExecutionParameter tep) throws ServiceException;

   /**
    * Removes TestExecutionParameter
    * 
    * @param tep
    * @return
    */
   void deleteTestExecutionTag(TestExecutionTag teg);

   /**
    * Creates new value.
    * 
    * @param value
    * @return
    * @throws ServiceException
    */
   Value addValue(Value value) throws ServiceException;

   /**
    * Updates Test Execution Value and the set of it's parameters.
    * 
    * @param value
    * @return
    * @throws ServiceException
    */
   Value updateValue(Value value) throws ServiceException;

   /**
    * Removes value from TestExecution
    * 
    * @param value
    * @throws ServiceException
    */
   void deleteValue(Value value) throws ServiceException;

   /**
    * Computes metric report.
    * 
    * @param request
    * @return response TO
    */
   MetricReportTO.Response computeMetricReport(MetricReportTO.Request request);

   /**
    * Get parameter and test execution.
    * 
    * @param paramId
    * @return
    */
   TestExecutionParameter getFullParameter(Long paramId);

   /**
    * 
    * @param userName
    * @return User with properties.
    */
   User getFullUser(String userName);

   /**
    * Set user property.
    * 
    * @param property
    * @throws ServiceException
    */
   UserProperty updateUserProperty(UserProperty property) throws ServiceException;

   /**
    * Deletes user property.
    * 
    * @param property
    * @throws ServiceException
    */
   void deleteUserProperty(UserProperty property) throws ServiceException;

   /**
    * Updates a set of user's properties in one transaction.
    * 
    * @param user
    * @param keysToRemove These properties will be removed
    * @param toUpdate These will be created or updated.
    * @throws ServiceException
    */
   void multiUpdateProperties(User user, Collection<String> keysToRemove, Map<String, String> toUpdate) throws ServiceException;

   /**
    * Create new user.
    * 
    * @param user
    * @return
    * @throws ServiceException
    */
   User createUser(User user) throws ServiceException;

   /**
    * Update user
    * 
    * @param user
    * @return
    * @throws ServiceException
    */
   User updateUser(User user) throws ServiceException;

   /**
    * Get value with metric and full execution and test.
    * 
    * @param valueId value id
    * @return value
    */
   Value getFullValue(Long valueId);

   /**
    * 
    * @return lists of tests for selection in UI.
    */
   List<Test> getAllSelectionTests();

   /**
    * 
    * @param testId
    * @return All possible execution params for given testId.
    */
   List<String> getAllSelectionExecutionParams(Long testId);

   /**
    * 
    * @param testId
    * @return All metrics for given testId
    */
   List<Metric> getAllSelectionMetrics(Long testId);

}
