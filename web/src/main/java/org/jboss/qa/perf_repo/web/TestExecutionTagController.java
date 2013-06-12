package org.jboss.qa.perf_repo.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.Conversation;
import javax.enterprise.context.RequestScoped;
import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.qa.perfrepo.dao.TestExecutionTagDAO;
import org.jboss.qa.perfrepo.model.TestExecutionTag;

@Named
@RequestScoped
public class TestExecutionTagController extends ControllerBase {

   private static final long serialVersionUID = 1102784150207588938L;

   @Inject
   private TestExecutionTagDAO dao;

   @Inject
   Conversation conversation;

   private TestExecutionTag bean = null;

   private List<TestExecutionTag> beanList = null;

   private SortFilterDataModel<TestExecutionTag> beanDataModel = null;

   public TestExecutionTag getBean() {
      String id;
      if (bean == null) {
         if ((id = getRequestParam("testExecutionTagId")) != null) {
            bean = dao.get(Long.valueOf(id));
         } else {
            bean = new TestExecutionTag();
         }
      }
      return bean;
   }

   public List<TestExecutionTag> getBeanList() {
      if (beanList == null) {
         Map<String, Object> queryParams = new HashMap<String, Object>();
         StringBuffer querySB = new StringBuffer();
         boolean whereAppended = false;
         querySB.append("SELECT x FROM TestExecutionTag x");
         String tagIdN_1 = getRequestParam("tagId");
         if (tagIdN_1 != null) {
            querySB.append(" " + (whereAppended ? "AND" : "WHERE") + " x.tag.id = :tagId");
            if (!whereAppended) {
               whereAppended = true;
            }
            queryParams.put("tagId", Long.valueOf(tagIdN_1));
         }
         String testExecutionIdN_1 = getRequestParam("testExecutionId");
         if (testExecutionIdN_1 != null) {
            querySB.append(" " + (whereAppended ? "AND" : "WHERE") + " x.testExecution.id = :testExecutionId");
            if (!whereAppended) {
               whereAppended = true;
            }
            queryParams.put("testExecutionId", Long.valueOf(testExecutionIdN_1));
         }

         beanList = dao.findByQuery(querySB.toString(), queryParams);
      }
      return beanList;
   }

   public DataModel<TestExecutionTag> getTableData() {
      if (beanDataModel == null) {
         beanDataModel = new SortFilterDataModel<TestExecutionTag>(getBeanList());
      }
      return beanDataModel;
   }

   /*
    * If generated bean does not have an attribute called 'name' it is likely that the select list
    * component wouldn't be used in web application so this method can be deleted.
    */
   public List<SelectItem> getBeanSelectItems() {
      List<SelectItem> list = new ArrayList<SelectItem>();
      List<TestExecutionTag> beanList = getBeanList();
      for (TestExecutionTag bean : beanList) {
         list.add(new SelectItem(bean.getId(), bean.getStringId()));
      }
      return list;
   }

   public String update() {
      if (bean != null) {
         dao.update(bean);
      }
      // on successfuly updated go back to the entity list
      return "TestExecutionTagList";
   }

   public String create() {
      if (bean != null) {
         dao.create(bean);
      }
      // on successfuly created go back to the entity list
      // return "TestExecutionTagList" + Constants.REDIRECT_URL;
      return "TestExecutionTagList";
   }

   public String delete() {
      if (bean != null) {
         dao.delete(bean);
      }

      // on successfuly created go back to the entity list
      return "TestExecutionTagList";
   }

}