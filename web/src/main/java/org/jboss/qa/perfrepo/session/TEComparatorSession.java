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
package org.jboss.qa.perfrepo.session;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.qa.perfrepo.service.TestService;

@Named(value="teComparatorSession")
@SessionScoped
public class TEComparatorSession implements Serializable {

   private static final long serialVersionUID = 1L;

   @Inject
   private TestService testExecutionService;

   private Set<Long> testExecutions = new HashSet<Long>();
   
   public void add(Long te) {
     testExecutions.add(te);
   }   
   
   public void remove(Long id) {
      testExecutions.remove(id);
   }
   
   public Collection<Long> getTestExecutions() {
      return testExecutions;
   }
   
   public boolean isAnyToCompare()  {
      return testExecutions != null && testExecutions.size() > 0;
   }
   
}