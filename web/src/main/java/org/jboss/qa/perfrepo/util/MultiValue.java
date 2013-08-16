package org.jboss.qa.perfrepo.util;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.log4j.Logger;
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

   private static final Logger log = Logger.getLogger(MultiValue.class);

   /**
    * Finds {@link ValueInfo} for {@link Value} by id.
    * 
    * @param value
    * @return
    */
   public static ValueInfo find(List<ValueInfo> valueInfos, Value value) {
      if (valueInfos == null || value == null) {
         return null;
      }
      for (ValueInfo vInfo : valueInfos) {
         if (vInfo.simpleValue != null && vInfo.simpleValue.getId().equals(value.getId())) {
            return vInfo;
         } else if (vInfo.complexValueByParamName != null) {
            for (List<ParamInfo> pInfoList : vInfo.complexValueByParamName.values()) {
               for (ParamInfo pInfo : pInfoList) {
                  if (pInfo.value.getId().equals(value.getId())) {
                     return vInfo;
                  }
               }
            }
         }
      }
      return null;
   }

   /**
    * Find value info by metric name.
    * 
    * @param valueInfos
    * @param metricName
    * @return
    */
   public static ValueInfo find(List<ValueInfo> valueInfos, String metricName) {
      if (valueInfos == null || metricName == null) {
         return null;
      }
      for (ValueInfo vInfo : valueInfos) {
         if (vInfo.metricName.equals(metricName)) {
            return vInfo;
         }
      }
      return null;
   }

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
            vInfo.simpleValue = entry.getValue().get(0);
         } else {
            vInfo.complexValueByParamName = new TreeMap<String, List<ParamInfo>>();
            for (Value v : entry.getValue()) {
               if (v.getParameters() == null || v.getParameters().isEmpty()) {
                  ValueParameter errorParameter = new ValueParameter();
                  errorParameter.setName("ERROR");
                  errorParameter.setParamValue("This value is erroneous.");
                  v.setParameters(Collections.singletonList(errorParameter));
                  log.error("Multi-value " + v.getResultValue() + " for metric " + vInfo.metricName + " is not perametrized");
               }
               for (ValueParameter vp : v.getParameters()) {
                  List<ParamInfo> paramInfos = vInfo.complexValueByParamName.get(vp.getName());
                  if (paramInfos == null) {
                     paramInfos = new ArrayList<ParamInfo>();
                     vInfo.complexValueByParamName.put(vp.getName(), paramInfos);
                  }
                  ParamInfo paramInfo = new ParamInfo();
                  paramInfo.param = vp;
                  paramInfo.value = v;
                  paramInfos.add(paramInfo);
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

   public static class ParamInfo implements Comparable<ParamInfo>, Serializable {
      private ValueParameter param;
      private Value value;

      public String getParamValue() {
         return param == null ? null : param.getParamValue();
      }

      public Double getValue() {
         return value == null ? null : value.getResultValue();
      }

      @Override
      public int compareTo(ParamInfo o) {
         try {
            return Double.valueOf(this.getParamValue()).compareTo(Double.valueOf(o.getParamValue()));
         } catch (NumberFormatException e) {
            return this.getParamValue().compareTo(o.getParamValue());
         }
      }

      public String getFormattedValue() {
         return getValue() == null ? null : FMT.format(getValue());
      }

      public Value getEntity() {
         return value;
      }
   }

   public static class ValueInfo implements Comparable<ValueInfo>, Serializable {
      private String metricName;
      private Value simpleValue;
      private SortedMap<String, List<ParamInfo>> complexValueByParamName;

      public String getMetricName() {
         return metricName;
      }

      public Value getEntity() {
         return simpleValue;
      }

      public Double getSimpleValue() {
         return simpleValue == null ? null : simpleValue.getResultValue();
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
         return simpleValue == null ? null : FMT.format(simpleValue.getResultValue());
      }

      public List<String> getComplexValueParams() {
         return complexValueByParamName == null ? Collections.<String> emptyList() : new ArrayList<String>(complexValueByParamName.keySet());
      }
   }
}
