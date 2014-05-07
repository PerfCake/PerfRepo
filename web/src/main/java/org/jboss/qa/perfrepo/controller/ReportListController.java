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
package org.jboss.qa.perfrepo.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.jboss.qa.perfrepo.service.ReportService;
import org.jboss.qa.perfrepo.service.ServiceException;
import org.jboss.qa.perfrepo.session.UserSession;
import org.jboss.qa.perfrepo.viewscope.ViewScoped;

/**
 * Controller for /repo/reports
 * 
 * @author Michal Linhard (mlinhard@redhat.com)
 * 
 */
@Named("reportList")
@ViewScoped
public class ReportListController extends ControllerBase {

   private static final Logger log = Logger.getLogger(ReportListController.class);

   private static final Comparator<ReportItem> COMPARE_NAME = new Comparator<ReportListController.ReportItem>() {
      @Override
      public int compare(ReportItem o1, ReportItem o2) {
         return o1.getName().compareTo(o2.getName());
      }
   };

   public class ReportItem {
      private String id;
      private String name;
      private String type;
      private String link;

      public ReportItem(String id, String name, String type, String link) {
         super();
         this.id = id;
         this.name = name;
         this.type = type;
         this.link = link;
      }

      public String getId() {
         return id;
      }

      public String getName() {
         return name;
      }

      public String getType() {
         return type;
      }

      public String getLink() {
         return link;
      }
   }

   @Inject
   private ReportService reportService;

   private List<ReportItem> savedReports;

   public List<ReportItem> getSavedReports() {
      return savedReports;
   }

   /**
    * called on preRenderView
    */
   public void preRender() throws Exception {
      reloadSessionMessages();
      if (savedReports == null) {
         updateSavedReports();
      }
   }

   private void updateSavedReports() {
      savedReports = new ArrayList<ReportItem>();
      addMetricReports();

      // when all types of reports have been added
      Collections.sort(savedReports, COMPARE_NAME);
   }

   private void addMetricReports() {
      for (String reportId : reportService.getAllReportIds()) {
         Map<String, String> props = reportService.getReportProperties(reportId);
         savedReports.add(new ReportItem(reportId, props.get("name"), props.get("type"), props.get("link")));
      }
   }

   public void remove(ReportItem itemToRemove) {
      if (itemToRemove != null) {
         try {
            reportService.removeReport(itemToRemove.getId());
         } catch (ServiceException e) {
            log.error("Error while removing report " + itemToRemove.getId(), e);
            addMessageFor(e);
         }
         updateSavedReports();
      } else {
         throw new IllegalStateException("item to remove is null");
      }
   }
}
