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
package org.jboss.qa.perfrepo.web.dao;

import org.jboss.qa.perfrepo.model.auth.Permission;
import org.jboss.qa.perfrepo.model.report.Report;

import javax.inject.Named;

import java.util.List;

/**
 * DAO for {@link org.jboss.qa.perfrepo.model.auth.Permission}
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
@Named
public class PermissionDAO extends DAO<Permission, Long> {

	/**
	 * Returns all report permission.
	 *
	 * @param reportId
	 * @return
	 */
	public List<Permission> getByReport(Long reportId) {
		Report report = new Report(reportId);
		return getAllByProperty("report", report);
	}

	/**
	 * Removes report permissions
	 *
	 * @param report
	 */
	public void removeReportPermissions(Long reportId) {
		List<Permission> permissions = getByReport(reportId);
		for (Permission p : permissions) {
			this.remove(p);
		}
	}
}
