package org.jboss.qa.perfrepo.web.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.qa.perfrepo.model.Metric;
import org.jboss.qa.perfrepo.model.Test;
import org.jboss.qa.perfrepo.model.TestMetric;
import org.jboss.qa.perfrepo.model.auth.AccessType;
import org.jboss.qa.perfrepo.model.report.Report;
import org.jboss.qa.perfrepo.model.report.ReportProperty;
import org.jboss.qa.perfrepo.model.to.MetricReportTO;
import org.jboss.qa.perfrepo.web.dao.MetricDAO;
import org.jboss.qa.perfrepo.web.dao.ReportDAO;
import org.jboss.qa.perfrepo.web.dao.TestDAO;
import org.jboss.qa.perfrepo.web.dao.TestExecutionDAO;
import org.jboss.qa.perfrepo.web.dao.TestMetricDAO;
import org.jboss.qa.perfrepo.web.security.Secured;
import org.jboss.qa.perfrepo.web.service.exceptions.ServiceException;

/**
 * Implements @link{ReportService}.
 *
 * @author Jiri Holusa <jholusa@redhat.com>
 */
@Named
@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ReportServiceBean implements ReportService {

   @Inject
   private ReportDAO reportDAO;

   @Inject
   private TestDAO testDAO;

   @Inject
   private MetricDAO metricDAO;

   @Inject
   private TestMetricDAO testMetricDAO;

   @Inject
   private TestExecutionDAO testExecutionDAO;

   @Inject
   private UserService userService;

   @Override
   public List<Report> getAllUsersReports() {
      return getAllReports(userService.getLoggedUser().getUsername());
   }

   @Override
   @Secured
   public void removeReport(Report report) throws ServiceException {
      Report r = reportDAO.get(report.getId());
      reportDAO.remove(r);
   }

   @Override
   public Report createReport(Report report) {
      return reportDAO.create(report);
   }

   @Override
   @Secured
   public Report updateReport(Report report) {
      return reportDAO.update(report);
   }

   @Override
   @Secured(accessType=AccessType.READ)
   public Report getFullReport(Report report) {
      Report freshReport  = reportDAO.get(report.getId());
      if(freshReport == null) {
         return null;
      }

      Map<String, ReportProperty> clonedReportProperties = new HashMap<String, ReportProperty>();

      for(String propertyKey: freshReport.getProperties().keySet()) {
         clonedReportProperties.put(propertyKey, freshReport.getProperties().get(propertyKey).clone());
      }

      Report result = freshReport.clone();
      result.setProperties(clonedReportProperties);

      return result;
   }

   @Override
   public MetricReportTO.Response computeMetricReport(MetricReportTO.Request request) {
      MetricReportTO.Response response = new MetricReportTO.Response();
      for (MetricReportTO.ChartRequest chartRequest : request.getCharts()) {
         MetricReportTO.ChartResponse chartResponse = new MetricReportTO.ChartResponse();
         response.addChart(chartResponse);
         if (chartRequest.getTestUid() == null) {
            continue;
         } else {
            Test freshTest = testDAO.findByUid(chartRequest.getTestUid());
            if (freshTest == null) {
               // test uid supplied but doesn't exist - pick another test
               response.setSelectionTests(testDAO.getAll());
               continue;
            } else {
               freshTest = freshTest.clone();
               chartResponse.setSelectedTest(freshTest);
               if (chartRequest.getSeries() == null || chartRequest.getSeries().isEmpty()) {
                  continue;
               }
               for (MetricReportTO.SeriesRequest seriesRequest : chartRequest.getSeries()) {
                  if (seriesRequest.getName() == null) {
                     throw new IllegalArgumentException("series has null name");
                  }
                  MetricReportTO.SeriesResponse seriesResponse = new MetricReportTO.SeriesResponse(seriesRequest.getName());
                  chartResponse.addSeries(seriesResponse);
                  if (seriesRequest.getMetricName() == null) {
                     continue;
                  }
                  TestMetric testMetric = testMetricDAO.find(freshTest, seriesRequest.getMetricName());
                  if (testMetric == null) {
                     chartResponse.setSelectionMetrics(metricDAO.getMetricByTest(freshTest.getId()));
                     continue;
                  }
                  Metric freshMetric = testMetric.getMetric().clone();
                  freshMetric.setTestMetrics(null);
                  freshMetric.setValues(null);
                  seriesResponse.setSelectedMetric(freshMetric);
                  List<MetricReportTO.DataPoint> datapoints = testExecutionDAO.searchValues(freshTest.getId(), seriesRequest.getMetricName(),
                                                                                            seriesRequest.getTags(), request.getLimitSize());
                  if (datapoints.isEmpty()) {
                     continue;
                  }
                  Collections.reverse(datapoints);
                  seriesResponse.setDatapoints(datapoints);
               }

               for (MetricReportTO.BaselineRequest baselineRequest : chartRequest.getBaselines()) {
                  if (baselineRequest.getName() == null) {
                     throw new IllegalArgumentException("baseline has null name");
                  }
                  MetricReportTO.BaselineResponse baselineResponse = new MetricReportTO.BaselineResponse(baselineRequest.getName());
                  chartResponse.addBaseline(baselineResponse);
                  if (baselineRequest.getMetricName() == null) {
                     continue;
                  }
                  TestMetric testMetric = testMetricDAO.find(freshTest, baselineRequest.getMetricName());
                  if (testMetric == null) {
                     chartResponse.setSelectionMetrics(metricDAO.getMetricByTest(freshTest.getId()));
                     continue;
                  }
                  Metric freshMetric = testMetric.getMetric().clone();
                  freshMetric.setTestMetrics(null);
                  freshMetric.setValues(null);
                  baselineResponse.setSelectedMetric(freshMetric);
                  baselineResponse.setExecId(baselineRequest.getExecId());
                  baselineResponse.setValue(testExecutionDAO.getValueForMetric(baselineRequest.getExecId(), baselineRequest.getMetricName()));
               }

            }
         }
      }
      return response;
   }

   private List<Report> getAllReports(String username) {
      List<Report> result = new ArrayList<Report>();
      result.addAll(reportDAO.findReportsByUser(username));

      return result;
   }
}
