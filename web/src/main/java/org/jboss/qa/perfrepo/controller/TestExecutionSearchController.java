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
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.qa.perfrepo.model.TestExecution;
import org.jboss.qa.perfrepo.model.to.TestExecutionSearchTO;
import org.jboss.qa.perfrepo.model.to.TestExecutionSearchTO.ParamCriteria;
import org.jboss.qa.perfrepo.model.util.EntityUtil;
import org.jboss.qa.perfrepo.model.util.ExecutionSort;
import org.jboss.qa.perfrepo.model.util.ExecutionSort.ParamExecutionSort;
import org.jboss.qa.perfrepo.service.exceptions.ServiceException;
import org.jboss.qa.perfrepo.service.TestService;
import org.jboss.qa.perfrepo.session.SearchCriteriaSession;
import org.jboss.qa.perfrepo.session.TEComparatorSession;
import org.jboss.qa.perfrepo.util.TagUtils;
import org.jboss.qa.perfrepo.util.ViewUtils;
import org.jboss.qa.perfrepo.viewscope.ViewScoped;

/**
 * Search test executions.
 * 
 * @author Pavel Drozd (pdrozd@redhat.com)
 * @author Michal Linhard (mlinhard@redhat.com)
 * @author Jiri Holusa (jholusa@redhat.com)
 * 
 */
@Named
@ViewScoped
public class TestExecutionSearchController extends BaseController {

   private static final long serialVersionUID = 1L;

   @Inject
   private TestService testService;

   @Inject
   private SearchCriteriaSession criteriaSession;

   @Inject
   private TEComparatorSession comparatorSession;

   private String tag;

   private List<TestExecution> result;
   private List<String> paramColumns;

   private String[] extraColumns = new String[0];

   private boolean showMassOperations = false;
   private String massOperationAddTags;
   private String massOperationDeleteTags;
   private int massOperationDeleteExecutionsConfirm;

   private ExecutionSort sort;

   public String[] getExtraColumns() {
      return extraColumns;
   }

   public void setExtraColumns(String[] extraColumns) {
      this.extraColumns = extraColumns;
   }

   public List<String> getParamColumns() {
      return paramColumns;
   }

   public void preRender() {
      if (sort == null) {
         sort = criteriaSession.getExecutionSearchSort();
         search();
      }
   }

   public String getTag() {
      return tag;
   }

   public void setTag(String tag) {
      this.tag = tag;
   }

   public void search() {
      TestExecutionSearchTO criteria = criteriaSession.getExecutionSearchCriteria();
      result = testService.searchTestExecutions(criteria);
      paramColumns = new ArrayList<String>(3);
      for (ParamCriteria pc : criteria.getParameters()) {
         if (pc.isDisplayed()) {
            paramColumns.add(pc.getName());
         }
      }
      Collections.sort(result, sort);
   }

   public String itemParam(TestExecution exec, String paramName) {
      return ViewUtils.displayValue(exec.findParameter(paramName));
   }

   public void addParameterCriteria() {
      criteriaSession.getExecutionSearchCriteria().getParameters().add(new TestExecutionSearchTO.ParamCriteria());
   }

   public void removeParameterCriteria(TestExecutionSearchTO.ParamCriteria criteriaToRemove) {
      criteriaSession.getExecutionSearchCriteria().getParameters().remove(criteriaToRemove);
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

   public boolean isShowMassOperations() {
      return showMassOperations;
   }

   public void setShowMassOperations(boolean showMassOperations) {
      this.showMassOperations = showMassOperations;
   }

   public void toggleShowMassOperations() {
      this.showMassOperations = !showMassOperations;
   }

   public String getMassOperationAddTags() {
      return massOperationAddTags;
   }

   public void setMassOperationAddTags(String massOperationAddTags) {
      this.massOperationAddTags = massOperationAddTags;
   }

   public String getMassOperationDeleteTags() {
      return massOperationDeleteTags;
   }

   public void setMassOperationDeleteTags(String massOperationDeleteTags) {
      this.massOperationDeleteTags = massOperationDeleteTags;
   }

   public int getMassOperationDeleteExecutionsConfirm() {
      return massOperationDeleteExecutionsConfirm;
   }

   public void setMassOperationDeleteExecutionsConfirm(int massOperationDeleteExecutionsConfirm) {
      this.massOperationDeleteExecutionsConfirm = massOperationDeleteExecutionsConfirm;
   }

   public int getResultSize() {
      return result != null ? result.size() : 0;
   }

   private ExecutionSort.Type getSortType(String what, boolean num) {
      // all sorts are ascending in this phase
      if ("id".equals(what)) {
         return ExecutionSort.Type.ID;
      } else if ("name".equals(what)) {
         return ExecutionSort.Type.NAME;
      } else if ("started".equals(what)) {
         return ExecutionSort.Type.TIME;
      } else if ("test".equals(what)) {
         return ExecutionSort.Type.TEST_NAME;
      } else if (paramColumns.contains(what)) {
         return num ? ExecutionSort.Type.PARAM_DOUBLE : ExecutionSort.Type.PARAM_STRING;
      } else {
         throw new IllegalArgumentException("unknown sort type");
      }
   }

   public void sortBy(String what, boolean num) {
      ExecutionSort.Type type = getSortType(what, num);
      boolean invertAscending = false;
      if (type.equals(sort.type())) {
         if (type.isParametrized()) {
            ParamExecutionSort<?> psort = (ParamExecutionSort<?>) sort;
            if (what.equals(psort.getParam())) {
               invertAscending = true;
            }
         } else {
            invertAscending = true;
         }
      }
      sort = ExecutionSort.create(type, what, invertAscending ? !sort.isAscending() : sort.isAscending());
      criteriaSession.setExecutionSearchSort(sort);
      Collections.sort(result, sort);
   }

   public void addAllCurrentResultsToComparison() {
      List<Long> ids = EntityUtil.extractIds(result);
      if (ids != null) {
         for (Long id : ids) {
            comparatorSession.add(id);
         }
      }
   }
   
   public List<String> autocompleteTest(String test) {
		return testService.getTestsByPrefix(test);
   }
   
   public List<String> autocompleteTags(String tag) {
      String returnPrefix = "";
      if(tag.startsWith("-")) {
         tag = tag.substring(1);
         returnPrefix = "-";
      }

      List<String> tmp = testService.getTagsByPrefix(tag);
      List<String> result = new ArrayList<String>(tmp.size());
      if(!returnPrefix.isEmpty()) {
         for(String item: tmp) {
            result.add(returnPrefix + item);
         }
      }
      else {
         result.addAll(tmp);
      }

		return result;
  }

   public boolean isDisplayColumn(String name) {
      if (extraColumns == null) {
         return false;
      }
      for (String ec : extraColumns) {
         if (name.equals(ec)) {
            return true;
         }
      }
      return false;
   }

   public void addTagsToFoundTestExecutions() {
      List<String> tags = TagUtils.parseTags(massOperationAddTags != null ? massOperationAddTags.toLowerCase() : "");

      testService.addTagsToTestExecutions(tags, result);
      search();
   }

   public void deleteTagsFromFoundTestExecutions() {
      List<String> tags = TagUtils.parseTags(massOperationDeleteTags !=null ? massOperationDeleteTags.toLowerCase() : "");

      testService.deleteTagsFromTestExecutions(tags, result);
      search();
   }

   public void deleteFoundTestExecutions() {
      for(TestExecution testExecution: result) {
         try {
            testService.deleteTestExecution(testExecution);
         } catch (ServiceException ex) {
            //TODO: how to handle this properly?
            throw new RuntimeException(ex);
         }
      }

      search();
   }
}