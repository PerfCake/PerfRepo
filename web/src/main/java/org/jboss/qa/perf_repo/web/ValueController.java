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

import org.jboss.qa.perfrepo.dao.ValueDAO;
import org.jboss.qa.perfrepo.model.Value;

@Named
@RequestScoped
public class ValueController implements Serializable {

   private static final long serialVersionUID = 1L;

   @Inject
   private ValueDAO dao;

   @Inject
   Conversation conversation;

   private Value bean = null;

   private List<Value> beanList = null;

   private SortFilterDataModel<Value> beanDataModel = null;

   public Value getBean() {
      String id;
      if (bean == null) {
         if ((id = getRequestParam("valueId")) != null) {
            bean = dao.get(Long.valueOf(id));
         } else {
            bean = new Value();
         }
      }
      return bean;
   }

   public List<Value> getBeanList() {
      if (beanList == null) {
         Map<String, Object> queryParams = new HashMap<String, Object>();
         StringBuffer querySB = new StringBuffer();
         boolean whereAppended = false;
         querySB.append("SELECT x FROM Value x");
         String metricIdN_1 = getRequestParam("metricId");
if (metricIdN_1 != null) {
querySB.append(" "+(whereAppended?"AND":"WHERE")+" x.metric.id = :metricId");
if(!whereAppended){
whereAppended = true;
}
queryParams.put("metricId", Long.valueOf(metricIdN_1));
}
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

   public DataModel<Value> getTableData() {
      if (beanDataModel == null) {
         beanDataModel = new SortFilterDataModel<Value>(getBeanList());
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
      List<Value> beanList = getBeanList();
      for (Value bean : beanList) {
         list.add(new SelectItem(bean.getId(), bean.getName()));
      }
      return list;
   }

   public String update() {
      if (bean != null) {
         dao.update(bean);
      }
      // on successfuly updated go back to the entity list
      return "ValueList";
   }

   public String create() {
      if (bean != null) {
         dao.create(bean);
      }
      // on successfuly created go back to the entity list
      // return "ValueList" + Constants.REDIRECT_URL;
      return "ValueList";
   }

   public String delete() {
      if (bean != null) {
         dao.delete(bean);
      }

      // on successfuly created go back to the entity list
      return "ValueList";
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