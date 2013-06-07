package org.jboss.qa.perfrepo.service;

import java.util.Collection;
import java.util.List;

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
public interface TestExecutionService {

   /**
    * TODO: comment
    * 
    * @param te
    * @return
    */
   TestExecution storeTestExecution(TestExecution te);

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
   Long addAttachment(TestExecutionAttachment attachment);

   /**
    * Get test execution attachment by id.
    * 
    * @param id
    * @return
    */
   TestExecutionAttachment getAttachment(Long id);

}
