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
package org.perfrepo.web.session;

import java.io.Serializable;
import java.util.Calendar;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.perfrepo.model.to.TestExecutionSearchTO;
import org.perfrepo.model.to.TestSearchTO;
import org.perfrepo.model.util.ExecutionSort;

/**
 * Session storage for search criteria.
 *
 * @author Michal Linhard (mlinhard@redhat.com)
 */
@Named(value = "searchCriteriaSession")
@SessionScoped
public class SearchCriteriaSession implements Serializable {

	private static final long serialVersionUID = 9050986914006178498L;

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
