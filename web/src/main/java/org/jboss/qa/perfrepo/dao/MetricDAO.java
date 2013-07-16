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

import javax.inject.Named;

import org.jboss.qa.perfrepo.model.Metric;

/**
 * DAO for {@link Metric}
 * 
 * @author Pavel Drozd (pdrozd@redhat.com)
 * @author Michal Linhard (mlinhard@redhat.com)
 * 
 */
@Named
public class MetricDAO extends DAO<Metric, Long> {

   public List<Metric> getMetrics() {
      return findAll();
   }

   /**
    * Returns all metrics by name prefix, which belong tests with defined group id
    * 
    * @param namePart
    * @param guid
    * @return
    */
   public List<Metric> getMetricByNameAndGroup(String name, String groupId) {
      return findByNamedQuery(Metric.FIND_BY_NAME_GROUPID, true, "groupId", groupId, "name", name);
   }

   /**
    * Returns all metrics which belong tests with defined group id
    * 
    * @param namePart
    * @param guid
    * @return
    */
   public List<Metric> getMetricByGroup(String groupId) {
      return findByNamedQuery(Metric.FIND_BY_GROUPID, true, "groupId", groupId);
   }

   /**
    * 
    * @param testId
    * @return All metrics under given test id.
    */
   public List<Metric> getMetricByTest(Long testId) {
      return findByNamedQuery(Metric.FIND_BY_TESTID, true, "testId", testId);
   }

}