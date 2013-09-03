package org.jboss.qa.perfrepo.session;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import org.jboss.qa.perfrepo.model.to.TestExecutionSearchTO;
import org.jboss.qa.perfrepo.model.to.TestSearchTO;
import org.jboss.qa.perfrepo.model.util.ExecutionSort;

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

   private ExecutionSort executionSearchSort;

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

   public ExecutionSort getExecutionSearchSort() {
      if (executionSearchSort == null) {
         executionSearchSort = ExecutionSort.TIME_DESC;
      }
      return executionSearchSort;
   }

   public void setExecutionSearchSort(ExecutionSort executionSearchSort) {
      this.executionSearchSort = executionSearchSort;
   }

}
