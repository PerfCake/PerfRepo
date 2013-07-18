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
import org.jboss.qa.perfrepo.model.to.MetricReportTO.SortType;
import org.jboss.qa.perfrepo.service.TestService;
import org.jboss.qa.perfrepo.viewscope.ViewScoped;
import org.jsflot.components.FlotChartRendererData;
import org.jsflot.xydata.XYDataList;
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

   private String tags;
   private String metricName;
   private String testUid;
   private String parameterName;
   private SortType sortType = SortType.NUMBER;

   private XYDataSetCollection chartData;
   private FlotChartRendererData chart;
   private Double minValue = null;
   private Double maxValue = null;

   private Response report;

   @PostConstruct
   protected void init() {
      chart = new FlotChartRendererData();
   }

   /**
    * called on preRenderView
    */
   public void preRender() {
      log.info("preRenderView event: testUid=" + testUid + ", metricName=" + metricName + ", parameterName=" + parameterName);
      report = testService.computeMetricReport(new Request(testUid, metricName, parameterName, parseTags(tags), sortType));
      chartData = recomputeChartData(report.getDatapoints());
   }

   private List<String> parseTags(String tags) {
      return tags == null ? null : Arrays.asList(tags.split(" "));
   }

   public String getTags() {
      return tags;
   }

   public void setTags(String tags) {
      this.tags = tags;
   }

   public String getMetricName() {
      return metricName;
   }

   public void setMetricName(String metricName) {
      this.metricName = metricName;
   }

   public String getTestUid() {
      return testUid;
   }

   public void setTestUid(String testUid) {
      this.testUid = testUid;
   }

   public String getParameterName() {
      return parameterName;
   }

   public void setParameterName(String parameterName) {
      this.parameterName = parameterName;
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

   ///////////////// UI FIELD: metric
   public boolean isMetricDisabled() {
      return report.getSelectedMetric() != null;
   }

   public List<Metric> getSelectionMetrics() {
      return report.getSelectionMetrics();
   }

   public Metric getSelectedMetric() {
      return report.getSelectedMetric();
   }

   ///////////////// UI FIELD: test execution parameter
   public boolean isExecParameterDisabled() {
      return report.getSelectedParam() != null;
   }

   public List<String> getSelectionParams() {
      return report.getSelectionParams();
   }

   public String getSelectedParam() {
      return report.getSelectedParam();
   }

   ///////////////// UI AREA: chart
   public boolean isChartVisible() {
      return chartData != null;
   }

   public XYDataSetCollection getChartData() {
      return chartData;
   }

   private XYDataSetCollection recomputeChartData(List<DataPoint> datapoints) {
      if (datapoints == null || datapoints.isEmpty()) {
         return null;
      }
      XYDataList series = new XYDataList();
      series.setLabel(metricName);
      minValue = Double.MAX_VALUE;
      maxValue = Double.MIN_VALUE;
      for (DataPoint dp : datapoints) {
         series.addDataPoint((Long) dp.param, dp.value);
         if (dp.value > maxValue) {
            maxValue = dp.value;
         }
         if (dp.value < minValue) {
            minValue = dp.value;
         }
      }
      XYDataSetCollection collection = new XYDataSetCollection();
      collection.addDataList(series);
      double range = maxValue - minValue;
      chart.setYaxisMaxValue(maxValue + 0.1d * range);
      double yaxisMinValue = minValue - 0.1d * range;
      if (minValue >= 0d && yaxisMinValue < 0) {
         yaxisMinValue = 0d; // don't get below zero if min value isn't negative
      }
      chart.setYaxisMinValue(yaxisMinValue);
      return collection;
   }

   public FlotChartRendererData getChart() {
      return chart;
   }

   ///////////////// UI AREA: problematic data points
   public boolean isProblematicDataPointsVisible() {
      return report.getProblematicDatapoints() != null;
   }

   public List<DataPoint> getProblematicDatapoints() {
      return report.getProblematicDatapoints();
   }

   ///////////////// UI AREA: statistics
   public String getMinValue() {
      return minValue == null ? "N/A" : FMT.format(minValue);
   }

   public String getMaxValue() {
      return maxValue == null ? "N/A" : FMT.format(maxValue);
   }

   public String getRange() {
      return minValue == null || maxValue == null ? "N/A" : FMT.format(maxValue - minValue);
   }

}