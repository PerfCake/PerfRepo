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
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.jboss.qa.perfrepo.controller.ControllerBase;
import org.jboss.qa.perfrepo.model.Metric;
import org.jboss.qa.perfrepo.model.Test;
import org.jboss.qa.perfrepo.model.TestExecution;
import org.jboss.qa.perfrepo.model.TestExecutionTag;
import org.jboss.qa.perfrepo.model.Value;
import org.jboss.qa.perfrepo.service.TestService;
import org.jboss.qa.perfrepo.session.TEComparatorSession;
import org.jboss.qa.perfrepo.viewscope.ViewScoped;
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
   @Inject
   private TestService service;

   @Inject
   private TEComparatorSession teComparator;

   private List<TestExecution> testExecutions = null;
   private Test test = null;
   private TestExecution baselineExecution = null;
   private XYDataSetCollection chartData;
   private FlotChartRendererData chart;

   public Test getTest() {
      return test;
   }

   public TestExecution getBaselineExecution() {
      return baselineExecution;
   }

   public void setAsBaseline(Long execId) {
      TestExecution baselineExec1 = findTestExecution(execId);
      if (baselineExec1 == null) {
         throw new IllegalStateException("Can't find execution " + execId);
      }
      baselineExecution = baselineExec1;
      chart = null;
      chartData = null;
   }

   public void removeFromComparison(Long execId) {
      teComparator.remove(execId);
      TestExecution execToRemove = findTestExecution(execId);
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

   public void createChart(Long metricId, boolean percents) {
      Metric metric = findMetric(metricId);
      if (metric == null) {
         log.error("Couldn't find metric " + metricId);
         return;
      }
      if (testExecutions == null || testExecutions.isEmpty()) {
         return;
      }
      chart = new FlotChartRendererData();
      chartData = new XYDataSetCollection();
      double minValue = Double.MAX_VALUE;
      double maxValue = Double.MIN_VALUE;
      XYDataList series = new XYDataList();
      series.setLabel(metric.getName() + (percents ? " % diff" : ""));
      int i = 0;
      for (TestExecution te : testExecutions) {
         Double value = percents ? getMetricValueBaselineComparedNum(te.getId(), metricId) : getMetricValueNum(te.getId(), metricId);
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

   private TestExecution findTestExecution(Long execId) {
      if (testExecutions == null || testExecutions.isEmpty()) {
         return null;
      } else {
         for (TestExecution e : testExecutions) {
            if (e.getId().equals(execId)) {
               return e;
            }
         }
         return null;
      }
   }

   public String getMetricValue(Long execId, Long metricId) {
      Double num = getMetricValueNum(execId, metricId);
      return num == null ? "N/A" : FMT.format(num);
   }

   private Double getMetricValueNum(Long execId, Long metricId) {
      TestExecution exec = findTestExecution(execId);
      if (exec == null) {
         return null;
      } else {
         Value v = findValue(exec, metricId);
         return v == null ? null : v.getResultValue();
      }
   }

   public String getMetricValueBaselineCompared(Long execId, Long metricId) {
      Double num = getMetricValueBaselineComparedNum(execId, metricId);
      return num == null ? "N/A" : (num < 0d ? "" : "+") + FMT_PERCENT.format(num) + " %";
   }

   private Double getMetricValueBaselineComparedNum(Long execId, Long metricId) {
      if (baselineExecution == null) {
         return null;
      }
      TestExecution exec = findTestExecution(execId);
      if (exec == null) {
         return null;
      } else {
         Value v = findValue(exec, metricId);
         if (v == null) {
            return null;
         } else {
            Value vBase = findValue(baselineExecution, metricId);
            if (vBase == null) {
               return null;
            } else {
               double dv = v.getResultValue();
               double dvbase = vBase.getResultValue();
               return ((dv - dvbase) / dvbase) * 100d;
            }
         }
      }
   }

   private Value findValue(TestExecution exec, Long metricId) {
      for (Value v : exec.getValues()) {
         if (v.getMetric().getId().equals(metricId)) {
            return v;
         }
      }
      return null;
   }

   private Metric findMetric(Long metricId) {
      if (test == null) {
         return null;
      }
      for (Metric m : test.getMetrics()) {
         if (m.getId().equals(metricId)) {
            return m;
         }
      }
      return null;
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

   /**
    * called on preRenderView
    */
   public void preRender() {
      reloadSessionMessages();
      if (testExecutions == null) {
         List<Long> execIdList = new ArrayList<Long>(teComparator.getExecIds());
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
            addMessage(ERROR, "page.compareExecs.errorDifferentTests");
            return;
         }
         test = service.getFullTest(testId);
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