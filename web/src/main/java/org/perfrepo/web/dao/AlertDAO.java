/**
 *
 * PerfRepo
 *
 * Copyright (C) 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.perfrepo.web.dao;

import org.perfrepo.model.Alert;
import org.perfrepo.model.Metric;
import org.perfrepo.model.Test;

import javax.inject.Named;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO for {@link org.perfrepo.model.Alert}
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
@Named
public class AlertDAO extends DAO<Alert, Long> {

   /**
    * TODO: document this
    *
    * @param test
    * @param metric
    * @return
    */
   public List<Alert> getByTestAndMetric(Test test, Metric metric) {
      Map<String, Object> params = new HashMap<String, Object>();
      params.put("testId", test.getId());
      params.put("metricId", metric.getId());
      return findByNamedQuery(Alert.GET_BY_TEST_AND_METRIC, params);
   }

}
