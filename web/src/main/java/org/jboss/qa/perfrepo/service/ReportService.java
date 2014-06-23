package org.jboss.qa.perfrepo.service;

import org.jboss.qa.perfrepo.model.report.Report;

import java.util.List;
import java.util.Map;

/**
 * // TODO: Document this
 *
 * @author Jiri Holusa <jholusa@redhat.com>
 */
public interface ReportService {

   public List<Report> getAllUsersReports();

   public void removeReport(Long id) throws ServiceException;

   public Report createReport(Report report);

   public Report updateReport(Report report);

   public Report getReport(Long id);

   public Long getMaxId();
}
