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
package org.jboss.qa.perfrepo.web.service.exceptions;

import javax.ejb.ApplicationException;

/**
 * Exception in service layer.
 *
 * @author Michal Linhard (mlinhard@redhat.com)
 */
@ApplicationException(rollback = true)
public class ServiceException extends Exception {

	private static final long serialVersionUID = 4888320719223847688L;

	public interface Codes {
		static final int TEST_UID_EXISTS = 100;
		static final int TEST_NOT_NULL = 110;
		static final int TEST_EXECUTION_NOT_FOUND = 200;
		static final int TEST_EXECUTION_NOT_FOUND_ADD_ATTACHMENT = 210;
		static final int TEST_EXECUTION_NOT_FOUND_REMOVE_ATTACHMENT = 220;
		static final int METRIC_NOT_IN_TEST = 300;
		static final int METRIC_NOT_FOUND = 400;
		static final int METRIC_SHARING_ONLY_IN_GROUP = 500;
		static final int METRIC_EXISTS = 600;
		static final int TEST_NOT_FOUND = 700;
		static final int TEST_UID_NOT_FOUND = 800;
		static final int METRIC_HAS_VALUES = 900;
		static final int VALUE_NOT_FOUND = 1000;
		static final int STALE_COLLECTION = 1100;
		static final int UNPARAMETRIZED_MULTI_VALUE = 1200;
		static final int PARAMETER_EXISTS = 1400;
		static final int USER_NOT_FOUND = 1500;
		static final int NOT_YOU = 1600;
		static final int USERNAME_ALREADY_EXISTS = 1700;
		static final int PASSWORD_IS_EMPTY = 1800;
		static final int PASSWORD_DOESNT_MATCH = 1900;
	}

	private int code;
	private Object[] params;

	public ServiceException(int code, Object... params) {
		this.code = code;
		this.params = params;
	}

	public Object[] getParams() {
		return params;
	}

	public int getCode() {
		return code;
	}
}
