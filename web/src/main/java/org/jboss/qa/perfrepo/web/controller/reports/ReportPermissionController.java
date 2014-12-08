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
package org.jboss.qa.perfrepo.web.controller.reports;

import org.jboss.qa.perfrepo.model.auth.AccessLevel;
import org.jboss.qa.perfrepo.model.auth.AccessType;
import org.jboss.qa.perfrepo.model.auth.Permission;
import org.jboss.qa.perfrepo.model.report.Report;
import org.jboss.qa.perfrepo.model.user.Group;
import org.jboss.qa.perfrepo.model.user.User;
import org.jboss.qa.perfrepo.web.controller.BaseController;
import org.jboss.qa.perfrepo.web.security.AuthorizationService;
import org.jboss.qa.perfrepo.web.service.ReportService;
import org.jboss.qa.perfrepo.web.service.UserService;
import org.jboss.qa.perfrepo.web.viewscope.ViewScoped;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Named
@ViewScoped
public class ReportPermissionController extends BaseController {

	/**
	 *
	 */
	private static final long serialVersionUID = 5444130093765337514L;

	@Inject
	ReportService reportService;

	@Inject
	UserService userService;

	private Collection<Permission> permissions = new ArrayList<Permission>();

	private Collection<Permission> workingCopy = new ArrayList<Permission>();

	private String userId;

	private String groupId;

	private Permission permission = new Permission();

	private Long reportId;

	private boolean isPanelShown = false;

	private boolean userAuthorized = false;

	@Inject
	private AuthorizationService authorizationService;

	public AccessLevel[] getAccessLevels() {
		return AccessLevel.values();
	}

	public AccessType[] getAccessTypes() {
		return AccessType.values();
	}

	public Collection<Permission> getPermissions() {
		return permissions;
	}

	public void setPermissions(Collection<Permission> permissions) {
		this.permissions = permissions;
	}

	@PostConstruct
	public void initialize() {
		String reportIdParam = getRequestParam("reportId");
		reportId = reportIdParam != null ? Long.parseLong(reportIdParam) : null;
		permissions = reportService.getReportPermissions(new Report(reportId));
		if (reportId != null) {
			userAuthorized = authorizationService.isUserAuthorizedFor(AccessType.WRITE, new Report(reportId));
		} else {
			userAuthorized = true;
		}
	}

	public void clearWorkingCopy() {
		workingCopy.clear();
	}

	public String getPermissionGroup(Permission p) {
		if (AccessLevel.GROUP.equals(p.getLevel())) {
			Group group = userService.getGroup(p.getGroupId());
			return group.getName();
		} else {
			return null;
		}
	}

	public String getPermissionUser(Permission p) {
		if (AccessLevel.USER.equals(p.getLevel())) {
			User user = userService.getUser(p.getUserId());
			return user.getUsername();
		} else {
			return null;
		}
	}

	public List<User> getUsers() {
		return userService.getUsers();
	}

	public List<Group> getGroups() {
		return userService.getGroups();
	}

	public void clearPermission() {
		permission = new Permission();
		userId = "";
		groupId = "";
	}

	public void removePermission(Permission p) {
		permissions.remove(p);
	}

	public void addNewPermission() {
		if (AccessLevel.GROUP.equals(permission.getLevel())) {
			permission.setGroupId(Long.valueOf(groupId));
		} else if (AccessLevel.USER.equals(permission.getLevel())) {
			permission.setUserId(Long.valueOf(userId));
		}
		permissions.add(permission);
		permission = new Permission();
		userId = "";
		groupId = "";
	}

	public Collection<Permission> getWorkingCopy() {
		return workingCopy;
	}

	public void setWorkingCopy(Collection<Permission> workingCopy) {
		this.workingCopy = workingCopy;
	}

	public Long getReportId() {
		return reportId;
	}

	public void setReportId(Long reportId) {
		this.reportId = reportId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public Permission getPermission() {
		return permission;
	}

	public void setPermission(Permission permission) {
		this.permission = permission;
	}

	public void togglePanel() {
		if (isPanelShown) {
			isPanelShown = false;
		} else {
			isPanelShown = true;
		}
	}

	public boolean isPanelShown() {
		return isPanelShown;
	}

	public void setPanelShown(boolean isPanelShown) {
		this.isPanelShown = isPanelShown;
	}

	public boolean isUserAuthorized() {
		return userAuthorized;
	}

	public void setUserAuthorized(boolean userAuthorized) {
		this.userAuthorized = userAuthorized;
	}
}