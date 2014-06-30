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
    * @return List of {@link Report}
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
    * @return created {@link Report}
    */
   public Report createReport(Report report);

   /**
    * Update existing report
    *
    * @param report
    * @return updated {@link Report}
    */
   public Report updateReport(Report report);

   /**
    * Get's the next id that is available for a report
    *
    * @return next ID
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
    * @return {@link Report} with all attributes fetched
    */
   public Report getFullReport(Long id);

}
