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
    * Request
    */
   public static class Request implements Serializable {

      public static final int DEFAULT_SIZE_LIMIT = 100;

      private String testUid;
      private String metricName;
      private String paramName;
      private List<String> tags;
      private SortType sortType;
      private int limitSize = DEFAULT_SIZE_LIMIT;

      public Request(String testUid, String metricName, String paramName, List<String> tags, SortType sortType) {
         this.testUid = testUid;
         this.metricName = metricName;
         this.paramName = paramName;
         this.tags = tags;
         this.sortType = sortType;
      }

      public Request(String testUid, String metricName, String paramName, SortType sortType) {
         this.testUid = testUid;
         this.metricName = metricName;
         this.paramName = paramName;
         this.sortType = sortType;
      }

      public void addTag(String tag) {
         if (tags == null) {
            tags = new ArrayList<String>();
         }
         tags.add(tag);
      }

      public String getTestUid() {
         return testUid;
      }

      public String getMetricName() {
         return metricName;
      }

      public String getParamName() {
         return paramName;
      }

      public List<String> getTags() {
         return tags;
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

   }

   /**
    * Response
    * 
    */
   public static class Response implements Serializable {

      private List<Test> selectionTests;
      private List<Metric> selectionMetric;
      private List<String> selectionParam;
      private List<DataPoint> datapoints;
      private List<DataPoint> problematicDatapoints;

      private Test selectedTest;
      private Metric selectedMetric;
      private String selectedParam;

      public List<Test> getSelectionTests() {
         return selectionTests;
      }

      public void setSelectionTests(List<Test> selectionTests) {
         this.selectionTests = selectionTests;
      }

      public List<Metric> getSelectionMetrics() {
         return selectionMetric;
      }

      public void setSelectionMetric(List<Metric> selectionMetric) {
         this.selectionMetric = selectionMetric;
      }

      public List<String> getSelectionParams() {
         return selectionParam;
      }

      public void setSelectionParam(List<String> selectionParam) {
         this.selectionParam = selectionParam;
      }

      public List<DataPoint> getDatapoints() {
         return datapoints;
      }

      public void setDatapoints(List<DataPoint> datapoints) {
         this.datapoints = datapoints;
      }

      public List<DataPoint> getProblematicDatapoints() {
         return problematicDatapoints;
      }

      public void setProblematicDatapoints(List<DataPoint> problematicDatapoints) {
         this.problematicDatapoints = problematicDatapoints;
      }

      public Test getSelectedTest() {
         return selectedTest;
      }

      public void setSelectedTest(Test selectedTest) {
         this.selectedTest = selectedTest;
      }

      public Metric getSelectedMetric() {
         return selectedMetric;
      }

      public void setSelectedMetric(Metric selectedMetric) {
         this.selectedMetric = selectedMetric;
      }

      public String getSelectedParam() {
         return selectedParam;
      }

      public void setSelectedParam(String selectedParam) {
         this.selectedParam = selectedParam;
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
   }

   public static enum SortType {
      NUMBER, STRING
   }
}
