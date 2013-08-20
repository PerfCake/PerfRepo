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
package org.jboss.qa.perfrepo.controller.reports;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.jboss.qa.perfrepo.controller.ControllerBase;
import org.jboss.qa.perfrepo.model.Metric;
import org.jboss.qa.perfrepo.model.Test;
import org.jboss.qa.perfrepo.model.to.MetricReportTO.DataPoint;
import org.jboss.qa.perfrepo.model.to.MetricReportTO.Request;
import org.jboss.qa.perfrepo.model.to.MetricReportTO.Response;
import org.jboss.qa.perfrepo.model.to.MetricReportTO.SeriesRequest;
import org.jboss.qa.perfrepo.model.to.MetricReportTO.SeriesResponse;
import org.jboss.qa.perfrepo.model.to.MetricReportTO.SortType;
import org.jboss.qa.perfrepo.service.TestService;
import org.jboss.qa.perfrepo.util.Util;
import org.jboss.qa.perfrepo.viewscope.ViewScoped;
import org.jsflot.components.FlotChartClickedEvent;
import org.jsflot.components.FlotChartRendererData;
import org.jsflot.xydata.XYDataList;
import org.jsflot.xydata.XYDataPoint;
import org.jsflot.xydata.XYDataSetCollection;

/**
 * Shows development of metric based on a specific test execution parameter.
 * 
 * TODO: correct error reporting when wrong test_uid, metric_name or execution parameter is supplied
 * via URL param in GET.
 * 
 * @author Michal Linhard (mlinhard@redhat.com)
 * 
 */
@Named("metricReportBean")
@ViewScoped
public class MetricReportController extends ControllerBase {

   private static final long serialVersionUID = 1L;

   private static final Logger log = Logger.getLogger(MetricReportController.class);
   private static final DecimalFormat FMT = new DecimalFormat("##.####");
   private static final Pattern DPPATTERN = Pattern.compile("Exec ID\\: (\\d+)");

   @Inject
   private TestService testService;

   private SortType sortType = SortType.NUMBER;

   private XYDataSetCollection chartData;
   private FlotChartRendererData chart;
   private Double minValue = null;
   private Double maxValue = null;
   private DataPoint selectedDataPoint;
   //private DataPoint baselineDataPoint;

   private Long selectedTestId;
   private String selectedParam;
   private Response report;

   private List<SeriesRequest> seriesSpecs;

   public static class SeriesRequestExt extends SeriesRequest {

      public SeriesRequestExt(String name, String metricName, List<String> tags, Long selectedMetricId, String selectedTags) {
         super(name, metricName, tags);
         this.selectedMetricId = selectedMetricId;
         this.selectedTags = selectedTags;
      }

      private transient Long selectedMetricId;
      private transient String selectedTags;

      public Long getSelectedMetricId() {
         return selectedMetricId;
      }

      public void setSelectedMetricId(Long selectedMetricId) {
         this.selectedMetricId = selectedMetricId;
      }

      public String getSelectedTags() {
         return selectedTags;
      }

      public void setSelectedTags(String selectedTags) {
         this.selectedTags = selectedTags;
      }

      @Override
      public String toString() {
         return super.toString() + "(metricId=" + selectedMetricId + ", selectedTags=" + selectedTags + ")";
      }

   }

   @PostConstruct
   protected void init() {
      chart = new FlotChartRendererData();
      String reportQuery = getRequestParam("q");
      if (reportQuery == null) {
         generateRequestFromSingleParamSet();
      } else {
         generateRequestFromReportLinkParam(reportQuery);
      }
   }

   private void generateRequestFromSingleParamSet() {
      seriesSpecs = new ArrayList<SeriesRequest>();
      if (seriesSpecs.isEmpty()) {
         String tagStr = getRequestParam("tags");
         seriesSpecs.add(new SeriesRequestExt("1", getRequestParam("m"), Util.parseTags(tagStr), null, tagStr));
      }
      updateReport(new Request(getRequestParam("t"), getRequestParam("p"), (List<SeriesRequest>) seriesSpecs, sortType));
      updateSeriesSpecsFromReport();
   }

   private Long findMetricId(String metricName) {
      if (metricName == null) {
         return null;
      }
      if (report.getSelectionMetrics() == null) {
         return null;
      }
      for (Metric m : report.getSelectionMetrics()) {
         if (metricName.equals(m.getName())) {
            return m.getId();
         }
      }
      return null;
   }

   /**
    * Parse a special metric report request url param /repo/report/metric?q=1+1+1+....
    * 
    * @param reportLinkParam raw value of the q param
    * @return report request
    */
   private void generateRequestFromReportLinkParam(String reportQuery) {
      String[] tokens = reportQuery.split("\\|");
      try {
         if (tokens.length < 5 || (tokens.length - 2) % 3 != 0) {
            throw new Exception("query too short");
         }
         String testQuery = tokens[0];
         String paramQuery = tokens[1];
         seriesSpecs = new ArrayList<SeriesRequest>();
         int numSeries = (tokens.length - 2) / 3;
         for (int i = 0; i < numSeries; i++) {
            String seriesLabel = tokens[2 + i * 3];
            String seriesMetricName = tokens[3 + i * 3];
            String seriesTags = tokens[4 + i * 3];
            seriesSpecs.add(new SeriesRequestExt(seriesLabel, seriesMetricName, Util.parseTags(seriesTags), null, seriesTags));
         }
         updateReport(new Request(testQuery, paramQuery, seriesSpecs, sortType));
         updateSeriesSpecsFromReport();
      } catch (Exception e) {
         addMessage(ERROR, "page.metricreport.errorParsingReportQuery");
         log.error("Error while parsing report url", e);
      }
   }

   private void updateSeriesSpecsFromReport() {
      for (SeriesRequest s : seriesSpecs) {
         SeriesRequestExt s1 = (SeriesRequestExt) s;
         s1.setSelectedMetricId(findMetricId(s.getMetricName()));
      }
   }

   private void updateSeriesSpecsFromView() {
      for (SeriesRequest s : seriesSpecs) {
         SeriesRequestExt s1 = (SeriesRequestExt) s;
         s1.setMetricName(findSelectedMetricName(s1.getSelectedMetricId()));
         s1.setTags(Util.parseTags(s1.getSelectedTags()));
      }
   }

   public String getLinkToReport() {
      String reportQuery = generateReportQuery();
      return "/repo/reports/metric" + (reportQuery == null ? "" : "?q=" + reportQuery);
   }

   private String generateReportQuery() {
      if (report == null || report.getSelectedTest() == null || report.getSelectedParam() == null || seriesSpecs == null || seriesSpecs.isEmpty()) {
         return null;
      }
      StringBuffer s = new StringBuffer();
      s.append(report.getSelectedTest().getUid());
      s.append("|");
      s.append(report.getSelectedParam());
      for (SeriesRequest sreq : seriesSpecs) {
         SeriesRequestExt sreq1 = (SeriesRequestExt) sreq;
         if (sreq.getName() == null || sreq.getName().isEmpty() || sreq.getMetricName() == null || sreq.getMetricName().isEmpty()) {
            return null;
         }
         s.append("|");
         s.append(sreq.getName());
         s.append("|");
         s.append(sreq.getMetricName());
         s.append("|");
         s.append(sreq1.getSelectedTags());
      }
      String result = s.toString().trim();
      return result.endsWith("|") ? result + " |" : result;
   }

   /**
    * called on preRenderView
    */
   public void preRender() {
      reloadSessionMessages();
   }

   private void updateReport(Request request) {
      report = testService.computeMetricReport(request);
      chartData = recomputeChartData(report);
      if (chartData == null) {
         if (report.getSelectedTest() == null) {
            addMessage(INFO, "page.metricreport.selectTestUID");
         } else if (report.getSelectedParam() == null) {
            addMessage(INFO, "page.metricreport.selectExecParam");
         } else if (report.getProblematicSeries() != null) {
            addMessage(INFO, "page.metricreport.parameterNotUnique", report.getSelectedParam());
         } else {
            addMessage(INFO, "page.metricreport.noTestExecutions");
         }
      }
   }

   private String findSelectedTestUID(Long id) {
      for (Test t : report.getSelectionTests()) {
         if (id.equals(t.getId())) {
            return t.getUid();
         }
      }
      return null;
   }

   private String findSelectedMetricName(Long id) {
      for (Metric m : report.getSelectionMetrics()) {
         if (id.equals(m.getId())) {
            return m.getName();
         }
      }
      return null;
   }

   public void redraw() {
      // test uid was selected
      if (!isTestUidDisabled()) {
         String uid = findSelectedTestUID(selectedTestId);
         if (uid == null) {
            throw new IllegalStateException("couldn't find test with id " + selectedTestId);
         }
         redirect("/reports/metric/" + uid);
      } else if (!isExecParamDisabled()) {
         redirect("/reports/metric/" + report.getSelectedTest().getUid() + "/" + selectedParam);
      } else {
         updateSeriesSpecsFromView();
         Request request = new Request(report.getSelectedTest().getUid(), report.getSelectedParam(), seriesSpecs, sortType);
         updateReport(request);
      }
   }

   public void addSeries() {
      // test uid was selected
      if (!isTestUidDisabled()) {
         String uid = findSelectedTestUID(selectedTestId);
         if (uid == null) {
            throw new IllegalStateException("couldn't find test with id " + selectedTestId);
         }
         redirect("/reports/metric/" + uid);
      } else if (!isExecParamDisabled()) {
         redirect("/reports/metric/" + report.getSelectedTest().getUid() + "/" + selectedParam);
      } else {
         seriesSpecs.add(new SeriesRequestExt(Integer.toString(seriesSpecs.size() + 1), null, null, null, null));
      }
   }

   public void removeSeries(SeriesRequest seriesToRemove) {
      // test uid was selected
      if (!isTestUidDisabled()) {
         String uid = findSelectedTestUID(selectedTestId);
         if (uid == null) {
            throw new IllegalStateException("couldn't find test with id " + selectedTestId);
         }
         redirect("/reports/metric/" + uid);
      } else if (!isExecParamDisabled()) {
         redirect("/reports/metric/" + report.getSelectedTest().getUid() + "/" + selectedParam);
      } else {
         seriesSpecs.remove(seriesToRemove);
         Request request = new Request(report.getSelectedTest().getUid(), report.getSelectedParam(), sortType);
         for (SeriesRequest r : seriesSpecs) {
            SeriesRequestExt rExt = (SeriesRequestExt) r;
            request.addSeries(new SeriesRequest(rExt.getName(), findSelectedMetricName(rExt.getSelectedMetricId()), Util.parseTags(rExt.getSelectedTags())));
         }
         updateReport(request);
      }
   }

   public void chartActionListener(ActionEvent event) {
      if (event instanceof FlotChartClickedEvent) {
         FlotChartClickedEvent flotEvent = (FlotChartClickedEvent) event;
         selectDataPoint(flotEvent.getClickedDataPoint());
      }
   }

   private void selectDataPoint(XYDataPoint dp) {
      selectedDataPoint = new DataPoint(dp.getX().doubleValue(), dp.getY().doubleValue(), null);
      Matcher m = DPPATTERN.matcher(dp.getPointLabel());
      if (m.matches()) {
         try {
            selectedDataPoint.execId = Long.valueOf(m.group(1));
         } catch (Exception e) {
            log.error("Error while obtaining datapoint", e);
         }
      }
   }

   public List<SeriesRequest> getSeriesSpecs() {
      return seriesSpecs;
   }

   public String getSortType() {
      return sortType.toString();
   }

   public void setSortType(String sortType) {
      this.sortType = SortType.valueOf(sortType);
   }

   ///////////////// UI FIELD: test UID
   public boolean isTestUidDisabled() {
      return report != null && report.getSelectedTest() != null;
   }

   public List<Test> getSelectionTests() {
      return report == null ? null : report.getSelectionTests();
   }

   public Test getSelectedTest() {
      return report == null ? null : report.getSelectedTest();
   }

   public Long getSelectedTestId() {
      return selectedTestId;
   }

   public void setSelectedTestId(Long selectedTestId) {
      this.selectedTestId = selectedTestId;
   }

   ///////////////// UI FIELD: test execution parameter
   public List<String> getSelectionParams() {
      return report == null ? null : report.getSelectionParams();
   }

   public String getSelectedParam() {
      return selectedParam;
   }

   public String getReportSelectedParam() {
      return report == null ? null : report.getSelectedParam();
   }

   public void setSelectedParam(String selectedParam) {
      this.selectedParam = selectedParam;
   }

   public boolean isExecParamDisabled() {
      return report != null && report.getSelectedParam() != null;
   }

   ///// the series table
   public List<Metric> getSelectionMetrics() {
      return report == null ? null : report.getSelectionMetrics();
   }

   ///////////////// UI AREA: chart
   public boolean isChartVisible() {
      return chartData != null;
   }

   public XYDataSetCollection getChartData() {
      return chartData;
   }

   private XYDataSetCollection recomputeChartData(Response response) {
      if (response == null || response.getSeries() == null || response.getSeries().isEmpty()) {
         return null;
      }
      XYDataSetCollection collection = new XYDataSetCollection();
      minValue = Double.MAX_VALUE;
      maxValue = Double.MIN_VALUE;
      for (SeriesResponse seriesResponse : response.getSeries()) {
         XYDataList series = new XYDataList();
         series.setLabel(seriesResponse.getName());
         for (DataPoint dp : seriesResponse.getDatapoints()) {
            series.addDataPoint(new XYDataPoint((Long) dp.param, dp.value, "Exec ID: " + dp.execId));
            if (dp.value > maxValue) {
               maxValue = dp.value;
            }
            if (dp.value < minValue) {
               minValue = dp.value;
            }
         }
         collection.addDataList(series);
      }
      double range = maxValue - minValue;
      chart.setYaxisMaxValue(maxValue + 0.1d * range);
      chart.setYaxisMinValue(minValue < 0d ? minValue : 0d);
      chart.setYaxisTitle("Metric values");
      chart.setXaxisTitle(response.getSelectedParam());
      return collection;
   }

   public FlotChartRendererData getChart() {//
      return chart;
   }

   ///////////////// UI AREA: problematic data points
   public boolean isProblematicDataPointsVisible() {
      return report != null && report.getProblematicSeries() != null;
   }

   public List<DataPoint> getProblematicDatapoints() {
      return report == null ? null : report.getProblematicSeries().getDatapoints();
   }

   ///////////////// UI AREA: statistics
   public String getMinValue() {
      return minValue == null || minValue == Double.MAX_VALUE ? "N/A" : FMT.format(minValue);
   }

   public String getMaxValue() {
      return maxValue == null || maxValue == Double.MIN_VALUE ? "N/A" : FMT.format(maxValue);
   }

   public String getRange() {
      return "N/A".equals(getMinValue()) || "N/A".equals(getMaxValue()) ? "N/A" : FMT.format(maxValue - minValue);
   }

   ///////////////// UI AREA: data point
   public String getDataPointParam() {
      return selectedDataPoint == null ? "N/A" : selectedDataPoint.getParam();
   }

   public String getDataPointValue() {
      return selectedDataPoint == null ? "N/A" : FMT.format(selectedDataPoint.value);
   }

   public String getDataPointExecId() {
      return selectedDataPoint == null || selectedDataPoint.execId == null ? "N/A" : selectedDataPoint.execId.toString();
   }

   public Long getDataPointExecIdLong() {
      return selectedDataPoint == null ? null : selectedDataPoint.execId;
   }

}