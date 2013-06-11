package org.jboss.qa.perf_repo.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.Conversation;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.qa.perfrepo.dao.TestExecutionDAO;
import org.jboss.qa.perfrepo.model.TestExecution;
import org.jboss.qa.perfrepo.service.ServiceException;
import org.jboss.qa.perfrepo.service.TestService;

@Named
@RequestScoped
public class TestExecutionController implements Serializable {

   private static final long serialVersionUID = 1L;

   @Inject
   private TestExecutionDAO dao;

   @Inject
   private TestService service;

   @Inject
   Conversation conversation;

   private TestExecution bean = null;

   private List<TestExecution> beanList = null;

   private SortFilterDataModel<TestExecution> beanDataModel = null;

   public TestExecution getBean() {
      String id;
      if (bean == null) {
         if ((id = getRequestParam("testExecutionId")) != null) {
            bean = dao.get(Long.valueOf(id));
         } else {
            bean = new TestExecution();
         }
      }
      return bean;
   }

   public List<TestExecution> getBeanList() {
      if (beanList == null) {
         Map<String, Object> queryParams = new HashMap<String, Object>();
         StringBuffer querySB = new StringBuffer();
         boolean whereAppended = false;
         querySB.append("SELECT x FROM TestExecution x");
         String testIdN_1 = getRequestParam("testId");
         if (testIdN_1 != null) {
            querySB.append(" " + (whereAppended ? "AND" : "WHERE") + " x.test.id = :testId");
            if (!whereAppended) {
               whereAppended = true;
            }
            queryParams.put("testId", Long.valueOf(testIdN_1));
         }

         beanList = dao.findByQuery(querySB.toString(), queryParams);
      }
      return beanList;
   }

   public DataModel<TestExecution> getTableData() {
      if (beanDataModel == null) {
         beanDataModel = new SortFilterDataModel<TestExecution>(getBeanList());
      }
      return beanDataModel;
   }

   /*
    * If generated bean does not have an attribute called 'name' it is likely
    * that the select list component wouldn't be used in web application so this
    * method can be deleted.
    */
   public List<SelectItem> getBeanSelectItems() {
      List<SelectItem> list = new ArrayList<SelectItem>();
      List<TestExecution> beanList = getBeanList();
      for (TestExecution bean : beanList) {
         list.add(new SelectItem(bean.getId(), bean.getName()));
      }
      return list;
   }

   public String update() {
      if (bean != null) {
         try {
            service.updateTestExecution(bean);
         } catch (ServiceException e) {
            // TODO: how to handle exceptions in web layer ?
            throw new RuntimeException(e);
         }
      }
      // on successfuly updated go back to the entity list
      return "TestExecutionList";
   }

   public String create() {
      if (bean != null) {
         dao.create(bean);
      }
      // on successfuly created go back to the entity list
      // return "TestExecutionList" + Constants.REDIRECT_URL;
      return "TestExecutionList";
   }

   public String delete() {
      if (bean != null) {
         dao.delete(bean);
      }

      // on successfuly created go back to the entity list
      return "TestExecutionList";
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