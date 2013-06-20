package org.jboss.qa.perfrepo.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.qa.perfrepo.model.Test;
import org.jboss.qa.perfrepo.model.to.TestSearchTO;
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
         result= new ArrayList<Test>();
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
}