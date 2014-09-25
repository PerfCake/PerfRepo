package org.jboss.qa.perfrepo.service;

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

import org.jboss.qa.perfrepo.dao.MetricDAO;
import org.jboss.qa.perfrepo.dao.ReportDAO;
import org.jboss.qa.perfrepo.dao.TestDAO;
import org.jboss.qa.perfrepo.dao.TestExecutionDAO;
import org.jboss.qa.perfrepo.dao.TestMetricDAO;
import org.jboss.qa.perfrepo.model.Metric;
import org.jboss.qa.perfrepo.model.Test;
import org.jboss.qa.perfrepo.model.TestMetric;
import org.jboss.qa.perfrepo.model.report.Report;
import org.jboss.qa.perfrepo.model.report.ReportProperty;
import org.jboss.qa.perfrepo.model.to.MetricReportTO;
import org.jboss.qa.perfrepo.service.exceptions.ServiceException;

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
      return getAllReports(userService.getLoggedUserName());
   }

   @Override
   public void removeReport(Long id) throws ServiceException {
      Report report = reportDAO.find(id);
      reportDAO.delete(report);
   }

   @Override
   public Report createReport(Report report) {
      return reportDAO.create(report);
   }

   @Override
   public Report updateReport(Report report) {
      return reportDAO.update(report);
   }

   @Override
   public Long getMaxId() {
      return reportDAO.findMaxId();
   }

   @Override
   public Report getFullReport(Long id) {
      Report report = reportDAO.find(id);
      if(report == null) {
         return null;
      }

      Map<String, ReportProperty> clonedReportProperties = new HashMap<String, ReportProperty>();

      for(String propertyKey: report.getProperties().keySet()) {
         clonedReportProperties.put(propertyKey, report.getProperties().get(propertyKey).clone());
      }

      Report result = report.clone();
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
               response.setSelectionTests(testDAO.findAll());
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
