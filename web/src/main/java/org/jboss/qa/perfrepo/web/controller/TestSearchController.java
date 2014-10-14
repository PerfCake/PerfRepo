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
package org.jboss.qa.perfrepo.web.controller;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.qa.perfrepo.model.Test;
import org.jboss.qa.perfrepo.model.to.TestSearchTO;
import org.jboss.qa.perfrepo.web.service.exceptions.ServiceException;
import org.jboss.qa.perfrepo.web.service.TestService;
import org.jboss.qa.perfrepo.web.session.SearchCriteriaSession;
import org.jboss.qa.perfrepo.web.viewscope.ViewScoped;

/**
 * Search tests.
 * 
 * @author Pavel Drozd (pdrozd@redhat.com)
 * @author Michal Linhard (mlinhard@redhat.com)
 * 
 */
@Named
@ViewScoped
public class TestSearchController extends BaseController {

   private static final long serialVersionUID = 1L;

   @Inject
   private TestService testService;

   @Inject
   private SearchCriteriaSession criteriaSession;

   private TestSearchTO criteria = null;

   private List<Test> result;

   public void preRender() {
      if (criteria == null) {
         criteria = criteriaSession.getTestSearchCriteria();
         search();
      }
   }

   public void search() {
      result = testService.searchTest(criteria);
   }

   public List<Test> getResult() {
      return result;
   }

   public void setResult(List<Test> result) {
      this.result = result;
   }

   public TestSearchTO getCriteria() {
      return criteria;
   }

   public void setCriteria(TestSearchTO search) {
      this.criteria = search;
   }

   public String delete() {
      Long idToDelete = Long.valueOf(getRequestParam("idToDelete"));
      if (idToDelete == null) {
         throw new IllegalStateException("Bad request, missing idToDelete");
      } else {
         Test testToRemove = removeById(idToDelete);
         if (testToRemove == null) {
            throw new IllegalStateException("Bad request, missing idToDelete");
         } else {
            try {
               testService.removeTest(testToRemove);
               addMessage(INFO, "page.testSearch.testDeleted", testToRemove.getName());
            } catch (ServiceException e) {
               addMessage(e);
            }
         }
      }
      return null;
   }

   public List<String> autocompleteTest(String test) {
		return testService.getTestsByPrefix(test);
   }

   private Test removeById(Long id) {
      for (Test test : result) {
         if (test.getId().equals(id)) {
            result.remove(test);
            return test;
         }
      }
      return null;
   }
}