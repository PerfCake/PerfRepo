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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.jboss.qa.perfrepo.controller.ControllerBase;
import org.jboss.qa.perfrepo.model.Metric;
import org.jboss.qa.perfrepo.model.Test;
import org.jboss.qa.perfrepo.model.TestExecution;
import org.jboss.qa.perfrepo.model.TestExecutionParameter;
import org.jboss.qa.perfrepo.model.to.MetricReportTO.BaselineRequest;
import org.jboss.qa.perfrepo.model.to.MetricReportTO.BaselineResponse;
import org.jboss.qa.perfrepo.model.to.MetricReportTO.ChartRequest;
import org.jboss.qa.perfrepo.model.to.MetricReportTO.ChartResponse;
import org.jboss.qa.perfrepo.model.to.MetricReportTO.DataPoint;
import org.jboss.qa.perfrepo.model.to.MetricReportTO.Request;
import org.jboss.qa.perfrepo.model.to.MetricReportTO.Response;
import org.jboss.qa.perfrepo.model.to.MetricReportTO.SeriesRequest;
import org.jboss.qa.perfrepo.model.to.MetricReportTO.SeriesResponse;
import org.jboss.qa.perfrepo.model.to.MetricReportTO.SortType;
import org.jboss.qa.perfrepo.service.TestService;
import org.jboss.qa.perfrepo.session.UserSession;
import org.jboss.qa.perfrepo.util.FavoriteParameter;
import org.jboss.qa.perfrepo.util.Util;
import org.jboss.qa.perfrepo.viewscope.ViewScoped;
import org.richfaces.ui.output.chart.ChartDataModel.ChartType;
import org.richfaces.ui.output.chart.NumberChartDataModel;
import org.richfaces.ui.output.chart.PlotClickEvent;

/**
 * Shows development of metric based on a specific test execution parameter.
 * 
 * @author Michal Linhard (mlinhard@redhat.com)
 * 
 */
@Named("metricReportBean")
@ViewScoped
public class MetricReportController extends ControllerBase {

   private static final long serialVersionUID = 1L;

   private static final Logger log = Logger.getLogger(MetricReportController.class);
   //   private static final DecimalFormat FMT = new DecimalFormat("0.000");
   private static final SimpleDateFormat DFMT = new SimpleDateFormat("dd.MM.yyyy HH:mm");
   private static final Pattern PATTERN_REPORT_ID = Pattern.compile("[a-zA-Z][a-zA-Z0-9_-]*");

   @Inject
   private TestService testService;

   @Inject
   private UserSession userSession;

   private Response report;
   private String reportName;
   private String reportId;
   private List<ChartSpec> chartSpecs;
   private List<SeriesSpec> seriesSpecs;
   private List<BaselineSpec> baselineSpecs;
   private List<Test> selectionTests;
   private boolean configVisible;
   private PointDetails pointDetails;

   private static final Comparator<ChartSpec> SORT_BY_NAME = new Comparator<MetricReportController.ChartSpec>() {
      @Override
      public int compare(ChartSpec o1, ChartSpec o2) {
         return o1.getChartName().compareTo(o2.getChartName());
      }
   };

   public class ChartSpec {

      private boolean renderDetails;
      private String chartName;
      private Long selectedTestId;
      private List<String> selectionParam;
      private ChartRequest request;
      private List<SeriesSpec> chartSeries = new ArrayList<SeriesSpec>();
      private List<BaselineSpec> chartBaselines = new ArrayList<BaselineSpec>();
      private List<Metric> selectionMetrics;

      public ChartSpec(String chartName) {
         this.chartName = chartName;
         this.request = new ChartRequest();

      }

      public boolean isRenderDetails() {
         return renderDetails;
      }

      public String getTestUid() {
         return request.getTestUid();
      }

      public void setTestUid(String testUid) {
         request.setTestUid(testUid);
      }

      public String getChartName() {
         return chartName;
      }

      public void setChartName(String chartName) {
         this.chartName = chartName;
      }

      public Long getSelectedTestId() {
         return selectedTestId;
      }

      public void setSelectedTestId(Long selectedTestId) {
         this.selectedTestId = selectedTestId;
         String testUid = findSelectedTestUID(this.selectedTestId);
         if (testUid == null) {
            throw new IllegalStateException("couldn't find test UID for id " + selectedTestId);
         }
         request.setTestUid(testUid);
      }

      public List<String> getSelectionParam() {
         return selectionParam;
      }

      public ChartRequest getRequest() {
         return request;
      }

      public void addSeries(SeriesSpec spec) {
         chartSeries.add(spec);
         request.addSeries(spec.request);
      }

      public void removeSeries(SeriesSpec spec) {
         chartSeries.remove(spec);
         request.removeSeries(spec.request);
      }

      public List<SeriesSpec> getChartSeries() {
         return chartSeries;
      }

      public void addBaseline(BaselineSpec spec) {
         chartBaselines.add(spec);
         request.addBaseline(spec.request);
      }

      public void removeBaseline(BaselineSpec spec) {
         chartBaselines.remove(spec);
         request.removeBaselines(spec.request);
      }

      public List<BaselineSpec> getChartBaselines() {
         return chartBaselines;
      }

      public SortType getSortType() {
         return request.getSortType();
      }

      public void setSortType(SortType sortType) {
         this.request.setSortType(sortType);
      }

      public String getParamName() {
         return request.getParamName();
      }

      public void setParamName(String paramName) {
         this.request.setParamName(paramName);
      }

      // chart config line listener
      public void selectedSortOrder() {
         changeParamSelection();
         updateReport();
      }

      // chart config line listener
      public void selectedTest() {
         changeParamSelection();
         changeMetricSelection();
         updateReport();
      }

      // chart config line listener
      public void editedName() {
         int i = 2;
         String original = chartName;
         while (findOtherSpec(chartName) != null) {
            chartName = original + (" " + i);
            i++;
         }
         if (!chartName.equals(original)) {
            addMessage(ERROR, "page.metricreport.chartExists", original);
         }
      }

      private ChartSpec findOtherSpec(String name) {
         for (ChartSpec chartSpec : chartSpecs) {
            if (chartSpec != this && chartSpec.chartName.equals(chartName)) {
               return chartSpec;
            }
         }
         return null;
      }

      // chart config line listener
      public void selectedParam() {
         updateReport();
      }

      public List<Metric> getSelectionMetrics() {
         return selectionMetrics;
      }

      private void changeMetricSelection() {
         if (selectedTestId == null) {
            throw new IllegalStateException("can't change metrics with null test ID");
         }
         selectionMetrics = testService.getAllSelectionMetrics(selectedTestId);
         if (selectionMetrics.isEmpty()) {
            addMessage(ERROR, "page.metricreport.noMetrics", findSelectedTestUID(selectedTestId));
         } else if (seriesSpecs != null) {
            for (SeriesSpec series : chartSeries) {
               if (series.getSelectedMetricId() == null || findMetricName(series.getSelectedMetricId()) == null) {
                  series.setSelectedMetricId(selectionMetrics.get(0).getId());
               }
            }
         }
      }

      private void changeParamSelection() {
         if (isParamSelectionEnabled()) {
            if (selectedTestId != null) {
               selectionParam = testService.getAllSelectionExecutionParams(selectedTestId);
               if (!selectionParam.isEmpty()) {
                  Collections.sort(selectionParam);
                  request.setParamName(selectionParam.get(0));
               }
            } else {
               selectionParam = Collections.<String> emptyList();
            }
         } else {
            selectionParam = Collections.singletonList("N/A");
         }
      }

      public boolean isParamSelectionEnabled() {
         return request.getSortType().needsParam();
      }

      public void chartActionListener(PlotClickEvent event) {
         for (ChartSpec c : chartSpecs) {
            c.renderDetails = false;
         }
         renderDetails = true;
         if (event.getSeriesIndex() >= chartSeries.size() + chartBaselines.size()) {
            throw new IllegalStateException("series index " + event.getSeriesIndex() + " bigger than or equal to series list size " + chartSeries.size());
         }
         pointDetails = new PointDetails();
         if (event.getSeriesIndex() >= chartSeries.size()) {
            BaselineSpec baseline = chartBaselines.get(event.getSeriesIndex() - chartSeries.size());
            if (event.getPointIndex() >= 2) {
               throw new IllegalStateException("point index " + event.getPointIndex() + " too big in baseline series");
            }
            pointDetails.execId = baseline.getValueExecId();
         } else {
            SeriesSpec series = chartSeries.get(event.getSeriesIndex());
            if (event.getPointIndex() >= series.execIds.size()) {
               throw new IllegalStateException("point index " + event.getPointIndex() + " bigger than or equal to execId list size " + series.execIds.size());
            }
            pointDetails.execId = series.execIds.get(event.getPointIndex());
         }
         pointDetails.exec = testService.getFullTestExecution(pointDetails.execId);
         for (FavoriteParameter fp : userSession.getFavoriteParametersFor(pointDetails.exec.getTest().getId())) {
            pointDetails.favParams.add(new PointDetailsFavParam(fp.getLabel(), pointDetails.exec.findParameter(fp.getParameterName())));
         }
      }

      public SeriesSpec findSeries(String name) {
         for (SeriesSpec spec : this.chartSeries) {
            if (spec.getName().equals(name)) {
               return spec;
            }
         }
         return null;
      }

      public BaselineSpec findBaseline(String name) {
         for (BaselineSpec spec : this.chartBaselines) {
            if (spec.getName().equals(name)) {
               return spec;
            }
         }
         return null;
      }

      public Long findMetricId(String metricName) {
         if (metricName == null) {
            return null;
         }
         if (selectionMetrics == null) {
            return null;
         }
         for (Metric m : selectionMetrics) {
            if (metricName.equals(m.getName())) {
               return m.getId();
            }
         }
         return null;
      }

      public String findMetricName(Long id) {
         if (id == null) {
            return null;
         }
         if (selectionMetrics == null) {
            return null;
         }
         for (Metric m : selectionMetrics) {
            if (id.equals(m.getId())) {
               return m.getName();
            }
         }
         return null;
      }

   }

   public class PointDetailsFavParam {
      private String label;
      private TestExecutionParameter param;

      private PointDetailsFavParam(String name, TestExecutionParameter param) {
         super();
         this.label = name;
         this.param = param;
      }

      public String getLabel() {
         return label;
      }

      public TestExecutionParameter getParam() {
         return param;
      }

   }

   public class PointDetails {

      private Long execId;
      private TestExecution exec;
      private List<PointDetailsFavParam> favParams = new ArrayList<MetricReportController.PointDetailsFavParam>();

      public Long getExecId() {
         return execId;
      }

      public String getExecName() {
         return exec == null ? "N/A" : exec.getName();
      }

      public String getExecStarted() {
         return exec == null ? "N/A" : DFMT.format(exec.getStarted());
      }

      public List<String> getTags() {
         return exec == null ? null : exec.getSortedTags();
      }

      public List<PointDetailsFavParam> getFavParams() {
         return favParams;
      }
   }

   public class SeriesSpec {

      private ChartSpec chart;
      private SeriesRequest request;
      private Long selectedMetricId;
      private String selectedTags;
      private NumberChartDataModel chartModel;
      private List<Long> execIds;

      private SeriesSpec(ChartSpec chart, SeriesRequest request) {
         super();
         this.chart = chart;
         this.request = request;
         this.chartModel = new NumberChartDataModel(ChartType.line);
         this.execIds = new ArrayList<Long>();
      }

      public String getName() {
         return request.getName();
      }

      public void setName(String name) {
         request.setName(name);
      }

      public NumberChartDataModel getChartModel() {
         return chartModel;
      }

      public void addPoint(double x, double y, long execId) {
         chartModel.put(x, y);
         execIds.add(execId);
      }

      public void clearModel() {
         this.chartModel = new NumberChartDataModel(ChartType.line);
         this.execIds.clear();
      }

      public Long getSelectedMetricId() {
         return selectedMetricId;
      }

      public void setSelectedMetricId(Long selectedMetricId) {
         this.selectedMetricId = selectedMetricId;
         request.setMetricName(chart.findMetricName(selectedMetricId));
      }

      public String getSelectedTags() {
         return selectedTags;
      }

      public void setSelectedTags(String selectedTags) {
         this.selectedTags = selectedTags;
         this.request.setTags(Util.parseTags(selectedTags));
      }

      public String getMetricName() {
         return this.request.getMetricName();
      }

      @Override
      public String toString() {
         return super.toString() + "(metricId=" + selectedMetricId + ", selectedTags=" + selectedTags + ")";
      }

      public List<Metric> getSelectionMetrics() {
         return chart.getSelectionMetrics();
      }

      public void setChart(String chartName) {
         if (this.chart != null && chartName.equals(this.chart.chartName)) {
            return;
         }
         ChartSpec newSpec = findChartSpecByName(chartName);
         if (newSpec != null) {
            if (this.chart != null) {
               this.chart.removeSeries(this);
            }
            newSpec.addSeries(this);
            this.chart = newSpec;
            setSelectedMetricId(this.chart.selectionMetrics.isEmpty() ? null : this.chart.selectionMetrics.get(0).getId());
         } else {
            throw new IllegalArgumentException("can't find chart " + chartName);
         }
      }

      public String getChart() {
         return chart.getChartName();
      }

      // chart config line listener
      public void selectedChart() {
         updateReport();
      }

      public void editedSeriesName() {
         int i = 2;
         String original = getName();
         while (findOtherSpec(getName()) != null) {
            setName(original + (" " + i));
            i++;
         }
         if (!getName().equals(original)) {
            addMessage(ERROR, "page.metricreport.seriesExists", original);
         }
         updateReport();
      }

      public void selectedMetric() {
         updateReport();
      }

      public void editedTags() {
         updateReport();
      }

      private SeriesSpec findOtherSpec(String name) {
         for (SeriesSpec seriesSpec : chart.chartSeries) {
            if (seriesSpec != this && seriesSpec.getName().equals(name)) {
               return seriesSpec;
            }
         }
         return null;
      }
   }

   public class BaselineSpec {

      private ChartSpec chart;
      private BaselineRequest request;
      private Long selectedMetricId;
      private NumberChartDataModel chartModel;
      private Long valueExecId;

      private BaselineSpec(ChartSpec chart, BaselineRequest request) {
         super();
         this.chart = chart;
         this.request = request;
         this.chartModel = new NumberChartDataModel(ChartType.line);
      }

      public String getName() {
         return request.getName();
      }

      public void setName(String name) {
         request.setName(name);
      }

      public NumberChartDataModel getChartModel() {
         return chartModel;
      }

      public void setValue(int maxDataPointCount, double value, Long execId) {
         chartModel.put(0, value);
         chartModel.put(maxDataPointCount - 1, value);
         this.valueExecId = execId;
      }

      public void clearModel() {
         this.chartModel = new NumberChartDataModel(ChartType.line);
         this.valueExecId = null;
      }

      public Long getSelectedMetricId() {
         return selectedMetricId;
      }

      public void setSelectedMetricId(Long selectedMetricId) {
         this.selectedMetricId = selectedMetricId;
         request.setMetricName(chart.findMetricName(selectedMetricId));
      }

      public String getMetricName() {
         return this.request.getMetricName();
      }

      public Long getExecId() {
         return request.getExecId();
      }

      public void setExecId(Long execId) {
         request.setExecId(execId);
      }

      public Long getValueExecId() {
         return valueExecId;
      }

      @Override
      public String toString() {
         return super.toString() + "(metricId=" + selectedMetricId + ", execId=" + (request == null ? null : request.getExecId()) + ")";
      }

      public List<Metric> getSelectionMetrics() {
         return chart.getSelectionMetrics();
      }

      public void setChart(String chartName) {
         if (this.chart != null && chartName.equals(this.chart.chartName)) {
            return;
         }
         ChartSpec newSpec = findChartSpecByName(chartName);
         if (newSpec != null) {
            if (this.chart != null) {
               this.chart.removeBaseline(this);
            }
            newSpec.addBaseline(this);
            this.chart = newSpec;
            setSelectedMetricId(this.chart.selectionMetrics.isEmpty() ? null : this.chart.selectionMetrics.get(0).getId());
         } else {
            throw new IllegalArgumentException("can't find chart " + chartName);
         }
      }

      public String getChart() {
         return chart.getChartName();
      }

      // chart config line listener
      public void selectedChart() {
         updateReport();
      }

      public void editedName() {
         int i = 2;
         String original = getName();
         while (findOtherSpec(getName()) != null) {
            setName(original + (" " + i));
            i++;
         }
         if (!getName().equals(original)) {
            addMessage(ERROR, "page.metricreport.baselineExists", original);
         }
         updateReport();
      }

      public void selectedMetric() {
         updateReport();
      }

      public void editedExecId() {
         updateReport();
      }

      private BaselineSpec findOtherSpec(String name) {
         for (BaselineSpec baselineSpec : chart.chartBaselines) {
            if (baselineSpec != this && baselineSpec.getName().equals(name)) {
               return baselineSpec;
            }
         }
         return null;
      }
   }

   public boolean isConfigVisible() {
      return configVisible;
   }

   public void showConfig() {
      this.configVisible = true;
   }

   public void hideConfig() {
      this.configVisible = false;
   }

   public String getReportName() {
      return reportName;
   }

   public String getReportId() {
      return reportId;
   }

   public void setReportName(String reportName) {
      this.reportName = reportName;
   }

   public void setReportId(String reportId) {
      if (reportId != null && !PATTERN_REPORT_ID.matcher(reportId).matches()) {
         addMessage(ERROR, "page.metricreport.reportIdFromat");
      }
      this.reportId = reportId;
   }

   public List<String> getChartNames() {
      List<String> r = new ArrayList<String>(chartSpecs.size());
      for (ChartSpec s : chartSpecs) {
         r.add(s.getChartName());
      }
      return r;
   }

   private ChartSpec findChartSpecByName(String name) {
      for (ChartSpec s : chartSpecs) {
         if (name.equals(s.getChartName())) {
            return s;
         }
      }
      return null;
   }

   @PostConstruct
   protected void init() {
      if (selectionTests == null) {
         selectionTests = testService.getAllSelectionTests();
         Collections.sort(selectionTests, new Comparator<Test>() {
            @Override
            public int compare(Test o1, Test o2) {
               return o1.getName().compareTo(o2.getName());
            }
         });
      }
      String reportOwnerId = getRequestParam("reportOwnerId");
      String reportId = getRequestParam("reportId");
      if (reportOwnerId != null && reportId != null) {
         generateSavedReport(reportOwnerId, reportId);
      } else {
         configVisible = true;
         generateNewReport();
      }
   }

   public List<SortType> getSortTypeValues() {
      return Arrays.asList(SortType.values());
   }

   private void generateNewReport() {
      if (selectionTests == null || selectionTests.isEmpty()) {
         addMessage(INFO, "page.metricreport.noTests");
      }
      reportName = getBundleString("page.metricreport.newReport");
      List<String> existingIds = userSession.getAllReportIds();
      int i = 1;
      while (existingIds.contains("metric" + Integer.toString(i))) {
         i++;
      }
      reportId = "metric" + Integer.toString(i);
      chartSpecs = new ArrayList<ChartSpec>();
      ChartSpec chart = new ChartSpec("Chart 1");
      chart.setSelectedTestId(selectionTests.get(0).getId());
      chart.changeParamSelection();
      chart.changeMetricSelection();
      chartSpecs.add(chart);
      seriesSpecs = new ArrayList<SeriesSpec>();
      baselineSpecs = new ArrayList<BaselineSpec>();
      SeriesSpec series = new SeriesSpec(chart, new SeriesRequest("Series 1"));
      if (chart.getSelectionMetrics() != null && !chart.getSelectionMetrics().isEmpty()) {
         series.setSelectedMetricId(chart.getSelectionMetrics().get(0).getId());
      }
      chart.addSeries(series);
      seriesSpecs.add(series);
      updateReport();
   }

   private void generateSavedReport(String reportOwnerId, String reportId) {
      try {
         Map<String, String> reportProperties = reportOwnerId.equals(userSession.getUser().getUsername()) ? userSession.getReportProperties(reportId)
               : userSession.getReportProperties(reportOwnerId, reportId);
         this.reportId = reportId;
         this.reportName = reportProperties.get("name");
         int chartIdx = 0;
         String chartPrefix = "chart" + chartIdx;
         List<ChartSpec> savedChartSpecs = new ArrayList<ChartSpec>();
         while (reportProperties.containsKey(chartPrefix + ".name")) {
            ChartSpec chart = new ChartSpec(reportProperties.get(chartPrefix + ".name"));
            chart.setSortType(SortType.valueOf(reportProperties.get(chartPrefix + ".sort")));
            chart.setSelectedTestId(Long.valueOf(reportProperties.get(chartPrefix + ".test")));
            chart.changeMetricSelection();
            chart.changeParamSelection();
            chart.setParamName(reportProperties.get(chartPrefix + ".param"));
            int j = 0;
            String seriesPrefix = chartPrefix + ".series" + j;
            while (reportProperties.containsKey(seriesPrefix + ".name")) {
               SeriesSpec series = new SeriesSpec(chart, new SeriesRequest(reportProperties.get(seriesPrefix + ".name")));
               series.setSelectedMetricId(Long.valueOf(reportProperties.get(seriesPrefix + ".metric")));
               series.setSelectedTags(reportProperties.get(seriesPrefix + ".tags"));
               chart.addSeries(series);
               j++;
               seriesPrefix = chartPrefix + ".series" + j;
            }
            j = 0;
            String baselinePrefix = chartPrefix + ".baseline" + j;
            while (reportProperties.containsKey(baselinePrefix + ".name")) {
               BaselineSpec baseline = new BaselineSpec(chart, new BaselineRequest(reportProperties.get(baselinePrefix + ".name")));
               baseline.setSelectedMetricId(Long.valueOf(reportProperties.get(baselinePrefix + ".metric")));
               baseline.setExecId(Long.valueOf(reportProperties.get(baselinePrefix + ".execId")));
               chart.addBaseline(baseline);
               j++;
               baselinePrefix = chartPrefix + ".baseline" + j;
            }
            savedChartSpecs.add(chart);
            chartIdx++;
            chartPrefix = "chart" + chartIdx;
         }
         chartSpecs = savedChartSpecs;
         seriesSpecs = new ArrayList<SeriesSpec>();
         baselineSpecs = new ArrayList<BaselineSpec>();
         updateReport();
      } catch (Exception e) {
         log.error("problem with saved report", e);
         addMessage(ERROR, "page.metricreport.savedReportProblem");
      }
   }

   public PointDetails getPointDetails() {
      return pointDetails;
   }

   public String getLinkToReport() {
      if (reportId == null || reportId.isEmpty() || !PATTERN_REPORT_ID.matcher(reportId).matches()) {
         return "/repo/reports/metric";
      } else {
         return "/repo/reports/metric/saved/" + userSession.getUser().getUsername() + "/" + reportId;
      }
   }

   /**
    * called on preRenderView
    */
   public void preRender() {
      reloadSessionMessages();
   }

   private void updateReport() {
      Collections.sort(chartSpecs, SORT_BY_NAME);
      Request request = new Request();
      for (ChartSpec chartSpec : chartSpecs) {
         request.addChart(chartSpec.getRequest());
      }
      seriesSpecs.clear();
      baselineSpecs.clear();
      report = testService.computeMetricReport(request);
      Iterator<ChartResponse> chartResponses = report.getCharts().iterator();
      Iterator<ChartRequest> chartRequests = request.getCharts().iterator();
      if (report.getCharts().size() != request.getCharts().size()) {
         throw new IllegalStateException("expected " + request.getCharts().size() + " returned " + report.getCharts().size());
      }
      for (ChartSpec chartSpec : chartSpecs) {
         ChartRequest chartRequest = chartRequests.next();
         ChartResponse chartResponse = chartResponses.next();
         recomputeChartData(chartSpec, chartResponse);
         if (chartResponse.getSelectedTest() == null) {
            addMessage(INFO, "page.metricreport.selectTestUID", chartSpec.getChartName());
         } else if (chartResponse.getSelectedParam() == null && chartRequest.getSortType().needsParam()) {
            addMessage(INFO, "page.metricreport.selectExecParam", chartSpec.getChartName());
         }
      }
   }

   private String findSelectedTestUID(Long id) {
      for (Test t : selectionTests) {
         if (id.equals(t.getId())) {
            return t.getUid();
         }
      }
      return null;
   }

   public void save() {
      if (reportId == null || reportId.isEmpty()) {
         addMessage(ERROR, "page.metricreport.enterReportID");
         return;
      }

      Map<String, String> reportProps = new HashMap<String, String>();
      reportProps.put("name", reportName);
      reportProps.put("type", "Metric");
      reportProps.put("link", getLinkToReport());

      for (int i = 0; i < chartSpecs.size(); i++) {
         ChartSpec chart = chartSpecs.get(i);
         String chartPrefix = "chart" + i;
         reportProps.put(chartPrefix + ".name", chart.getChartName());
         if (chart.getParamName() != null) {
            reportProps.put(chartPrefix + ".param", chart.getParamName());
         }
         reportProps.put(chartPrefix + ".test", Long.toString(chart.getSelectedTestId()));
         reportProps.put(chartPrefix + ".sort", chart.getSortType().toString());
         for (int j = 0; j < chart.chartSeries.size(); j++) {
            SeriesSpec series = chart.chartSeries.get(j);
            String seriesPrefix = chartPrefix + ".series" + j;
            reportProps.put(seriesPrefix + ".name", series.getName());
            reportProps.put(seriesPrefix + ".metric", Long.toString(series.getSelectedMetricId()));
            reportProps.put(seriesPrefix + ".tags", series.getSelectedTags());
         }
         for (int j = 0; j < chart.chartBaselines.size(); j++) {
            BaselineSpec baseline = chart.chartBaselines.get(j);
            String baselinePrefix = chartPrefix + ".baseline" + j;
            reportProps.put(baselinePrefix + ".name", baseline.getName());
            reportProps.put(baselinePrefix + ".metric", Long.toString(baseline.getSelectedMetricId()));
            reportProps.put(baselinePrefix + ".execId", Long.toString(baseline.getExecId()));
         }
      }

      userSession.setReportProperties(reportId, reportProps);
   }

   public void addSeries() {
      if (chartSpecs == null || chartSpecs.isEmpty()) {
         addMessage(ERROR, "page.metricreport.noChartSpecs");
      } else {
         SeriesSpec newSeries = new SeriesSpec(chartSpecs.get(0), new SeriesRequest("Series " + (seriesSpecs.size() + 1)));
         chartSpecs.get(0).addSeries(newSeries);
         seriesSpecs.add(newSeries);
      }
   }

   public void addBaseline() {
      if (chartSpecs == null || chartSpecs.isEmpty()) {
         addMessage(ERROR, "page.metricreport.noChartSpecs");
      } else {
         BaselineSpec newBaseline = new BaselineSpec(chartSpecs.get(0), new BaselineRequest("Baseline " + (baselineSpecs.size() + 1)));
         chartSpecs.get(0).addBaseline(newBaseline);
         baselineSpecs.add(newBaseline);
      }
   }

   public void removeChart(ChartSpec chartToRemove) {
      if (chartSpecs == null || chartSpecs.isEmpty()) {
         log.warn("Strange. Removing a non-existent chart.");
         return;
      }
      if (chartSpecs.remove(chartToRemove)) {
         if (chartSpecs.isEmpty()) {
            seriesSpecs.clear();
         } else {
            ChartSpec thesurvivor = chartSpecs.get(0);
            for (SeriesSpec series : chartToRemove.chartSeries) {
               series.chart = thesurvivor;
            }
         }
      }
   }

   public void addChart() {
      ChartSpec newSpec = new ChartSpec("Chart " + (chartSpecs.size() + 1));
      newSpec.setSelectedTestId(selectionTests.get(0).getId());
      newSpec.changeParamSelection();
      newSpec.changeMetricSelection();
      chartSpecs.add(newSpec);
   }

   public void removeSeries(SeriesSpec seriesToRemove) {
      if (seriesToRemove.chart != null) {
         seriesToRemove.chart.removeSeries(seriesToRemove);
      }
      seriesSpecs.remove(seriesToRemove);
      updateReport();
   }

   public void removeBaseline(BaselineSpec baselineToRemove) {
      if (baselineToRemove.chart != null) {
         baselineToRemove.chart.removeBaseline(baselineToRemove);
      }
      baselineSpecs.remove(baselineToRemove);
      updateReport();
   }

   public List<ChartSpec> getChartSpecs() {
      return chartSpecs;
   }

   public List<SeriesSpec> getSeriesSpecs() {
      return seriesSpecs;
   }

   public List<BaselineSpec> getBaselineSpecs() {
      return baselineSpecs;
   }

   private void recomputeChartData(ChartSpec chartSpec, ChartResponse response) {
      if (response == null || response.getSeries() == null || response.getSeries().isEmpty()) {
         chartSpec.getChartSeries().clear();
         return;
      }
      int maxDatapointCount = 2;
      for (SeriesResponse seriesResponse : response.getSeries()) {
         SeriesSpec seriesSpec = chartSpec.findSeries(seriesResponse.getName());
         if (seriesSpec == null) {
            throw new IllegalStateException("Series \"" + seriesResponse.getName() + "\" doesn't exist anymore.");
         }
         seriesSpec.clearModel();
         if (seriesResponse.getDatapoints() != null) {
            for (int i = 0; i < seriesResponse.getDatapoints().size(); i++) {
               DataPoint dp = seriesResponse.getDatapoints().get(i);
               seriesSpec.addPoint(i, dp.value, dp.execId);
            }
            if (seriesResponse.getDatapoints().size() > maxDatapointCount) {
               maxDatapointCount = seriesResponse.getDatapoints().size();
            }
         }
         seriesSpecs.add(seriesSpec);
      }
      for (BaselineResponse baselineResponse : response.getBaselines()) {
         BaselineSpec baselineSpec = chartSpec.findBaseline(baselineResponse.getName());
         if (baselineSpec == null) {
            throw new IllegalStateException("Baseline \"" + baselineResponse.getName() + "\" doesn't exist anymore.");
         }
         baselineSpec.clearModel();
         if (baselineResponse.getValue() != null) {
            baselineSpec.setValue(maxDatapointCount, baselineResponse.getValue(), baselineResponse.getExecId());
         }
         baselineSpecs.add(baselineSpec);
      }
   }

   public List<Test> getSelectionTests() {
      return selectionTests;
   }

   public String getDisplayValueFavParam(PointDetailsFavParam favParam) {
      return Util.displayValue(favParam.getParam());
   }
}