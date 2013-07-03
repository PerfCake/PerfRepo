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

import org.apache.log4j.Logger;
import org.jboss.qa.perfrepo.model.Test;
import org.jboss.qa.perfrepo.model.to.TestSearchTO;
import org.jboss.qa.perfrepo.service.ServiceException;
import org.jboss.qa.perfrepo.service.TestService;
import org.jboss.qa.perfrepo.viewscope.ViewScoped;

@Named
@ViewScoped
public class TestSearchController extends ControllerBase {

   private static final long serialVersionUID = 1L;
   private static final Logger log = Logger.getLogger(TestSearchController.class);

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
      // TODO: learn error message handling and display
      Long idToDelete = Long.valueOf(getRequestParam("idToDelete"));
      if (idToDelete == null) {
         log.error("Bad request, missing idToDelete");
         return "Search";
      } else {
         Test testToRemove = removeById(idToDelete);
         if (testToRemove == null) {
            log.error("Bad request, test execution " + idToDelete + " not found among current search results");
            return "Search";
         } else {
            try {
               testService.deleteTest(testToRemove);
               return null;
            } catch (ServiceException e) {
               return "Search";
            }
         }
      }
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