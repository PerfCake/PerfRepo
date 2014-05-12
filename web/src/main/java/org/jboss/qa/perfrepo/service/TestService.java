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

/**
 * 
 * Main facade to the test execution services.
 * 
 * @author Pavel Drozd (pdrozd@redhat.com)
 * @author Michal Linhard (mlinhard@redhat.com)
 */
public interface TestService {

   /**
    * Stores a new test execution.
    * 
    * The new test execution needs to contain test UID and the referred test has to exist.
    * 
    * @param testExecution New test execution.
    * @return Created test execution. Contains database IDs.
    * @throws ServiceException
    */
   public TestExecution createTestExecution(TestExecution testExecution) throws ServiceException;

   /**
    * Returns list of TestExecutionss according to criteria defined by TestExecutionSearchTO
    * 
    * @param search
    * @return
    */
   public List<TestExecution> searchTestExecutions(TestExecutionSearchTO search);

   /**
    * Returns list of TestExecutionss according to criteria defined by TestExecutionSearchTO grouped
    * by job ID
    * 
    * @param search
    * @return
    */
   public List<TestExecution> searchTestExecutionsGroupedByJobId(TestExecutionSearchTO search);

   /**
    * Returns list of Tests according to criteria defined by TestSearchTO
    * 
    * @param search
    * @return
    */
   public List<Test> searchTest(TestSearchTO search);

   /**
    * Get list of full test executions.
    * 
    * @param ids
    * @return
    */
   public List<TestExecution> getFullTestExecutions(List<Long> ids);

   /**
    * Add attachment to the test execution. The {@link TestExecution} object referred by attachment
    * needs to be an empty object with only id set.
    * 
    * @param attachment
    * @return id of newly created attachment
    */
   public Long addAttachment(TestExecutionAttachment attachment) throws ServiceException;

   /**
    * Delete attachment.
    * 
    * @param attachment
    * @throws ServiceException
    */
   public void deleteAttachment(TestExecutionAttachment attachment) throws ServiceException;

   /**
    * Get test execution attachment by id.
    * 
    * @param id
    * @return
    */
   public TestExecutionAttachment getAttachment(Long id);

   /**
    * Create a new test with collection of metrics.Group id of the new test needs to be one
    * of the current user's roles.
    * 
    * @param test
    * @return
    * @throws ServiceException
    */
   public Test createTest(Test test) throws ServiceException;

   /**
    * Delete a test execution with all it's subobjects.
    * 
    * @param testExecution
    * @throws ServiceException
    */
   public void deleteTestExecution(TestExecution testExecution) throws ServiceException;

   /**
    * Get {@link TestExecution} with all details.
    * 
    * @param id
    * @return
    */
   public TestExecution getFullTestExecution(Long id);

   /**
    * Returns all test executions with all information.
    * @return
    */
   public List<TestExecution> getAllFullTestExecutions();

   /**
    * Returns all test executions of the specified test.
    * @param testId
    * @return
    */
   public List<TestExecution> findExecutionsByTest(Long testId);

   /**
    * Returns TestExecutions by Test id and job Id
    * 
    * @param testId
    * @param jobId
    * @return
    */
   public List<TestExecution> getFullTestExecutionsByTestAndJob(Long testId, Long jobId);

   /**
    * Returns TestExecutions by tags and test uids
    * @param tags
    * @param testUIDs
    * @return
    */
   public List<TestExecution> getTestExecutions(List<String> tags, List<String> testUIDs);

   /**
    * Add metric to given test.
    * 
    * @param test
    * @param metric
    * @return
    * @throws ServiceException
    */
   public TestMetric addMetric(Test test, Metric metric) throws ServiceException;

   /**
    * Update metric.
    * 
    * @param test
    * @param metric
    * @return Updated metric
    * @throws ServiceException
    */
   public Metric updateMetric(Test test, Metric metric) throws ServiceException;

   /**
    * Get test with all metrics but without executions.
    * 
    * @param testId Test id
    * @return Test
    */
   public Test getFullTest(Long testId);

   /**
    * Delete a test with all it's sub-objects. Use with caution!
    * 
    * @param test
    * @throws ServiceException
    */
   public void deleteTest(Test test) throws ServiceException;

   /**
    * Updates the test.
    *
    * @param test
    * @return
    */
   public Test updateTest(Test test);

   /**
    * Returns all tests with all information.
    *
    * @return
    */
   public List<Test> getAllFullTests();

   /**
    * Get metric with all associated tests (without details).
    * 
    * @param id
    * @return
    */
   public Metric getFullMetric(Long id);

   /**
    * Returns all metrics with all information.
    *
    * @return
    */
   public List<Metric> getAllFullMetrics();

   /**
    * Returns metrics which belong tests with defined group id and are not defined on the defined
    * test
    *
    * @param test
    * @return
    */
   public List<Metric> getAvailableMetrics(Test test);

   /**
    * Returns all metrics, which are defined on the Test
    * 
    * @return
    */
   public List<Metric> getTestMetrics(Test test);

   /**
    * Delete metric from the test.
    * 
    * @param test
    * @param metric
    * @throws ServiceException
    */
   public void deleteMetric(Test test, Metric metric) throws ServiceException;

   /**
    * Update only attributes name and started and collection of tags.
    * 
    * @param testExecution
    * @return fresh full test execution
    * @throws ServiceException
    */
   public TestExecution updateTestExecution(TestExecution testExecution) throws ServiceException;

   /**
    * Lock/unlock test execution.
    * 
    * @param anExec
    * @param locked
    * @return full test execution
    * @throws ServiceException
    */
   public TestExecution setExecutionLocked(TestExecution anExec, boolean locked) throws ServiceException;

   /**
    * Updates or creates TestExecutionParameter
    * 
    * @param tep TestExecutionParameter to update
    * @return
    * @throws ServiceException
    */
   public TestExecutionParameter updateParameter(TestExecutionParameter tep) throws ServiceException;

   /**
    * Removes TestExecutionParameter
    * 
    * @param tep
    * @return
    * @throws ServiceException
    */
   public void deleteParameter(TestExecutionParameter tep) throws ServiceException;

   /**
    * Creates new value.
    * 
    * @param value
    * @return
    * @throws ServiceException
    */
   public Value addValue(Value value) throws ServiceException;

   /**
    * Updates Test Execution Value and the set of it's parameters.
    * 
    * @param value
    * @return
    * @throws ServiceException
    */
   public Value updateValue(Value value) throws ServiceException;

   /**
    * Removes value from TestExecution
    * 
    * @param value
    * @throws ServiceException
    */
   public void deleteValue(Value value) throws ServiceException;

   /**
    * Computes metric report.
    * 
    * @param request
    * @return response TO
    */
   public MetricReportTO.Response computeMetricReport(MetricReportTO.Request request);

   /**
    * Get parameter and test execution.
    * 
    * @param paramId
    * @return
    */
   public TestExecutionParameter getFullParameter(Long paramId);

   /**
    * 
    * @return lists of tests for selection in UI.
    */
   public List<Test> getAllSelectionTests();

   /**
    * 
    * @param testId
    * @return All metrics for given testId
    */
   public List<Metric> getAllSelectionMetrics(Long testId);

   /**
    * Returns test uids matching prefix
    * @param prefix
    * @return
    */
   public List<String> getTestsByPrefix(String prefix);

   /**
    * Returns tags matching prefix
    * @param prefix
    * @return
    */
   public List<String> getTagsByPrefix(String prefix);

   /**
    * Returns test by test uid
    * @param uid
    * @return
    */
   public Test getTestByUID(String uid);

   /**
    * Perform mass operation. Adds tags to provided test executions.
    *
    * @param tags
    * @param testExecutions
    */
   public void addTagsToTestExecutions(Collection<String> tags, Collection<TestExecution> testExecutions);

   /**
    * Perform mass operation. Deletes tags from provided test executions.
    *
    * @param tags
    * @param testExecutions
    */
   public void deleteTagsFromTestExecutions(Collection<String> tags, Collection<TestExecution> testExecutions);

}
