package org.jboss.qa.perfrepo.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Conversation;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.qa.perfrepo.model.TestExecution;
import org.jboss.qa.perfrepo.model.TestExecutionSearchTO;
import org.jboss.qa.perfrepo.service.TestExecutionService;
import org.jboss.qa.perfrepo.viewscope.ViewScoped;

@Named
@ViewScoped
public class SearchController implements Serializable {

   private static final long serialVersionUID = 1L;
   
   @Inject
   private TestExecutionService testExecutionService;
   
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
   
   public String addTag() {  
      //getBean().getTags().add(tag);  
      tag = "";
      return null;
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
      result = testExecutionService.searchTestExecutions(bean);
      return null;
   }


   public List<TestExecution> getResult() {
      return result;
   }


   public void setResult(List<TestExecution> result) {
      this.result = result;
   }
   
}