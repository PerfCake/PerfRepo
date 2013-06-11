package org.jboss.qa.perfrepo.service;

import java.util.Collection;
import java.util.List;

import org.jboss.qa.perfrepo.model.Metric;
import org.jboss.qa.perfrepo.model.Test;
import org.jboss.qa.perfrepo.model.TestExecution;
import org.jboss.qa.perfrepo.model.TestExecutionAttachment;
import org.jboss.qa.perfrepo.model.TestExecutionSearchTO;

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
   TestExecution storeTestExecution(TestExecution testExecution) throws ServiceException;

   /**
    * TODO: comment
    * 
    * @param search
    * @return
    */
   List<TestExecution> searchTestExecutions(TestExecutionSearchTO search);

   /**
    * TODO: comment
    * 
    * @param ids
    * @return
    */
   List<TestExecution> getTestExecutions(Collection<Long> ids);

   /**
    * Add attachment to the test execution. The {@link TestExecution} object referred by attachment
    * needs to be an empty object with only id set.
    * 
    * @param attachment
    * @return id of newly created attachment
    */
   Long addAttachment(TestExecutionAttachment attachment) throws ServiceException;

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
    */
   Test createTest(Test test);

   TestExecution getTestExecution(Long id);

   List<TestExecution> findAllTestExecutions();

   List<TestExecution> findExecutionsByTest(Long testId);

   /**
    * Add metric to given test.
    * 
    * @param test
    * @param metric
    * @return
    */
   Metric addMetric(Test test, Metric metric);

   Metric updateMetric(Metric metric);

   Test getTest(Long id);

   void deleteTest(Test test);

   Test updateTest(Test test);

   List<Test> findAllTests();

   Test getOrCreateTest(Test test);

   Metric getMetric(Long id);

   List<Metric> getAllMetrics();

   void deleteMetric(Metric metric);
   
   TestExecution updateTestExecution(TestExecution testExecution) throws ServiceException;
}
