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
package org.perfrepo.model.to;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.perfrepo.model.userproperty.GroupFilter;

/**
 * Test execution search criteria.
 *
 * @author Pavel Drozd (pdrozd@redhat.com)
 * @author Michal Linhard (mlinhard@redhat.com)
 */
public class TestExecutionSearchTO implements Serializable {

	private static final long serialVersionUID = -2274979571623499791L;
	//means "Test executed after"
	private Date startedFrom;
	//means "Test executed before"
	private Date startedTo;
	private String tags;
	private String testUID;
	private String testName;
	private List<ParamCriteria> parameters = new ArrayList<ParamCriteria>();
	private int limit;
	private GroupFilter groupFilter;

	//means "Test executed after"
	public Date getStartedFrom() {
		return startedFrom;
	}

	public void setStartedFrom(Date startedFrom) {
		this.startedFrom = startedFrom;
	}

	//means "Test executed before"
	public Date getStartedTo() {
		return startedTo;
	}

	public void setStartedTo(Date startedTo) {
		this.startedTo = startedTo;
	}

	public String getTestUID() {
		return testUID;
	}

	public void setTestUID(String testUID) {
		this.testUID = testUID;
	}

	public String getTestName() {
		return testName;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public List<ParamCriteria> getParameters() {
		return parameters;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public GroupFilter getGroupFilter() {
		return groupFilter;
	}

	public void setGroupFilter(GroupFilter groupFilter) {
		this.groupFilter = groupFilter;
	}

   public static class ParamCriteria implements Serializable {

      private static final long serialVersionUID = -2562642308678063396L;
      private String name;
      private String value;

      public String getName() {
         return name;
      }

      public void setName(String name) {
         this.name = name;
      }

      public String getValue() {
         return value;
      }

      public void setValue(String value) {
         this.value = value;
      }

      public boolean isDisplayed() {
         return isNameEmpty() || value == null || "".equals(value.trim()) || value.contains("%");
      }

      public boolean isNameEmpty() {
         return name == null || "".equals(name.trim());
      }
   }
}
