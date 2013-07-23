package org.jboss.qa.perfrepo.model.to;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jboss.qa.perfrepo.model.Metric;
import org.jboss.qa.perfrepo.model.Test;

/**
 * Transfer objects related to metric report.
 * 
 * @author Michal Linhard (mlinhard@redhat.com)
 * 
 */
public class MetricReportTO {

   /**
    * Chart series specification.
    */
   public static class SeriesRequest implements Serializable {
      private String name;
      private String metricName;
      private List<String> tags;

      public SeriesRequest(String name, String metricName, List<String> tags) {
         this.name = name;
         this.metricName = metricName;
         this.tags = tags;
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
   }

   public static class SeriesResponse implements Serializable {
      private String name;
      private Metric selectedMetric;
      private List<DataPoint> datapoints;

      public SeriesResponse(String name, Metric selectedMetric, List<DataPoint> datapoints) {
         super();
         this.name = name;
         this.selectedMetric = selectedMetric;
         this.datapoints = datapoints;
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

   /**
    * Request
    */
   public static class Request implements Serializable {

      public static final int DEFAULT_SIZE_LIMIT = 100;

      private String testUid;
      private String paramName;
      private List<SeriesRequest> seriesSpecs;
      private SortType sortType = SortType.NUMBER;
      private int limitSize = DEFAULT_SIZE_LIMIT;

      public Request(String testUid, String paramName, List<SeriesRequest> seriesSpecs, SortType sortType) {
         this.testUid = testUid;
         this.paramName = paramName;
         this.seriesSpecs = seriesSpecs;
         this.sortType = sortType;
      }

      public Request(String testUid, String paramName, SortType sortType) {
         this.testUid = testUid;
         this.paramName = paramName;
         this.sortType = sortType;
      }

      public String getTestUid() {
         return testUid;
      }

      public String getParamName() {
         return paramName;
      }

      public SortType getSortType() {
         return sortType;
      }

      public int getLimitSize() {
         return limitSize;
      }

      public void setLimitSize(int limitSize) {
         this.limitSize = limitSize;
      }

      public List<? extends SeriesRequest> getSeriesSpecs() {
         return seriesSpecs;
      }

      public void addSeries(SeriesRequest series) {
         if (seriesSpecs == null) {
            seriesSpecs = new ArrayList<MetricReportTO.SeriesRequest>();
         }
         seriesSpecs.add(series);
      }
   }

   /**
    * Response
    * 
    */
   public static class Response implements Serializable {

      private List<Test> selectionTests;
      private List<String> selectionParam;
      private List<Metric> selectionMetrics;

      private Test selectedTest;
      private String selectedParam;

      private List<SeriesResponse> series;
      private SeriesResponse problematicSeries;

      public List<Test> getSelectionTests() {
         return selectionTests;
      }

      public void setSelectionTests(List<Test> selectionTests) {
         this.selectionTests = selectionTests;
      }

      public void setSelectionMetrics(List<Metric> selectionMetric) {
         this.selectionMetrics = selectionMetric;
      }

      public List<String> getSelectionParams() {
         return selectionParam;
      }

      public void setSelectionParam(List<String> selectionParam) {
         this.selectionParam = selectionParam;
      }

      public Test getSelectedTest() {
         return selectedTest;
      }

      public void setSelectedTest(Test selectedTest) {
         this.selectedTest = selectedTest;
      }

      public String getSelectedParam() {
         return selectedParam;
      }

      public void setSelectedParam(String selectedParam) {
         this.selectedParam = selectedParam;
      }

      public List<SeriesResponse> getSeries() {
         return series;
      }

      public void setSeries(List<SeriesResponse> series) {
         this.series = series;
      }

      public SeriesResponse getProblematicSeries() {
         return problematicSeries;
      }

      public void setProblematicSeries(SeriesResponse problematicSeries) {
         this.problematicSeries = problematicSeries;
      }

      public List<String> getSelectionParam() {
         return selectionParam;
      }

      public List<Metric> getSelectionMetrics() {
         return selectionMetrics;
      }

      public void addSeries(SeriesResponse series) {
         if (this.series == null) {
            this.series = new ArrayList<SeriesResponse>();
         }
         this.series.add(series);
      }

   }

   /**
    * 
    * Data point for charts.
    * 
    */
   public static class DataPoint implements Serializable, Comparable<DataPoint> {

      public static final Double CONFLICT = 1.0d;
      public static final Double CONVERSION = 2.0d;

      public Object param;
      public Double value;
      public Long execId;

      public DataPoint(Object param, Double value, Long execId) {
         super();
         this.param = param;
         this.value = value;
         this.execId = execId;
      }

      @Override
      @SuppressWarnings({ "unchecked", "rawtypes" })
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
   }

   public static enum SortType {
      NUMBER, STRING
   }
}
