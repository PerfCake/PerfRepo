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
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.qa.perfrepo.model.Test;
import org.jboss.qa.perfrepo.model.to.TestSearchTO;
import org.jboss.qa.perfrepo.service.ServiceException;
import org.jboss.qa.perfrepo.service.TestService;
import org.jboss.qa.perfrepo.viewscope.ViewScoped;

@Named
@ViewScoped
public class TestSearchController extends ControllerBase {

   private static final long serialVersionUID = 1L;

   @Inject
   private TestService testService;

   private TestSearchTO search = null;

   private List<Test> result;

   @PostConstruct
   public void init() {
      if (search == null) {
         search = new TestSearchTO();
      }
      if (result == null) {
         result = new ArrayList<Test>();
      }
   }

   public void search() {
      result = testService.searchTest(search);
   }

   public List<Test> getResult() {
      return result;
   }

   public void setResult(List<Test> result) {
      this.result = result;
   }

   public TestSearchTO getSearch() {
      return search;
   }

   public void setSearch(TestSearchTO search) {
      this.search = search;
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
               testService.deleteTest(testToRemove);
               addMessage(INFO, "page.testSearch.testDeleted", testToRemove.getName());
            } catch (ServiceException e) {
               addMessageFor(e);
            }
         }
      }
      return null;
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