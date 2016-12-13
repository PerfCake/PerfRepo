/**
 * PerfRepo
 * <p>
 * Copyright (C) 2015 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.perfrepo.web.controller.reports.tablecomparison;

import org.perfrepo.model.Test;
import org.perfrepo.model.auth.AccessType;
import org.perfrepo.model.auth.Permission;
import org.perfrepo.model.report.Report;
import org.perfrepo.web.controller.BaseController;
import org.perfrepo.web.controller.reports.ReportPermissionController;
import org.perfrepo.web.security.AuthorizationService;
import org.perfrepo.web.service.TestService;
import org.perfrepo.web.service.reports.TableComparisonReportServiceBean;
import org.perfrepo.web.viewscope.ViewScoped;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Controller for table comparison reports
 * Please refer to the description of the report in tablecomparison.xhtml to gain insight on how the report works.
 *
 * @author Jakub Markos (jmarkos@redhat.com)
 */
@Named("tableComparisonReportBean")
@ViewScoped
public class TableComparisonReportController extends BaseController {

   @Inject
   private TestService testService;

   @Inject
   private TableComparisonReportServiceBean reportService;

   @Inject
   private ReportPermissionController reportPermissionController;

   @Inject
   private AuthorizationService authorizationService;

   //properties bound to specific report being displayed
   private String name;
   private List<Group> groups;
   private boolean showConfiguration;
   private List<Test> testsForSelection;
   private boolean userAuthorized = false;

   /**
    * Called directly from template allowing to display messages.
    */
   public void init() {
      reloadSessionMessages();
   }

   @PostConstruct
   public void postConstruct() {
      reloadSessionMessages();
      if (name == null) {
         if (getReportId() != null) {
            load();
         } else {
            showConfiguration = true;
            name = "New table comparison report";
            addNewGroup();
            userAuthorized = true;
         }
      }
   }

   public void save() {
      Long reportId = getReportId();
      if (reportId == null) {
         reportId = reportService.create(name, groups, reportPermissionController.getPermissions());
      } else {
         reportId = reportService.update(reportId, name, groups, reportPermissionController.getPermissions());
      }
      redirect(reportId);
   }

   public void load() {
      Long reportId = getReportId();
      if (reportId == null) {
         throw new IllegalArgumentException("No report ID provided.");
      }

      Map.Entry<String, List<Group>> loadedReport;
      try {
         loadedReport = reportService.load(reportId);
         name = loadedReport.getKey();
         groups = loadedReport.getValue();
      } catch (IllegalArgumentException ex) {
         redirectWithMessage("/reports", ERROR, "page.report.error");
      } catch (Exception e) {
         if (e.getCause() instanceof SecurityException) {
            redirectWithMessage("/reports", ERROR, "page.report.permissionDenied");
         } else {
            redirectWithMessage("/reports", ERROR, "page.report.error");
         }
      }
      userAuthorized = authorizationService.isUserAuthorizedFor(AccessType.WRITE, new Report(reportId));
      updateGroups();
   }

   public void cloneReport() {
      setName("Clone of " + getName());
      Long reportId = reportService.create(name, groups, copyPermissions());
      redirect(reportId);
   }

   public void redirect(Long reportId) {
      if (reportId != null) {
         redirectWithMessage("/reports/tableComparisonReport/" + reportId, INFO, "page.reports.tableComparison.reportSaved");
      } else {
         redirectWithMessage("/reports", ERROR, "page.reports.tableComparison.reportNotSaved");
      }
   }

   /**
    * Helper method used for cloning the report. It copies permissions from the existing report.
    * @return Copied permissions
    */
   private List<Permission> copyPermissions() {
      Collection<Permission> permissions = reportPermissionController.getPermissions();
      List<Permission> clonedPermissions = new ArrayList<Permission>();
      for (Permission p : permissions) {
         Permission newPerm = p.clone();
         newPerm.setId(null);
         newPerm.setReport(null);
         clonedPermissions.add(newPerm);
      }
      return clonedPermissions;
   }

   public void updateGroups() {
      for (Group group : groups) {
         for (Comparison comparison : group.getComparisons()) {
            updateAllItemsInComparison(group, comparison);
         }
      }
   }

   public void updateAllItemsInComparison(Group group, Comparison comparison) {
      for (ComparisonItem comparisonItem : comparison.getComparisonItems()) {
         reportService.updateComparisonItem(comparison, comparisonItem);
      }
      reportService.updateComparison(group, comparison);
   }

   public void updateGroupsEvent(javax.faces.event.AjaxBehaviorEvent event) throws javax.faces.event.AbortProcessingException {
      updateGroups();
   }

   public List<Test> getTestsForSelection() {
      if (testsForSelection == null) {
         testsForSelection = testService.getAvailableTests().getResult();
         Collections.sort(testsForSelection, (o1, o2) -> o1.getName().compareTo(o2.getName()));
      }

      return testsForSelection;
   }

   public void addNewGroup() {
      if (groups == null) {
         groups = new ArrayList<>();
      }

      Group newGroup = new Group();
      Comparison newComparison = new Comparison();
      newComparison.addComparisonItem(new ComparisonItem());
      newGroup.setName("New Group");
      newGroup.addComparison(newComparison);
      groups.add(newGroup);
   }

   public void removeGroup(Group group) {
      groups.remove(group);
   }

   public void addNewComparison(Group group) {
      Comparison comparison = new Comparison();
      comparison.addComparisonItem(new ComparisonItem());
      group.addComparison(comparison);
   }

   public void removeComparison(Group group, Comparison comparison) {
      group.removeComparison(comparison);
   }

   public void addNewComparisonItem(Comparison comparison) {
      comparison.addComparisonItem(new ComparisonItem());
   }

   public void removeComparisonItem(Comparison comparison, ComparisonItem comparisonItem) {
      comparison.removeComparisonItem(comparisonItem);
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public List<Group> getGroups() {
      return groups;
   }

   public void setGroups(List<Group> groups) {
      this.groups = groups;
   }

   public boolean isShowConfiguration() {
      return showConfiguration;
   }

   public void setShowConfiguration(boolean showConfiguration) {
      this.showConfiguration = showConfiguration;
   }

   public boolean isUserAuthorized() {
      return userAuthorized;
   }

   public void setUserAuthorized(boolean userAuthorized) {
      this.userAuthorized = userAuthorized;
   }

   public Long getReportId() {
      return getRequestParamLong("reportId");
   }

}