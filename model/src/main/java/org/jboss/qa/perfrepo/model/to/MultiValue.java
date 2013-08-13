package org.jboss.qa.perfrepo.model.to;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.jboss.qa.perfrepo.model.TestExecution;
import org.jboss.qa.perfrepo.model.Value;
import org.jboss.qa.perfrepo.model.ValueParameter;

/**
 * Structured representation of Multi-value, parsed from {@link Value} and {@link ValueParameter}
 * entities extracted from fully loaded {@link TestExecution}.
 * 
 * @author Michal Linhard (mlinhard@redhat.com)
 * 
 */
public class MultiValue {

   /**
    * Create list of multi-values for single test execution, one for each metric.
    * 
    * @param testExecution
    * @return
    */
   public static List<ValueInfo> createFrom(TestExecution testExecution) {
      // group by metric
      Map<String, List<Value>> valuesByMetric = new HashMap<String, List<Value>>();
      for (Value v : testExecution.getValues()) {
         List<Value> values = valuesByMetric.get(v.getMetricName());
         if (values == null) {
            values = new ArrayList<Value>();
            valuesByMetric.put(v.getMetricName(), values);
         }
         values.add(v);
      }
      List<ValueInfo> r = new ArrayList<ValueInfo>();
      for (Map.Entry<String, List<Value>> entry : valuesByMetric.entrySet()) {
         ValueInfo vInfo = new ValueInfo();
         vInfo.metricName = entry.getKey();
         if (entry.getValue().size() == 1) {
            vInfo.simpleValue = entry.getValue().get(0).getResultValue();
         } else {
            vInfo.complexValueByParamName = new TreeMap<String, List<ParamInfo>>();
            for (Value v : entry.getValue()) {
               if (v.getParameters() == null || v.getParameters().isEmpty()) {
                  throw new IllegalStateException("can't have two unparametrized values for same metric");
               } else {
                  for (ValueParameter vp : v.getParameters()) {
                     List<ParamInfo> paramInfos = vInfo.complexValueByParamName.get(vp.getName());
                     if (paramInfos == null) {
                        paramInfos = new ArrayList<ParamInfo>();
                        vInfo.complexValueByParamName.put(vp.getName(), paramInfos);
                     }
                     ParamInfo paramInfo = new ParamInfo();
                     paramInfo.setParamValue(vp.getParamValue());
                     paramInfo.setValue(v.getResultValue());
                     paramInfos.add(paramInfo);
                  }
               }
            }
         }
         if (vInfo.complexValueByParamName != null) {
            for (List<ParamInfo> paramInfoList : vInfo.complexValueByParamName.values()) {
               Collections.sort(paramInfoList);
            }
         }
         r.add(vInfo);
      }
      return r;
   }

   private static final DecimalFormat FMT = new DecimalFormat("0.000");

   public static class ParamInfo implements Comparable<ParamInfo> {
      private String paramValue;
      private Double value;

      public String getParamValue() {
         return paramValue;
      }

      public void setParamValue(String paramValue) {
         this.paramValue = paramValue;
      }

      public Double getValue() {
         return value;
      }

      public void setValue(Double value) {
         this.value = value;
      }

      @Override
      public int compareTo(ParamInfo o) {
         try {
            return Double.valueOf(this.paramValue).compareTo(Double.valueOf(o.paramValue));
         } catch (NumberFormatException e) {
            return this.paramValue.compareTo(o.paramValue);
         }
      }

      public String getFormattedValue() {
         return value == null ? null : FMT.format(value);
      }
   }

   public static class ValueInfo implements Comparable<ValueInfo> {
      private String metricName;
      private Double simpleValue;
      private SortedMap<String, List<ParamInfo>> complexValueByParamName;

      public String getMetricName() {
         return metricName;
      }

      public Double getSimpleValue() {
         return simpleValue;
      }

      public List<ParamInfo> getComplexValueByParamName(String paramName) {
         return complexValueByParamName == null ? null : complexValueByParamName.get(paramName);
      }

      @Override
      public int compareTo(ValueInfo o) {
         return this.metricName.compareTo(o.metricName);
      }

      public boolean isMultiValue() {
         return complexValueByParamName != null;
      }

      public String getFormattedSimpleValue() {
         return simpleValue == null ? null : FMT.format(simpleValue);
      }

      public List<String> getComplexValueParams() {
         return complexValueByParamName == null ? Collections.<String> emptyList() : new ArrayList<String>(complexValueByParamName.keySet());
      }
   }
}
