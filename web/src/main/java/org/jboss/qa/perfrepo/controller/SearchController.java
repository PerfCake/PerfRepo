package org.jboss.qa.perfrepo.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.qa.perf_repo.web.ControllerBase;
import org.jboss.qa.perfrepo.model.TestExecution;
import org.jboss.qa.perfrepo.model.TestExecutionSearchTO;
import org.jboss.qa.perfrepo.service.TestService;
import org.jboss.qa.perfrepo.viewscope.ViewScoped;

@Named
@ViewScoped
public class SearchController extends ControllerBase {

   private static final long serialVersionUID = 1L;
   
   @Inject
   private TestService testService;
   
   private String tag;
   
   private TestExecutionSearchTO bean = null;
   
   private List<TestExecution> result;
  
   @PostConstruct
   public void init() {
      if (bean == null) {
         bean = new TestExecutionSearchTO();
      }
      if (result == null) {
         result= new ArrayList<TestExecution>();
      }
   }
   
   public TestExecutionSearchTO getBean() {      
      return bean;
   }
   
   
   public String getTag() {
      return tag;
   }


   public void setTag(String tag) {
      this.tag = tag;
   }


   public String search() {
      result = testService.searchTestExecutions(bean);
      return null;
   }


   public List<TestExecution> getResult() {
      return result;
   }


   public void setResult(List<TestExecution> result) {
      this.result = result;
   }
   
}