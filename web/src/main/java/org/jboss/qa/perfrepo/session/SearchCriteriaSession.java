package org.jboss.qa.perfrepo.session;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import org.jboss.qa.perfrepo.model.to.TestExecutionSearchTO;
import org.jboss.qa.perfrepo.model.to.TestSearchTO;

/**
 * Session storage for search criteria.
 * 
 * @author Michal Linhard (mlinhard@redhat.com)
 * 
 */
@Named(value = "searchCriteriaSession")
@SessionScoped
public class SearchCriteriaSession implements Serializable {

   private TestExecutionSearchTO executionSearchCriteria;

   private TestSearchTO testSearchCriteria;

   public TestExecutionSearchTO getExecutionSearchCriteria() {
      if (executionSearchCriteria == null) {
         executionSearchCriteria = new TestExecutionSearchTO();
      }
      return executionSearchCriteria;
   }

   public TestSearchTO getTestSearchCriteria() {
      if (testSearchCriteria == null) {
         testSearchCriteria = new TestSearchTO();
      }
      return testSearchCriteria;
   }

}
