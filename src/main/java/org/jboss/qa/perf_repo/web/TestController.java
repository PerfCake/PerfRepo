package org.jboss.qa.perf_repo.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.qa.perfrepo.model.Test;
import org.jboss.qa.perfrepo.service.TestService;

@Named
@RequestScoped
public class TestController implements Serializable {

   private static final long serialVersionUID = 1L;

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
          testList = testService.getAllTests();
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
         testService.storeTest(test);
      }
      return "TestList";
   }

   public String delete() {
      if (test != null) {
         testService.deleteTest(test);
      }
      return "TestList";
   }

   public Map<String, String> getRequestParams() {
      Map<String, String> map = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
      return map;
   }

   public String getRequestParam(String name) {
      return getRequestParams().get(name);
   }

   public String getRequestParam(String name, String _default) {
      String ret = getRequestParam(name);
      if (ret == null) {
         return _default;
      } else {
         return ret;
      }
   }
}