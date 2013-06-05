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

import org.jboss.qa.perfrepo.dao.TestExecutionParameterDAO;
import org.jboss.qa.perfrepo.model.TestExecutionParameter;

@Named
@RequestScoped
public class TestExecutionParameterController implements Serializable {

   private static final long serialVersionUID = 1L;

   @Inject
   private TestExecutionParameterDAO dao;

   @Inject
   Conversation conversation;

   private TestExecutionParameter bean = null;

   private List<TestExecutionParameter> beanList = null;

   private SortFilterDataModel<TestExecutionParameter> beanDataModel = null;

   public TestExecutionParameter getBean() {
      String id;
      if (bean == null) {
         if ((id = getRequestParam("testExecutionParameterId")) != null) {
            bean = dao.get(Long.valueOf(id));
         } else {
            bean = new TestExecutionParameter();
         }
      }
      return bean;
   }

   public List<TestExecutionParameter> getBeanList() {
      if (beanList == null) {
         Map<String, Object> queryParams = new HashMap<String, Object>();
         StringBuffer querySB = new StringBuffer();
         boolean whereAppended = false;
         querySB.append("SELECT x FROM TestExecutionParameter x");
         String testExecutionIdN_1 = getRequestParam("testExecutionId");
if (testExecutionIdN_1 != null) {
querySB.append(" "+(whereAppended?"AND":"WHERE")+" x.testExecution.id = :testExecutionId");
if(!whereAppended){
whereAppended = true;
}
queryParams.put("testExecutionId", Long.valueOf(testExecutionIdN_1));
}

         beanList = dao.findByQuery(querySB.toString(), queryParams);
      }
      return beanList;
   }

   public DataModel<TestExecutionParameter> getTableData() {
      if (beanDataModel == null) {
         beanDataModel = new SortFilterDataModel<TestExecutionParameter>(getBeanList());
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
      List<TestExecutionParameter> beanList = getBeanList();
      for (TestExecutionParameter bean : beanList) {
         list.add(new SelectItem(bean.getId(), bean.getName()));
      }
      return list;
   }

   public String update() {
      if (bean != null) {
         dao.update(bean);
      }
      // on successfuly updated go back to the entity list
      return "TestExecutionParameterList";
   }

   public String create() {
      if (bean != null) {
         dao.create(bean);
      }
      // on successfuly created go back to the entity list
      // return "TestExecutionParameterList" + Constants.REDIRECT_URL;
      return "TestExecutionParameterList";
   }

   public String delete() {
      if (bean != null) {
         dao.delete(bean);
      }

      // on successfuly created go back to the entity list
      return "TestExecutionParameterList";
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