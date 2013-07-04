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

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.qa.perfrepo.model.Metric;
import org.jboss.qa.perfrepo.service.TestService;

@Named
@RequestScoped
public class MetricController extends ControllerBase {

   private static final long serialVersionUID = -2267332752408811209L;

   @Inject
   TestService testService;

   private Metric metric = null;

   private List<Metric> metricList = null;

   public Metric getMetric() {
      String id = null;
      if (metric == null) {
         if ((id = getRequestParam("metricId")) != null) {
            metric = testService.getFullMetric(Long.valueOf(id));
         } else {
            metric = new Metric();
         }
      }
      return metric;
   }

   public List<Metric> getMetricList() {
      if (metricList == null) {
         metricList = testService.getAllFullMetrics();
      }
      return metricList;
   }

   public String update() {
      if (metric != null) {
         testService.updateMetric(metric);
      }
      return "MetricList";
   }

   public String create() {
      throw new UnsupportedOperationException();
      //      //      if (metric != null) {
      //      //         testService.addMetric(metric.get metric);
      //      //      }
      //      return "MetricList";
   }

   public String delete(Metric metric) {
      testService.deleteMetric(metric);
      return "MetricList";
   }

}