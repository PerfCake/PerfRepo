package org.jboss.qa.perfrepo.service;

import java.util.Collection;
import java.util.List;

import org.jboss.qa.perfrepo.model.TestExecution;
import org.jboss.qa.perfrepo.model.TestExecutionSearchTO;

public interface TestExecutionService {   
   
   public TestExecution storeTestExecution(TestExecution te);
   
   public List<TestExecution> searchTestExecutions(TestExecutionSearchTO search);
   
   public List<TestExecution> getTestExecutions(Collection<Long> ids);
   
}
