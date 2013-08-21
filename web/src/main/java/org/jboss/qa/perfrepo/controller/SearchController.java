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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.qa.perfrepo.model.TestExecution;
import org.jboss.qa.perfrepo.model.TestExecutionParameter;
import org.jboss.qa.perfrepo.model.to.TestExecutionSearchTO;
import org.jboss.qa.perfrepo.model.to.TestExecutionSearchTO.ParamCriteria;
import org.jboss.qa.perfrepo.service.ServiceException;
import org.jboss.qa.perfrepo.service.TestService;
import org.jboss.qa.perfrepo.session.SearchCriteriaSession;
import org.jboss.qa.perfrepo.util.Util;
import org.jboss.qa.perfrepo.viewscope.ViewScoped;

/**
 * Search test executions.
 * 
 * @author Pavel Drozd (pdrozd@redhat.com)
 * @author Michal Linhard (mlinhard@redhat.com)
 * 
 */
@Named
@ViewScoped
public class SearchController extends ControllerBase {

   private static final long serialVersionUID = 1L;

   //private static final Logger log = Logger.getLogger(SearchController.class);

   @Inject
   private TestService testService;

   @Inject
   private SearchCriteriaSession criteriaSession;

   private String tag;

   private TestExecutionSearchTO criteria = null;

   private List<TestExecution> result;
   private List<String> paramColumns;

   public List<String> getParamColumns() {
      return paramColumns;
   }

   public void preRender() {
      if (criteria == null) {
         criteria = criteriaSession.getExecutionSearchCriteria();
         search();
      }
   }

   public TestExecutionSearchTO getCriteria() {
      return criteria;
   }

   public String getTag() {
      return tag;
   }

   public void setTag(String tag) {
      this.tag = tag;
   }

   public void search() {
      result = testService.searchTestExecutions(criteria);
      paramColumns = new ArrayList<String>(3);
      for (ParamCriteria pc : criteria.getParameters()) {
         if (pc.isDisplayed()) {
            paramColumns.add(pc.getName());
         }
      }
   }

   public String itemParam(TestExecution exec, String paramName) {
      return Util.displayValue(exec.findParameter(paramName));
   }

   public void addParameterCriteria() {
      criteria.getParameters().add(new TestExecutionSearchTO.ParamCriteria());
   }

   public void removeParameterCriteria(TestExecutionSearchTO.ParamCriteria criteriaToRemove) {
      criteria.getParameters().remove(criteriaToRemove);
   }

   public String delete() {
      Long idToDelete = Long.valueOf(getRequestParam("idToDelete"));
      if (idToDelete == null) {
         throw new IllegalStateException("Bad request, missing idToDelete");
      } else {
         TestExecution execToRemove = removeById(idToDelete);
         if (execToRemove == null) {
            throw new IllegalStateException("Bad request, missing idToDelete");
         } else {
            try {
               testService.deleteTestExecution(execToRemove);
               addMessage(INFO, "page.execSearch.execSucessfullyDeleted", execToRemove.getName());
            } catch (ServiceException e) {
               addMessageFor(e);
            }
         }
      }
      return null;
   }

   private TestExecution removeById(Long id) {
      for (TestExecution exec : result) {
         if (exec.getId().equals(id)) {
            result.remove(exec);
            return exec;
         }
      }
      return null;
   }

   public List<TestExecution> getResult() {
      return result;
   }

   public void setResult(List<TestExecution> result) {
      this.result = result;
   }

   private class ParamComparator implements Comparator<TestExecution> {

      private String param;
      private boolean num;

      public ParamComparator(String param, boolean num) {
         super();
         this.param = param;
         this.num = num;
      }

      @Override
      public int compare(TestExecution o1, TestExecution o2) {
         if (o1 == null) {
            return o2 == null ? 0 : -1;
         } else {
            return o2 == null ? 1 : compareNotNull(o1, o2);
         }
      }

      private int compareNotNull(TestExecution o1, TestExecution o2) {
         TestExecutionParameter p1 = o1.findParameter(param);
         TestExecutionParameter p2 = o2.findParameter(param);
         if (p1 == null || p1.getValue() == null) {
            return p2 == null || p2.getValue() == null ? 0 : -1;
         } else {
            try {
               return p2 == null || p2.getValue() == null ? 1 : (num ? new Double(p1.getValue()).compareTo(new Double(p2.getValue())) : p1.getValue()
                     .compareTo(p2.getValue()));
            } catch (NumberFormatException e) {
               return p1.getValue().compareTo(p2.getValue());
            }
         }
      }

   }

   public void sortBy(String what, boolean num) {
      if ("id".equals(what)) {
         Collections.sort(result, TestExecution.SORT_BY_ID);
      } else if ("name".equals(what)) {
         Collections.sort(result, TestExecution.SORT_BY_NAME);
      } else if ("started".equals(what)) {
         Collections.sort(result, TestExecution.SORT_BY_STARTED);
      } else if ("test".equals(what)) {
         Collections.sort(result, TestExecution.SORT_BY_TEST_NAME);
      } else if (paramColumns.contains(what)) {
         Collections.sort(result, new ParamComparator(what, num));
      } else {
         throw new IllegalArgumentException("unknown sort criteria");
      }
   }
}