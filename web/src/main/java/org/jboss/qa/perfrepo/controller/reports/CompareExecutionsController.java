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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.jboss.qa.perfrepo.controller.ControllerBase;
import org.jboss.qa.perfrepo.controller.JFreechartBean.XYLineChartSpec;
import org.jboss.qa.perfrepo.model.Metric;
import org.jboss.qa.perfrepo.model.Test;
import org.jboss.qa.perfrepo.model.TestExecution;
import org.jboss.qa.perfrepo.model.TestExecutionTag;
import org.jboss.qa.perfrepo.model.util.EntityUtil;
import org.jboss.qa.perfrepo.service.TestService;
import org.jboss.qa.perfrepo.session.TEComparatorSession;
import org.jboss.qa.perfrepo.util.MultiValue;
import org.jboss.qa.perfrepo.util.MultiValue.ParamInfo;
import org.jboss.qa.perfrepo.util.MultiValue.ValueInfo;
import org.jboss.qa.perfrepo.viewscope.ViewScoped;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jsflot.components.FlotChartRendererData;
import org.jsflot.xydata.XYDataList;
import org.jsflot.xydata.XYDataPoint;
import org.jsflot.xydata.XYDataSetCollection;

/**
 * Simple comparison of test execution values.
 * 
 * @author Michal Linhard (mlinhard@redhat.com)
 * 
 */
@Named
@ViewScoped
public class CompareExecutionsController extends ControllerBase {

   private static Logger log = Logger.getLogger(CompareExecutionsController.class);

   private static final long serialVersionUID = 1L;
   private static final DecimalFormat FMT = new DecimalFormat("##.0000");
   private static final DecimalFormat FMT_PERCENT = new DecimalFormat("0.00");
   private static final Comparator<String> COMPARE_PARAM_VALUE = new Comparator<String>() {
      @Override
      public int compare(String o1, String o2) {
         try {
            return Double.valueOf(o1).compareTo(Double.valueOf(o2));
         } catch (Exception e) {
            return o1.compareTo(o2);
         }
      }
   };

   @Inject
   private TestService service;

   @Inject
   private TEComparatorSession teComparator;

   private List<TestExecution> testExecutions = null;
   private Map<Long, Map<String, ValueInfo>> values = null;
   private Test test = null;
   private TestExecution baselineExecution = null;
   private XYDataSetCollection chartData;
   private FlotChartRendererData chart;

   private List<String> multiValueCompareList;
   private String multiValueCompareMetric;
   private String multiValueCompareParam;
   private List<String> multiValueCompareParamList;
   private XYLineChartSpec multiValueCompareChartData = null;

   public Test getTest() {
      return test;
   }

   public TestExecution getBaselineExecution() {
      return baselineExecution;
   }

   public void setAsBaseline(Long execId) {
      TestExecution baselineExec1 = EntityUtil.findById(testExecutions, execId);
      if (baselineExec1 == null) {
         throw new IllegalStateException("Can't find execution " + execId);
      }
      baselineExecution = baselineExec1;
      chart = null;
      chartData = null;
   }

   public void removeFromComparison(Long execId) {
      teComparator.remove(execId);
      TestExecution execToRemove = EntityUtil.findById(testExecutions, execId);
      if (baselineExecution == execToRemove) {
         baselineExecution = null;
      }
      if (execToRemove != null && testExecutions != null) {
         testExecutions.remove(execToRemove);
      }
      chart = null;
      chartData = null;
   }

   public boolean isBaseline(Long execId) {
      return baselineExecution != null && baselineExecution.getId().equals(execId);
   }

   public void createChart(String metricName, boolean percents) {
      if (testExecutions == null || testExecutions.isEmpty()) {
         return;
      }
      chart = new FlotChartRendererData();
      chartData = new XYDataSetCollection();
      double minValue = Double.MAX_VALUE;
      double maxValue = Double.MIN_VALUE;
      XYDataList series = new XYDataList();
      series.setLabel(metricName + (percents ? " % diff" : ""));
      int i = 0;
      for (TestExecution te : testExecutions) {
         Double value = percents ? getSimpleBaselineNum(te.getId(), metricName) : getSimpleNum(te.getId(), metricName);
         if (value != null) {
            XYDataPoint dp = new XYDataPoint(new Double(i), value, te.getName());
            series.addDataPoint(dp);
            if (value > maxValue) {
               maxValue = value;
            }
            if (value < minValue) {
               minValue = value;
            }
         }
         i++;
      }
      chartData.addDataList(series);
      double range = maxValue - minValue;
      chart.setYaxisMaxValue(maxValue + 0.1d * range);
      double yaxisMinValue = minValue - 0.1d * range;
      if (minValue >= 0d && yaxisMinValue < 0) {
         yaxisMinValue = 0d; // don't get below zero if min value isn't negative
      }
      chart.setYaxisMinValue(yaxisMinValue);
   }

   public FlotChartRendererData getChart() {
      return chart;
   }

   public XYDataSetCollection getChartData() {
      return chartData;
   }

   public List<String> getMultiValueCompareList() {
      return multiValueCompareList;
   }

   public String getMultiValueCompareParam() {
      return multiValueCompareParam;
   }

   public void setMultiValueCompareParam(String multiValueCompareParam) {
      this.multiValueCompareParam = multiValueCompareParam;
   }

   public List<String> getMultiValueCompareParamList() {
      return multiValueCompareParamList;
   }

   public XYLineChartSpec getMultiValueCompareChartData() {
      return multiValueCompareChartData;
   }

   private Map<Long, Map<String, ValueInfo>> computeValues() {
      if (testExecutions == null || testExecutions.isEmpty()) {
         return null;
      }
      Map<Long, Map<String, ValueInfo>> r = new TreeMap<Long, Map<String, ValueInfo>>();
      for (TestExecution testExecution : testExecutions) {
         List<ValueInfo> valueInfos = MultiValue.createFrom(testExecution);
         Map<String, ValueInfo> valueInfosForExec = new TreeMap<String, MultiValue.ValueInfo>();
         for (ValueInfo valueInfo : valueInfos) {
            valueInfosForExec.put(valueInfo.getMetricName(), valueInfo);
         }
         r.put(testExecution.getId(), valueInfosForExec);
      }
      return r;
   }

   public String getMetricValue(Long execId, String metricName) {
      ValueInfo valueInfo = findValueInfo(execId, metricName);
      if (valueInfo == null) {
         return "N/A";
      }
      if (valueInfo.isMultiValue()) {
         return "MULTIVALUE";
      } else {
         return valueInfo.getSimpleValue() == null ? "N/A" : FMT.format(valueInfo.getSimpleValue());
      }
   }

   public String getMetricBaselinedValue(Long execId, String metricName) {
      if (baselineExecution == null) {
         return "N/A";
      }
      ValueInfo val = findValueInfo(execId, metricName);
      ValueInfo valBase = findValueInfo(baselineExecution.getId(), metricName);
      if (val == null || valBase == null) {
         return "N/A";
      }
      if (val.isMultiValue() || valBase.isMultiValue()) {
         return "MULTIVALUE";
      }
      if (val.getSimpleValue() == null || valBase.getSimpleValue() == null) {
         return "N/A";
      }
      double dv = val.getSimpleValue();
      double dvbase = valBase.getSimpleValue();
      return FMT_PERCENT.format(((dv - dvbase) / dvbase) * 100d) + " %";
   }

   private ValueInfo findValueInfo(Long execId, String metricName) {
      if (values == null) {
         return null;
      }
      Map<String, ValueInfo> valueInfosForExec = values.get(execId);
      if (valueInfosForExec == null) {
         return null;
      }
      return valueInfosForExec.get(metricName);
   }

   private Double getSimpleNum(Long execId, String metricName) {
      ValueInfo val = findValueInfo(execId, metricName);
      return val == null ? null : val.getSimpleValue();
   }

   private Double getSimpleBaselineNum(Long execId, String metricName) {
      if (baselineExecution == null) {
         return null;
      }
      ValueInfo val = findValueInfo(execId, metricName);
      ValueInfo valBase = findValueInfo(baselineExecution.getId(), metricName);
      if (val == null || valBase == null || val.getSimpleValue() == null || valBase.getSimpleValue() == null) {
         return null;
      }
      double dv = val.getSimpleValue();
      double dvbase = valBase.getSimpleValue();
      return ((dv - dvbase) / dvbase) * 100d;
   }

   private Long checkCommonTestId(List<TestExecution> execs) {
      if (execs == null || execs.isEmpty()) {
         return null;
      }
      Long commonTestId = execs.iterator().next().getTest().getId();
      for (TestExecution exec : execs) {
         if (!commonTestId.equals(exec.getTest().getId())) {
            return null;
         }
      }
      return commonTestId;
   }

   public String getPermaLink() {
      return "/repo/reports/compare/exec?q=" + ParamUtil.generateExecQuery(testExecutions);
   }

   /**
    * called on preRenderView
    */
   public void preRender() {
      reloadSessionMessages();
      if (testExecutions == null) {
         List<Long> execIdList = ParamUtil.parseExecQuery(getRequestParam("q"));
         if (execIdList == null) {
            execIdList = new ArrayList<Long>(teComparator.getExecIds());
         } else {
            teComparator.clear();
            teComparator.getExecIds().addAll(execIdList);
            execIdList = new ArrayList<Long>(teComparator.getExecIds());
         }
         if (execIdList.isEmpty()) {
            addMessage(INFO, "page.compareExecs.nothingToCompare");
            return;
         }
         Collections.sort(execIdList);
         testExecutions = service.getFullTestExecutions(execIdList);
         // update teComparator, some of the executions might not exist anymore
         teComparator.clear();
         for (TestExecution te : testExecutions) {
            teComparator.add(te.getId());
         }
         Long testId = checkCommonTestId(testExecutions);
         if (testId == null) {
            log.error("Can't compare executions of different tests");
            addMessage(ERROR, "page.compareExecs.errorDifferentTests");
            return;
         }
         test = service.getFullTest(testId);
         values = computeValues();
      }
   }

   private List<ValueInfo> findValueInfos(String metricName) {
      if (testExecutions == null || values == null) {
         return Collections.emptyList();
      }
      List<ValueInfo> infos = new ArrayList<MultiValue.ValueInfo>(testExecutions.size());
      for (TestExecution exec : testExecutions) {
         ValueInfo vi = findValueInfo(exec.getId(), metricName);
         if (vi != null) {
            infos.add(vi);
         }
      }
      return infos;
   }

   public void showMultiValue(String metricName) {
      if (testExecutions == null || values == null) {
         return;
      }
      Set<String> stringSet = new HashSet<String>();
      List<ValueInfo> infos = findValueInfos(metricName);
      for (ValueInfo vi : infos) {
         stringSet.addAll(vi.getComplexValueParams());
      }
      multiValueCompareParamList = new ArrayList<String>(stringSet);
      Collections.sort(multiValueCompareParamList);
      if (multiValueCompareParamList.isEmpty()) {
         multiValueCompareParamList = null;
         return;
      }
      multiValueCompareParam = multiValueCompareParamList.get(0);
      multiValueCompareMetric = metricName;
      updateParamValues(stringSet, infos);
      multiValueCompareChartData = createChart();
   }

   private void updateParamValues(Set<String> stringSet, List<ValueInfo> infos) {
      stringSet.clear();
      for (ValueInfo vi : infos) {
         List<ParamInfo> cv = vi.getComplexValueByParamName(multiValueCompareParam);
         if (cv != null) {
            for (ParamInfo pi : cv) {
               stringSet.add(pi.getParamValue());
            }
         }
      }
      multiValueCompareList = new ArrayList<String>(stringSet);
      Collections.sort(multiValueCompareList, COMPARE_PARAM_VALUE);
   }

   public String getMultiValueCompare(Long execId, String paramValue) {
      if (multiValueCompareMetric == null || multiValueCompareParam == null) {
         return null;
      }
      ValueInfo vi = findValueInfo(execId, multiValueCompareMetric);
      if (vi == null) {
         return null;
      }
      List<ParamInfo> cv = vi.getComplexValueByParamName(multiValueCompareParam);
      if (cv == null) {
         return null;
      }
      for (ParamInfo pi : cv) {
         if (paramValue.equals(pi.getParamValue())) {
            return pi.getFormattedValue();
         }
      }
      return null;
   }

   public void updateParamSelection() {
      if (multiValueCompareMetric == null || multiValueCompareParam == null) {
         return;
      }
      updateParamValues(new HashSet<String>(), findValueInfos(multiValueCompareMetric));
      multiValueCompareChartData = createChart();
   }

   private XYLineChartSpec createChart() {
      try {
         if (multiValueCompareMetric == null || multiValueCompareParam == null || testExecutions == null) {
            return null;
         }
         XYSeriesCollection dataset = new XYSeriesCollection();
         for (TestExecution exec : testExecutions) {
            ValueInfo vi = findValueInfo(exec.getId(), multiValueCompareMetric);
            if (vi != null) {
               List<ParamInfo> pinfos = vi.getComplexValueByParamName(multiValueCompareParam);
               if (pinfos != null) {
                  XYSeries series = new XYSeries(exec.getName());
                  dataset.addSeries(series);

                  for (ParamInfo pinfo : pinfos) {
                     Double paramValue = Double.valueOf(pinfo.getParamValue());
                     if (paramValue != null) {
                        series.add(paramValue, pinfo.getValue());
                     }
                  }

               }
            }
         }
         XYLineChartSpec chartSpec = new XYLineChartSpec();
         chartSpec.title = "Multi-value for " + multiValueCompareMetric;
         chartSpec.xAxisLabel = multiValueCompareParam;
         chartSpec.yAxisLabel = "Metric value";
         chartSpec.dataset = dataset;
         return chartSpec;
      } catch (NumberFormatException e) {
         log.error("Can't chart non-numeric values");
         return null;
      } catch (Exception e) {
         log.error("Error while creating chart", e);
         return null;
      }
   }

   public List<Metric> getMetrics() {
      return test == null ? Collections.<Metric> emptyList() : new ArrayList<Metric>(test.getMetrics());
   }

   public List<TestExecution> getTestExecutions() {
      return testExecutions;
   }

   public String getTags(TestExecution te) {
      StringBuilder tag = new StringBuilder();
      for (TestExecutionTag teg : te.getTestExecutionTags()) {
         tag.append(teg.getTag().getName()).append("\n");
      }
      return tag.toString().substring(0, tag.length() - 1);
   }

}