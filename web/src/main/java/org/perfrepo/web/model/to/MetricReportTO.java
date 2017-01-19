/**
 * PerfRepo
 * <p>
 * Copyright (C) 2015 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.perfrepo.web.model.to;

import org.perfrepo.web.model.Metric;
import org.perfrepo.web.model.Test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Transfer objects related to metric report.
 *
 * @author Michal Linhard (mlinhard@redhat.com)
 */
public class MetricReportTO {

   /**
    * Chart specification.
    */
   public static class ChartRequest implements Serializable {
      private String testUid;
      private List<SeriesRequest> series = new ArrayList<SeriesRequest>();
      private List<BaselineRequest> baselines = new ArrayList<BaselineRequest>();

      public ChartRequest() {
      }

      public String getTestUid() {
         return testUid;
      }

      public List<SeriesRequest> getSeries() {
         return series;
      }

      public void addSeries(SeriesRequest seriesToAdd) {
         series.add(seriesToAdd);
      }

      public void removeSeries(SeriesRequest seriesToRemove) {
         if (series != null) {
            series.remove(seriesToRemove);
         }
      }

      public void clearSeries() {
         series = null;
      }

      public List<BaselineRequest> getBaselines() {
         return baselines;
      }

      public void addBaseline(BaselineRequest baselineToAdd) {
         baselines.add(baselineToAdd);
      }

      public void removeBaselines(BaselineRequest baselineToRemove) {
         if (baselines != null) {
            baselines.remove(baselineToRemove);
         }
      }

      public void clearBaselines() {
         baselines = null;
      }

      public void setTestUid(String testUid) {
         this.testUid = testUid;
      }
   }

   /**
    * Chart series specification.
    */
   public static class SeriesRequest implements Serializable {
      private String name;
      private String metricName;
      private List<String> tags;

      public SeriesRequest(String name) {
         this.name = name;
      }

      public String getName() {
         return name;
      }

      public void setName(String name) {
         this.name = name;
      }

      public String getMetricName() {
         return metricName;
      }

      public void setMetricName(String metricName) {
         this.metricName = metricName;
      }

      public void addTag(String tag) {
         if (tags == null) {
            tags = new ArrayList<String>();
         }
         tags.add(tag);
      }

      public List<String> getTags() {
         return tags;
      }

      public void setTags(List<String> tags) {
         this.tags = tags;
      }

      @Override
      public String toString() {
         return "(" + name + "|" + metricName + "|" + tags + ")";
      }
   }

   /**
    * Chart series specification.
    */
   public static class BaselineRequest implements Serializable {
      private String name;
      private String metricName;
      private Long execId;

      public BaselineRequest(String name) {
         this.name = name;
      }

      public String getName() {
         return name;
      }

      public void setName(String name) {
         this.name = name;
      }

      public String getMetricName() {
         return metricName;
      }

      public void setMetricName(String metricName) {
         this.metricName = metricName;
      }

      public Long getExecId() {
         return execId;
      }

      public void setExecId(Long execId) {
         this.execId = execId;
      }

      @Override
      public String toString() {
         return "(" + name + "|" + metricName + "|" + Long.toString(execId) + ")";
      }
   }

   public static class SeriesResponse implements Serializable {
      private String name;
      private Metric selectedMetric;
      private List<DataPoint> datapoints;

      public SeriesResponse(String name) {
         super();
         this.name = name;
      }

      public String getName() {
         return name;
      }

      public void setName(String name) {
         this.name = name;
      }

      public Metric getSelectedMetric() {
         return selectedMetric;
      }

      public void setSelectedMetric(Metric selectedMetric) {
         this.selectedMetric = selectedMetric;
      }

      public List<DataPoint> getDatapoints() {
         return datapoints;
      }

      public void setDatapoints(List<DataPoint> datapoints) {
         this.datapoints = datapoints;
      }
   }

   public static class BaselineResponse implements Serializable {
      private String name;
      private Metric selectedMetric;
      private Long execId;
      private Double value;

      public BaselineResponse(String name) {
         super();
         this.name = name;
      }

      public String getName() {
         return name;
      }

      public void setName(String name) {
         this.name = name;
      }

      public Metric getSelectedMetric() {
         return selectedMetric;
      }

      public void setSelectedMetric(Metric selectedMetric) {
         this.selectedMetric = selectedMetric;
      }

      public Long getExecId() {
         return execId;
      }

      public void setExecId(Long execId) {
         this.execId = execId;
      }

      public Double getValue() {
         return value;
      }

      public void setValue(Double value) {
         this.value = value;
      }
   }

   /**
    * Request
    */
   public static class Request implements Serializable {

      public static final int DEFAULT_SIZE_LIMIT = 100;

      private List<ChartRequest> chartSpecs;
      private int limitSize = DEFAULT_SIZE_LIMIT;

      public Request() {

      }

      public Request(List<ChartRequest> chartSpecs) {
         this.chartSpecs = chartSpecs;
      }

      public int getLimitSize() {
         return limitSize;
      }

      public void setLimitSize(int limitSize) {
         this.limitSize = limitSize;
      }

      public List<ChartRequest> getCharts() {
         return chartSpecs;
      }

      public void addChart(ChartRequest chart) {
         if (chartSpecs == null) {
            chartSpecs = new ArrayList<ChartRequest>();
         }
         chartSpecs.add(chart);
      }
   }

   /**
    * Response
    */
   public static class Response implements Serializable {
      private List<Test> selectionTests;
      private List<ChartResponse> charts;

      public List<ChartResponse> getCharts() {
         return charts;
      }

      public void addChart(ChartResponse chart) {
         if (charts == null) {
            charts = new ArrayList<ChartResponse>();
         }
         charts.add(chart);
      }

      public List<Test> getSelectionTests() {
         return selectionTests;
      }

      public void setSelectionTests(List<Test> selectionTests) {
         this.selectionTests = selectionTests;
      }
   }

   /**
    * Chart Response
    */
   public static class ChartResponse implements Serializable {

      private List<Metric> selectionMetrics;

      private Test selectedTest;

      private List<SeriesResponse> series = new ArrayList<SeriesResponse>();
      private List<BaselineResponse> baselines = new ArrayList<BaselineResponse>();

      public void setSelectionMetrics(List<Metric> selectionMetric) {
         this.selectionMetrics = selectionMetric;
      }

      public Test getSelectedTest() {
         return selectedTest;
      }

      public void setSelectedTest(Test selectedTest) {
         this.selectedTest = selectedTest;
      }

      public List<SeriesResponse> getSeries() {
         return series;
      }

      public void setSeries(List<SeriesResponse> series) {
         this.series = series;
      }

      public List<Metric> getSelectionMetrics() {
         return selectionMetrics;
      }

      public void addSeries(SeriesResponse series) {
         this.series.add(series);
      }

      public List<BaselineResponse> getBaselines() {
         return baselines;
      }

      public void setBaselines(List<BaselineResponse> baselines) {
         this.baselines = baselines;
      }

      public void addBaseline(BaselineResponse baseline) {
         this.baselines.add(baseline);
      }
   }

   /**
    * Data point for charts.
    */
   @Deprecated
   public static class DataPoint implements Serializable, Comparable<DataPoint> {

      public static final Double CONFLICT = 1.0d;
      public static final Double CONVERSION = 2.0d;

      private Object param;
      private Double value;
      private Long execId;

      public DataPoint(Object param, Double value, Long execId) {
         super();
         this.param = param;
         this.value = value;
         this.execId = execId;
      }

      @Override
      @SuppressWarnings({"unchecked", "rawtypes"})
      public int compareTo(DataPoint o) {
         return ((Comparable) this.param).compareTo(o.param);
      }

      public String getParam() {
         return param.toString();
      }

      public Long getExecId() {
         return execId;
      }

      public String getProblemType() {
         if (CONFLICT.equals(value)) {
            return "Conflict";
         } else if (CONVERSION.equals(value)) {
            return "Conversion";
         } else {
            return "N/A";
         }
      }

      public String toString() {
         String problem = getProblemType();
         return "(" + param + ", " + ("N/A".equals(problem) ? value : "Problem: " + problem) + ", " + execId + ")";
      }

      public Double getValue() {
         return value;
      }
   }
}
