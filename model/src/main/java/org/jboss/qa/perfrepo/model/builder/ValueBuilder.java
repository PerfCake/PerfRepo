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
package org.jboss.qa.perfrepo.model.builder;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.jboss.qa.perfrepo.model.Metric;
import org.jboss.qa.perfrepo.model.Value;
import org.jboss.qa.perfrepo.model.ValueParameter;

/**
 * {@link Value} builder
 * 
 * @author Michal Linhard (mlinhard@redhat.com)
 * 
 */
public class ValueBuilder {
   private TestExecutionBuilder parentBuilder;
   private Value value;

   ValueBuilder(TestExecutionBuilder parentBuilder, Value value) {
      super();
      this.parentBuilder = parentBuilder;
      this.value = value;
   }

   private ValueParameter addParameter(ValueParameter param) {
      Collection<ValueParameter> parameters = value.getParameters();
      if (parameters == null) {
         parameters = new HashSet<ValueParameter>();
         value.setParameters((Set<ValueParameter>) parameters);
      }
      parameters.add(param);
      return param;
   }

   public ValueBuilder parameter(String name, String value) {
      addParameter(new ValueParameter(name, value));
      return this;
   }

   public ValueBuilder metricName(String metricName) {
      Metric metric = value.getMetric();
      if (metric == null) {
         metric = new Metric();
         value.setMetric(metric);
      }
      metric.setName(metricName);
      return this;
   }

   public ValueBuilder resultValue(Double resultValue) {
      value.setResultValue(resultValue);
      return this;
   }

   public ValueBuilder name(String name) {
      value.setName(name);
      return this;
   }

   public TestExecutionBuilder execution() {
      return parentBuilder;
   }

}
