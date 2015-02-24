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
package org.perfrepo.web.alerting;


import org.perfrepo.model.Metric;
import org.perfrepo.model.Test;

/**
 * API for checking of condition for alerting.
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public interface ConditionChecker {

   /**
    * Checks the condition for alerting
    *
    * @param condition specification of condition in String in our custom DSL that should hold
    * @param currentResult result of the test execution that is currently being processed, e.g. against which the
    *                      condition should hold. This value will replace the "result" variable in condition!
    * @param test test that the condition is linked to
    * @param metric metric that the condition is linked to
    * @return true if condition still hold | false if condition is broken
    */
   public boolean checkCondition(String condition, double currentResult, Test test, Metric metric);
}
