package org.jboss.qa.perfrepo.service;

import org.jboss.qa.perfrepo.model.report.Report;
import org.jboss.qa.perfrepo.model.to.MetricReportTO;

import java.util.List;
import java.util.Map;

/**
 * Service layer for all operations related to reports.
 *
 * @author Jiri Holusa <jholusa@redhat.com>
 */
public interface ReportService {

   /**
    * Get all reports that associated (visible) with currently logged user.
    * @return
    */
   public List<Report> getAllUsersReports();

   /**
    * Removes report
    *
    * @param id
    * @throws ServiceException
    */
   public void removeReport(Long id) throws ServiceException;

   /**
    * Create new report
    *
    * @param report
    * @return
    */
   public Report createReport(Report report);

   /**
    * Update existing report
    *
    * @param report
    * @return
    */
   public Report updateReport(Report report);

   /**
    * Get's the next id that is available for a report
    *
    * @return
    */
   public Long getMaxId();

   /**
    * Computes metric report.
    *
    * @param request
    * @return response TO
    */
   public MetricReportTO.Response computeMetricReport(MetricReportTO.Request request);

   /**
    * Get report with all properties
    *
    * @param id
    * @return
    */
   public Report getFullReport(Long id);

}
