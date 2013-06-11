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

import org.jboss.qa.perfrepo.dao.TestMetricDAO;
import org.jboss.qa.perfrepo.model.TestMetric;

@Named
@RequestScoped
public class TestMetricController extends ControllerBase {

   @Inject
   private TestMetricDAO dao;

   @Inject
   Conversation conversation;

   private TestMetric bean = null;

   private List<TestMetric> beanList = null;

   private SortFilterDataModel<TestMetric> beanDataModel = null;

   public TestMetric getBean() {
      String id;
      if (bean == null) {
         if ((id = getRequestParam("testMetricId")) != null) {
            bean = dao.get(Long.valueOf(id));
         } else {
            bean = new TestMetric();
         }
      }
      return bean;
   }

   public List<TestMetric> getBeanList() {
      if (beanList == null) {
         Map<String, Object> queryParams = new HashMap<String, Object>();
         StringBuffer querySB = new StringBuffer();
         boolean whereAppended = false;
         querySB.append("SELECT x FROM TestMetric x");
         String metricIdN_1 = getRequestParam("metricId");
         if (metricIdN_1 != null) {
            querySB.append(" " + (whereAppended ? "AND" : "WHERE") + " x.metric.id = :metricId");
            if (!whereAppended) {
               whereAppended = true;
            }
            queryParams.put("metricId", Integer.valueOf(metricIdN_1));
         }
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

   public DataModel<TestMetric> getTableData() {
      if (beanDataModel == null) {
         beanDataModel = new SortFilterDataModel<TestMetric>(getBeanList());
      }
      return beanDataModel;
   }

   /*
    * If generated bean does not have an attribute called 'name' it is likely that the select list
    * component wouldn't be used in web application so this method can be deleted.
    */
   public List<SelectItem> getBeanSelectItems() {
      List<SelectItem> list = new ArrayList<SelectItem>();
      List<TestMetric> beanList = getBeanList();
      for (TestMetric bean : beanList) {
         list.add(new SelectItem(bean.getId(), bean.getStringId()));
      }
      return list;
   }

   public String update() {
      if (bean != null) {
         dao.update(bean);
      }
      // on successfuly updated go back to the entity list
      return "TestMetricList";
   }

   public String create() {
      if (bean != null) {
         dao.create(bean);
      }
      // on successfuly created go back to the entity list
      // return "TestMetricList" + Constants.REDIRECT_URL;
      return "TestMetricList";
   }

   public String delete() {
      if (bean != null) {
         dao.delete(bean);
      }

      // on successfuly created go back to the entity list
      return "TestMetricList";
   }

}