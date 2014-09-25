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
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.jboss.qa.perfrepo.model.report.Report;
import org.jboss.qa.perfrepo.service.ReportService;
import org.jboss.qa.perfrepo.service.exceptions.ServiceException;
import org.jboss.qa.perfrepo.viewscope.ViewScoped;

/**
 * Controller for /repo/reports
 * 
 * @author Michal Linhard (mlinhard@redhat.com)
 * @author Jiri Holusa (jholusa@redhat.com)
 * 
 */
@Named("reportList")
@ViewScoped
public class ReportListController extends BaseController {

   private static final Logger log = Logger.getLogger(ReportListController.class);

   @Inject
   private ReportService reportService;

   private List<Report> savedReports;

   public List<Report> getSavedReports() {
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
      savedReports = new ArrayList<Report>();

      savedReports = reportService.getAllUsersReports();

      // when all types of reports have been added
      Collections.sort(savedReports);
   }

   public void remove(Report itemToRemove) {
      if (itemToRemove == null) {
         throw new IllegalStateException("Item to remove is null");
      }

      try {
         reportService.removeReport(itemToRemove.getId());
      } catch (ServiceException e) {
         log.error("Error while removing report " + itemToRemove.getId(), e);
         addMessageFor(e);
      }

      updateSavedReports();
   }
}
