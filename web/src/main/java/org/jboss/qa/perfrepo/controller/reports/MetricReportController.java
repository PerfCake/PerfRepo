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

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.qa.perfrepo.controller.ControllerBase;
import org.jboss.qa.perfrepo.model.Metric;
import org.jboss.qa.perfrepo.model.Test;
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
   private static final String CHART_MODE = "Time";
   private static final String CHART_WIDTH_IN_PIXELS = "625";

   //private static final Logger log = Logger.getLogger(MetricTimelineController.class);

   @Inject
   private TestService testService;

   private String tags;
   private String metricName;
   private String testUid;
   private String parameterName;

   private XYDataList dataList = new XYDataList();
   private FlotChartRendererData chartData = new FlotChartRendererData();

   private List<Metric> selectionMetrics;
   private List<Test> selectionTests;
   private List<String> selectionParameters;
   private Long selectedMetricId;
   private Long selectedTestId;
   private String selectedParameterName;

   /*
    * testUID -> choose tests from all tests for users groupid
    * 
    * by GET url parameter a nonexistent UID can be passed
    * 
    * metric -> choose from all metrics for given test reset when testUID changes
    * 
    * parameter -> choos from all test execution parameters bound to given test
    */

   public List<Test> getSelectionTests() {
      if (selectionTests == null) {
         selectionTests = testService.getAllSelectionTests();
      }
      return selectionTests;
   }

   public Long getSelectedTestId() {
      if (selectedTestId == null) {
         List<Test> tests = getSelectionTests();
         for (Test test : tests) {
            if (test.getUid().equals(testUid)) {
               selectedTestId = test.getId();
               return selectedTestId;
            }
         }
      }
      return null;
   }

   public void setSelectedTestId(Long id) {
      selectedTestId = null;
      testUid = null;
      selectionMetrics = null;
      selectionParameters = null;
      selectedParameterName = null;
      selectedMetricId = null;
      List<Test> tests = getSelectionTests();
      for (Test test : tests) {
         if (test.getId().equals(id)) {
            testUid = test.getUid();
            selectedTestId = id;
            break;
         }
      }
   }

   public boolean isSelectionMetricsDisabled() {
      return selectionMetrics == null;
   }

   public List<Metric> getSelectionMetrics() {
      if (selectionMetrics == null) {
         Long testId = getSelectedTestId();
         if (testId != null) {
            selectionMetrics = testService.getAllSelectionMetrics(testId);
         }
      }
      return selectionMetrics;
   }

   public Long getSelectedMetricId() {
      if (selectedMetricId == null) {
         List<Metric> metrics = getSelectionMetrics();
         if (metrics != null && metricName != null) {
            for (Metric metric : metrics) {
               if (metric.getName().equals(metricName)) {
                  selectedMetricId = metric.getId();
               }
            }
         }

      }
      return selectedMetricId;
   }

   public void setSelectedMetricId(Long selectedMetricId) {
      this.selectedMetricId = selectedMetricId;
   }

   public List<String> getSelectionParameters() {
      if (selectionParameters == null) {
         Long testId = getSelectedTestId();
         if (testId != null) {
            selectionParameters = testService.getAllSelectionExecutionParams(testId);
         }
      }
      return selectionParameters;
   }

   public String getSelectedParameterName() {
      if (selectedParameterName == null) {
         if (parameterName != null && selectionParameters != null && selectionParameters.contains(parameterName)) {
            selectedParameterName = parameterName;
         }
      }
      return selectedParameterName;
   }

   public void setSelectedParameterName(String selectedParameterName) {
      this.selectedParameterName = selectedParameterName;
   }

   public boolean isSelectionParametersDisabled() {
      return selectionParameters == null;
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

   // if testId is null - choose test
   // if testId not null and metric null -> choose metric
   // if testId != null and metric != null ->
   /**
    * Constructor, which is called when JSFlot component is initialized. Gets the data from the
    * cache, full or partial dependent on the parameter from request and fills the corresponding
    * collections for further processing.
    */
   private void generateSeriesData() {
      chartData.setMode(CHART_MODE);
      chartData.setWidth(CHART_WIDTH_IN_PIXELS);
      dataList.setLabel(metricName);
   }

   /**
    * Returns chart series. They are 4 - user counts, sent notifications count, subscription and
    * cancellation counts.
    * 
    * @return the chart series.
    */
   @Produces
   @RequestScoped
   @Named("chartSeriesData")
   public XYDataSetCollection getChartSeries() {
      generateSeriesData();

      XYDataSetCollection collection = new XYDataSetCollection();

      collection.addDataList(getChartDataList(dataList));

      return collection;
   }

   private XYDataList getChartDataList(XYDataList seriesDataList) {
      XYDataList currentSeries1DataList = new XYDataList();

      for (int i = 0; i < seriesDataList.size(); i++) {
         XYDataPoint p1 = new XYDataPoint(seriesDataList.get(i).getX(), seriesDataList.get(i).getY());

         currentSeries1DataList.addDataPoint(p1);
      }
      //Copy over the meta data for each series to the current viewed-series
      currentSeries1DataList.setLabel(seriesDataList.getLabel());
      currentSeries1DataList.setFillLines(seriesDataList.isFillLines());
      currentSeries1DataList.setMarkerPosition(seriesDataList.getMarkerPosition());
      currentSeries1DataList.setMarkers(seriesDataList.isMarkers());
      currentSeries1DataList.setShowDataPoints(seriesDataList.isShowDataPoints());
      currentSeries1DataList.setShowLines(seriesDataList.isShowLines());

      return currentSeries1DataList;
   }

   public XYDataList getDataList() {
      return dataList;
   }

   public void setDataList(XYDataList dataList) {
      this.dataList = dataList;
   }

   public FlotChartRendererData getChartData() {
      return chartData;
   }

   public void setChartData(FlotChartRendererData chartData) {
      this.chartData = chartData;
   }

}