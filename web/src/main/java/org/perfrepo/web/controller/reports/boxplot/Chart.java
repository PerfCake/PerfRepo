package org.perfrepo.web.controller.reports.boxplot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Transfer object for communication between boxplot report controller and boxplot report service.
 * Contains all information about charts, so whole report can be displayed from list of
 * these chart objects. Encapsulates also series and baselines info.
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public class Chart {

   private Long testId;
   private String name;

   private List<Series> seriesList;
   private List<Baseline> baselines;

   private AxisOption xAxisLabel;
   private String xAxisLabelParameter;
   private AxisOption xAxisSort;
   private String xAxisSortParameter;

   public Long getTestId() {
      return testId;
   }

   public void setTestId(Long testId) {
      this.testId = testId;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public List<Series> getSeriesList() {
      return seriesList;
   }

   public void setSeriesList(List<Series> series) {
      this.seriesList = series;
   }

   public List<Baseline> getBaselines() {
      return baselines;
   }

   public void setBaselines(List<Baseline> baselines) {
      this.baselines = baselines;
   }

   public AxisOption getxAxisLabel() {
      return xAxisLabel;
   }

   public void setxAxisLabel(AxisOption xAxisLabel) {
      this.xAxisLabel = xAxisLabel;
   }

   public AxisOption getxAxisSort() {
      return xAxisSort;
   }

   public String getxAxisLabelParameter() {
      return xAxisLabelParameter;
   }

   public void setxAxisLabelParameter(String xAxisLabelParameter) {
      this.xAxisLabelParameter = xAxisLabelParameter;
   }

   public String getxAxisSortParameter() {
      return xAxisSortParameter;
   }

   public void setxAxisSortParameter(String xAxisSortParameter) {
      this.xAxisSortParameter = xAxisSortParameter;
   }

   public void setxAxisSort(AxisOption xAxisSort) {
      this.xAxisSort = xAxisSort;
   }

   public void addSeries(Series series) {
      if(seriesList == null) {
         seriesList = new ArrayList<>();
      }

      seriesList.add(series);
   }

   public void removeSeries(Series series) {
      seriesList.remove(series);
   }

   public void addBaseline(Baseline baseline) {
      if(baselines == null) {
         baselines = new ArrayList<>();
      }

      baselines.add(baseline);
   }

   public void removeBaseline(Baseline baseline) {
      baselines.remove(baseline);
   }

   /* -------------------- Inner classes --------------------- */

   public static class Series {

      private String name;
      private String tags;
      private Long metricId;

      private List<DataPoint> dataPoints;

      public String getName() {
         return name;
      }

      public void setName(String name) {
         this.name = name;
      }

      public String getTags() {
         return tags;
      }

      public void setTags(String tags) {
         this.tags = tags;
      }

      public Long getMetricId() {
         return metricId;
      }

      public void setMetricId(Long metricId) {
         this.metricId = metricId;
      }

      public List<DataPoint> getDataPoints() {
         return dataPoints;
      }

      public void setDataPoints(List<DataPoint> dataPoints) {
         this.dataPoints = dataPoints;
      }

      /**
       * Data point represents a single boxplot item in a chart. Front-end JavaScript library
       * computes the boxplot from the values and we usually need more info than just values, like
       * label on x-axis or test execution ID. This class encapsulates all the needed info
       * so it can be easily extended in the future.
       */
      public static class DataPoint {

         private final Object label;
         private final Long testExecutionId;

         private final List<Double> values;

         public DataPoint(Long testExecutionId, Object label, List<Double> values) {
            this.label = label;
            this.testExecutionId = testExecutionId;
            this.values = new ArrayList<>(values);
         }

         public Object getLabel() {
            return label;
         }

         public Long getTestExecutionId() {
            return testExecutionId;
         }

         public List<Double> getValues() {
            return Collections.unmodifiableList(values);
         }
      }
   }

   public static class Baseline {

      private String name;
      private Long metricId;
      private Long executionId;

      private Double resultValue;

      public Long getExecutionId() {
         return executionId;
      }

      public void setExecutionId(Long executionId) {
         this.executionId = executionId;
      }

      public String getName() {
         return name;
      }

      public void setName(String name) {
         this.name = name;
      }

      public Long getMetricId() {
         return metricId;
      }

      public void setMetricId(Long metricId) {
         this.metricId = metricId;
      }

      public Double getResultValue() {
         return resultValue;
      }

      public void setResultValue(Double resultValue) {
         this.resultValue = resultValue;
      }
   }

   public enum AxisOption {

      DATE, PARAMETER, VERSION

   }
}
