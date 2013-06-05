package org.jboss.qa.perfrepo.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Named;

import org.jboss.qa.perfrepo.model.Tag;

@Named
@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class TagDAO extends DAO<Tag, Long> {

   private static final long serialVersionUID = 1L;  
   
   public Tag findByName(String name) {
      List<Tag> tags = findAllByProperty("name", name);
      if (tags.size() > 0) {
         return tags.get(0);
      }
      return null;
   }
}