package org.jboss.qa.perf_repo.web;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.qa.perfrepo.model.Test;
import org.jboss.qa.perfrepo.service.ServiceException;
import org.jboss.qa.perfrepo.service.TestService;

@Named
@RequestScoped
public class TestController extends ControllerBase {

   private static final long serialVersionUID = 370202307562230671L;

   @Inject
   TestService testService;

   private Test test = null;

   private List<Test> testList = null;

   public Test getTest() {
      String id;
      if (test == null) {
         if ((id = getRequestParam("testId")) != null) {
            test = testService.getTest(Long.valueOf(id));
         } else {
            test = new Test();
         }
      }
      return test;
   }

   @Deprecated
   public List<Test> getTestList() {
      if (testList == null) {
         testList = testService.findAllTests();
      }
      return testList;
   }

   public String update() {
      if (test != null) {
         testService.updateTest(test);
      }
      return "TestList";
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
