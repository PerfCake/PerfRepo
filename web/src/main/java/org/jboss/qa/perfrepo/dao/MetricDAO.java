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
package org.jboss.qa.perfrepo.dao;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.inject.Named;

import org.jboss.qa.perfrepo.model.Metric;

@Named
public class MetricDAO extends DAO<Metric, Long> {

   public Metric findByName(String name) {
      List<Metric> metrics = findAllByProperty("name", name);
      if (metrics.size() > 0)
         return metrics.get(0);
      return null;
   }
   
   public List<Metric> getMetrics() {
      return findAll();
   }
   
   /**
    * Returns all metrics by name prefix, which belong tests with defined group id
    * @param namePart
    * @param guid
    * @return
    */
   public List<Metric> getMetricByNameAndGroup(String name, String groupId) {
      Map<String, Object> params = new TreeMap<>();
      params.put("groupId", groupId);
      params.put("name", name);
      return findByNamedQuery(Metric.FIND_BY_NAME_GROUPID, params);
   }
   
   /**
    * Returns all metrics which belong tests with defined group id
    * @param namePart
    * @param guid
    * @return
    */
   public List<Metric> getMetricByGroup(String groupId) {
      Map<String, Object> params = new TreeMap<>();
      params.put("groupId", groupId);      
      return findByNamedQuery(Metric.FIND_BY_GROUPID, params);
   }

}