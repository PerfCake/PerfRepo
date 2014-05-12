package org.jboss.qa.perfrepo.service;

import java.util.List;
import java.util.Map;

/**
 * // TODO: Document this
 *
 * @author Jiri Holusa <jholusa@redhat.com>
 * @since 4.0
 */
public interface ReportService {

   public static final String REPORT_KEY_PREFIX = "report.";

   public List<String> getAllReportIds();

   public void removeReport(String reportId) throws ServiceException;

   public void setReportProperties(String reportId, Map<String, String> props) throws ServiceException;

   public Map<String, String> getReportProperties(String reportId);

   public Map<String, String> getReportProperties(String userName, String reportId);

}
