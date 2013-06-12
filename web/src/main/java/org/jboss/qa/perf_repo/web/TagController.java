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

import org.jboss.qa.perfrepo.dao.TagDAO;
import org.jboss.qa.perfrepo.model.Tag;

@Named
@RequestScoped
public class TagController extends ControllerBase {

   private static final long serialVersionUID = -8109085992506268845L;

   @Inject
   private TagDAO dao;

   @Inject
   Conversation conversation;

   private Tag bean = null;

   private List<Tag> beanList = null;

   private SortFilterDataModel<Tag> beanDataModel = null;

   public Tag getBean() {
      String id;
      if (bean == null) {
         if ((id = getRequestParam("tagId")) != null) {
            bean = dao.get(Long.valueOf(id));
         } else {
            bean = new Tag();
         }
      }
      return bean;
   }

   public List<Tag> getBeanList() {
      if (beanList == null) {
         Map<String, Object> queryParams = new HashMap<String, Object>();
         StringBuffer querySB = new StringBuffer();
         querySB.append("SELECT x FROM Tag x");

         beanList = dao.findByQuery(querySB.toString(), queryParams);
      }
      return beanList;
   }

   public DataModel<Tag> getTableData() {
      if (beanDataModel == null) {
         beanDataModel = new SortFilterDataModel<Tag>(getBeanList());
      }
      return beanDataModel;
   }

   /*
    * If generated bean does not have an attribute called 'name' it is likely that the select list
    * component wouldn't be used in web application so this method can be deleted.
    */
   public List<SelectItem> getBeanSelectItems() {
      List<SelectItem> list = new ArrayList<SelectItem>();
      List<Tag> beanList = getBeanList();
      for (Tag bean : beanList) {
         list.add(new SelectItem(bean.getId(), bean.getName()));
      }
      return list;
   }

   public String update() {
      if (bean != null) {
         dao.update(bean);
      }
      // on successfuly updated go back to the entity list
      return "TagList";
   }

   public String create() {
      if (bean != null) {
         dao.create(bean);
      }
      // on successfuly created go back to the entity list
      // return "TagList" + Constants.REDIRECT_URL;
      return "TagList";
   }

   public String delete() {
      if (bean != null) {
         dao.delete(bean);
      }

      // on successfuly created go back to the entity list
      return "TagList";
   }

}