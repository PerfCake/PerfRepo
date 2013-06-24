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
package org.jboss.qa.perfrepo.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.qa.perfrepo.model.Metric;
import org.jboss.qa.perfrepo.model.Test;
import org.jboss.qa.perfrepo.model.TestMetric;
import org.jboss.qa.perfrepo.service.ServiceException;
import org.jboss.qa.perfrepo.service.TestService;
import org.jboss.qa.perfrepo.viewscope.ViewScoped;

@Named
@ViewScoped
public class TestController extends ControllerBase {

   private static final long serialVersionUID = 370202307562230671L;

   @Inject
   TestService testService;

   private Test test = null;
   
   private Metric metric = null;
   
   @PostConstruct
   public void init() {
      String id;
      if (test == null) {
         if ((id = getRequestParam("testId")) != null) {
            test = testService.getTest(Long.valueOf(id));
         } else {
            test = new Test();
         }
      }
   }

   public Test getTest() {      
      return test;
   }
   
   public void setTest(Test test) {
      this.test = test;
   }
      
   public Metric getMetric() {
      return metric;
   }

   public void setMetric(Metric metric) {
      this.metric = metric;
   }

   public String update() {
      if (test != null) {
         testService.updateTest(test);
         return "/test/detail.xhtml?testId=";
      }
      return null;
   }
   
   public void createMetric() {
      metric = new Metric();
   }
   
   public void updateMetric() {
      if (metric != null) {
         testService.updateMetric(metric);
      }
   }
   
   public void addMetric() {
      if (metric != null && test != null) {
         TestMetric tm = testService.addMetric(test, metric);
         test.getTestMetrics().add(tm);
      }
   }
   
   public void deleteTestMetric(TestMetric tm) {
      if (metric != null && test!= null) {
         testService.deleteTestMetric(tm);
         test.getTestMetrics().remove(tm);
      }
   }
   
   public List<TestMetric> getMetricsList() {
      List<TestMetric> tm = new ArrayList<TestMetric>();
      if (test != null) {
         tm.addAll(test.getTestMetrics());
      }
      return tm;
   }
   
   public List<Metric> getAvailableMetricsByTest() {
      return testService.getAvailableMetrics(test);
   }

   public String create() {
      if (test != null) {
         testService.createTest(test);
      }
      return "TestList";
   }

   public String delete() {
      Test testToDelete = test;
      if (test == null) {
         testToDelete = new Test();
         testToDelete.setId(new Long(getRequestParam("testId")));
      }
      try {
         testService.deleteTest(testToDelete);
      } catch (ServiceException e) {
         // TODO: how to handle exceptions in web layer?
         throw new RuntimeException(e);
      }
      return "TestList";
   }

}
