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
package org.perfrepo.model.userproperty;

/**
 * User Property enumeration GroupFilter used by Test and Test Execution search algorithm.
 * @author Pavel Drozd
 *
 */
public enum GroupFilter {

	/**
	 * The search algorithm uses user groups to find the Test or Test Execution
	 */
	MY_GROUPS,

	/**
	 * The search algorithm uses all groups to find the Test or Test Execution
	 */
	ALL_GROUPS

}
