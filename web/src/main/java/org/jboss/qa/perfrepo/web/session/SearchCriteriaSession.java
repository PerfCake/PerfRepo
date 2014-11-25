package org.jboss.qa.perfrepo.web.session;

import org.jboss.qa.perfrepo.model.to.TestExecutionSearchTO;
import org.jboss.qa.perfrepo.model.to.TestSearchTO;
import org.jboss.qa.perfrepo.model.util.ExecutionSort;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Session storage for search criteria.
 *
 * @author Michal Linhard (mlinhard@redhat.com)
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

			//do not leave empty criteria - it would cause load of all test executions
			//limit the amount by date <-1 week, now>
			Calendar executedAfter = Calendar.getInstance();
			executedAfter.add(Calendar.DATE, -7);
			executedAfter.set(Calendar.HOUR_OF_DAY, 0);
			executedAfter.set(Calendar.MINUTE, 0);
			executedAfter.set(Calendar.SECOND, 0);

			executionSearchCriteria.setStartedFrom(executedAfter.getTime());
		}
		return executionSearchCriteria;
	}

	public void clearExecutionSearchCriteria() {
		executionSearchCriteria = new TestExecutionSearchTO();
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
