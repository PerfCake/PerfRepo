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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.jboss.qa.perfrepo.controller.ControllerBase;
import org.jboss.qa.perfrepo.model.Metric;
import org.jboss.qa.perfrepo.model.Test;
import org.jboss.qa.perfrepo.model.TestExecution;
import org.jboss.qa.perfrepo.model.TestExecutionParameter;
import org.jboss.qa.perfrepo.model.TestExecutionTag;
import org.jboss.qa.perfrepo.service.TestService;
import org.jboss.qa.perfrepo.session.TEComparatorSession;
import org.jboss.qa.perfrepo.viewscope.ViewScoped;

/**
 * Simple comparison of test execution parameters.
 * 
 * @author Michal Linhard (mlinhard@redhat.com)
 * 
 */
@Named
@ViewScoped
public class CompareParametersController extends ControllerBase {

   private static Logger log = Logger.getLogger(CompareParametersController.class);

   private static final long serialVersionUID = 1L;
   @Inject
   private TestService service;

   @Inject
   private TEComparatorSession teComparator;

   private List<TestExecution> testExecutions = null;
   private Test test = null;
   private List<String> allParameters = null;

   public Test getTest() {
      return test;
   }

   public List<String> getAllParameters() {
      return allParameters;
   }

   private List<String> extractParams(List<TestExecution> testExecutions) {
      if (testExecutions == null || testExecutions.isEmpty()) {
         return Collections.emptyList();
      }
      Set<String> pset = new HashSet<String>();
      for (TestExecution te : testExecutions) {
         for (TestExecutionParameter p : te.getParameters()) {
            pset.add(p.getName());
         }
      }
      List<String> r = new ArrayList<String>(pset);
      Collections.sort(r);
      return r;
   }

   public void removeFromComparison(Long execId) {
      teComparator.remove(execId);
      TestExecution execToRemove = findTestExecution(execId);
      if (execToRemove != null && testExecutions != null) {
         testExecutions.remove(execToRemove);
      }
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
         Long testId = checkCommonTestId(testExecutions);
         if (testId == null) {
            addMessage(ERROR, "page.compareExecs.errorDifferentTests");
            return;
         }
         test = service.getFullTest(testId);
         allParameters = extractParams(testExecutions);
      }
   }

   public String getParamValue(TestExecution exec, String paramName) {
      TestExecutionParameter param = exec.findParameter(paramName);
      if (param == null) {
         return null;
      }
      String value = param.getValue();
      if (value == null) {
         return null;
      }
      if (value.startsWith("http://") || value.startsWith("https://")) {
         return "<a href=\"" + value + "\">" + value + "</a>";
      } else if (value.length() > 100) {
         return "<a href=\"/repo/param/" + param.getId() + "\">" + value.substring(0, 96) + " ...</a>";
      } else {
         return value;
      }
   }

   public boolean paramsEqual(String paramName) {
      if (testExecutions == null || testExecutions.isEmpty()) {
         return true;
      }
      TestExecutionParameter param = testExecutions.get(0).findParameter(paramName);
      String commonValue = param == null ? null : param.getValue();
      for (TestExecution exec : testExecutions) {
         param = exec.findParameter(paramName);
         if (param == null) {
            if (commonValue != null) {
               return false;
            }
         } else {
            String currentValue = param.getValue();
            if (currentValue == null) {
               if (commonValue != null) {
                  return false;
               }
            } else {
               if (!currentValue.equals(commonValue)) {
                  return false;
               }
            }
         }
      }
      return true;
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