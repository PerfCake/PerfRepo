package org.jboss.qa.perf_repo.web;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.qa.perfrepo.model.Metric;
import org.jboss.qa.perfrepo.model.Test;
import org.jboss.qa.perfrepo.service.ServiceException;
import org.jboss.qa.perfrepo.service.TestService;
import org.jboss.qa.perfrepo.viewscope.ViewScoped;

@Named
@ViewScoped
public class TestController extends ControllerBase {

   private static final long serialVersionUID = 370202307562230671L;

   @Inject
   TestService testService;

   private Test test = null;
   
   private Metric metric = null;
   
   @PostConstruct
   public void init() {
      String id;
      if (test == null) {
         if ((id = getRequestParam("testId")) != null) {
            test = testService.getTest(Long.valueOf(id));
         } else {
            test = new Test();
         }
      }
   }

   public Test getTest() {      
      return test;
   }
      
   public Metric getMetric() {
      return metric;
   }

   public void setMetric(Metric metric) {
      this.metric = metric;
   }

   public String update() {
      if (test != null) {
         testService.updateTest(test);
      }
      return "TestDetail";
   }
   
   public void createMetric() {
      metric = new Metric();
   }
   
   public void updateMetric() {
      if (metric != null) {
         testService.updateMetric(metric);
      }
   }
   
   public void addMetric() {
      if (metric != null && test != null) {
         testService.addMetric(test, metric);
         //TODO:add to collection
      }
   }
   
   public void deleteMetric() {
      if (metric != null && test!= null) {
         testService.deleteTestMetric(test, metric);
      }
   }

   public String create() {
      if (test != null) {
         testService.createTest(test);
      }
      return "TestList";
   }

   public String delete() {
      Test testToDelete = test;
      if (test == null) {
         testToDelete = new Test();
         testToDelete.setId(new Long(getRequestParam("testId")));
      }
      try {
         testService.deleteTest(testToDelete);
      } catch (ServiceException e) {
         // TODO: how to handle exceptions in web layer?
         throw new RuntimeException(e);
      }
      return "TestList";
   }

}
