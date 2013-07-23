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
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
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
import org.jboss.qa.perfrepo.viewscope.ViewScoped;
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

   @Inject
   private TestService testService;

   private SortType sortType = SortType.NUMBER;

   private XYDataSetCollection chartData;
   private FlotChartRendererData chart;
   private Double minValue = null;
   private Double maxValue = null;
   private DataPoint selectedDataPoint;
   private DataPoint baselineDataPoint;

   private Long selectedTestId;
   private String selectedParam;
   private Response report;

   private List<SeriesRequest> seriesSpecs;

   public static class SeriesRequestExt extends SeriesRequest {

      public SeriesRequestExt(String name, String metricName, List<String> tags) {
         super(name, metricName, tags);
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

   }

   @PostConstruct
   protected void init() {
      chart = new FlotChartRendererData();
      seriesSpecs = new ArrayList<SeriesRequest>();
      if (seriesSpecs.isEmpty()) {
         seriesSpecs.add(new SeriesRequestExt("1", getRequestParam("m"), parseTags(getRequestParam("tags"))));
      }
      updateReport(new Request(getRequestParam("t"), getRequestParam("p"), (List<SeriesRequest>) seriesSpecs, sortType));
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
         Request request = new Request(report.getSelectedTest().getUid(), report.getSelectedParam(), sortType);
         for (SeriesRequest r : seriesSpecs) {
            SeriesRequestExt rExt = (SeriesRequestExt) r;
            request.addSeries(new SeriesRequest(rExt.getName(), findSelectedMetricName(rExt.getSelectedMetricId()), parseTags(rExt.getSelectedTags())));
         }
         updateReport(request);
      }
   }

   public List<SeriesRequest> getSeriesSpecs() {
      return seriesSpecs;
   }

   private List<String> parseTags(String tags) {
      return (tags == null || "".equals(tags.trim())) ? null : Arrays.asList(tags.split(" "));
   }

   public String getSortType() {
      return sortType.toString();
   }

   public void setSortType(String sortType) {
      this.sortType = SortType.valueOf(sortType);
   }

   ///////////////// UI FIELD: test UID
   public boolean isTestUidDisabled() {
      return report.getSelectedTest() != null;
   }

   public List<Test> getSelectionTests() {
      return report.getSelectionTests();
   }

   public Test getSelectedTest() {
      return report.getSelectedTest();
   }

   public Long getSelectedTestId() {
      return selectedTestId;
   }

   public void setSelectedTestId(Long selectedTestId) {
      this.selectedTestId = selectedTestId;
   }

   ///////////////// UI FIELD: test execution parameter
   public List<String> getSelectionParams() {
      return report.getSelectionParams();
   }

   public String getSelectedParam() {
      return selectedParam;
   }

   public String getReportSelectedParam() {
      return report.getSelectedParam();
   }

   public void setSelectedParam(String selectedParam) {
      this.selectedParam = selectedParam;
   }

   public boolean isExecParamDisabled() {
      return report.getSelectedParam() != null;
   }

   ///// the series table
   public List<Metric> getSelectionMetrics() {
      return report.getSelectionMetrics();
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
      double yaxisMinValue = minValue - 0.1d * range;
      if (minValue >= 0d && yaxisMinValue < 0) {
         yaxisMinValue = 0d; // don't get below zero if min value isn't negative
      }
      chart.setYaxisMinValue(yaxisMinValue);
      return collection;
   }

   public FlotChartRendererData getChart() {//
      return chart;
   }

   ///////////////// UI AREA: problematic data points
   public boolean isProblematicDataPointsVisible() {
      return report.getProblematicSeries() != null;
   }

   public List<DataPoint> getProblematicDatapoints() {
      return report.getProblematicSeries().getDatapoints();
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
      return selectedDataPoint == null ? "N/A" : selectedDataPoint.execId.toString();
   }

}