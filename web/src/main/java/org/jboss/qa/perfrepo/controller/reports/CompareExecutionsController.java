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

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.qa.perfrepo.controller.ControllerBase;
import org.jboss.qa.perfrepo.model.Metric;
import org.jboss.qa.perfrepo.model.Test;
import org.jboss.qa.perfrepo.model.TestExecution;
import org.jboss.qa.perfrepo.model.TestExecutionTag;
import org.jboss.qa.perfrepo.model.Value;
import org.jboss.qa.perfrepo.service.TestService;
import org.jboss.qa.perfrepo.session.TEComparatorSession;

/**
 * Simple comparison of test execution values.
 * 
 * TODO: - select a baseline - table with percentage diff from baseline - chart selected metric
 * comparison with barchart
 * 
 * @author Michal Linhard (mlinhard@redhat.com)
 * 
 */
@Named
@RequestScoped
public class CompareExecutionsController extends ControllerBase {

   private static final long serialVersionUID = 1L;
   private static final DecimalFormat FMT = new DecimalFormat("##.####");
   @Inject
   private TestService service;

   @Inject
   private TEComparatorSession teComparator;

   private List<TestExecution> testExecutions = null;
   private Test test = null;

   public Test getTest() {
      return test;
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
      TestExecution exec = findTestExecution(execId);
      if (exec == null) {
         return null;
      } else {
         Value v = findValue(exec, metricId);
         if (v == null) {
            return "N/A";
         } else {
            return FMT.format(v.getResultValue());
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
   }

   @PostConstruct
   public void init() {
      List<Long> execIdList = new ArrayList<Long>(teComparator.getTestExecutions());
      if (execIdList.isEmpty()) {
         addMessage(INFO, "page.compareExecs.nothingToCompare");
         return;
      }
      Collections.sort(execIdList);
      testExecutions = service.getFullTestExecutions(execIdList);
      Long testId = checkCommonTestId(testExecutions);
      if (testId == null) {
         addMessage(ERROR, "page.compareExecs.errorDifferentTests");
         return;
      }
      test = service.getFullTest(testId);
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