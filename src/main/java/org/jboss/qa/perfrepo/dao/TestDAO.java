package org.jboss.qa.perfrepo.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Named;

import org.jboss.qa.perfrepo.model.Test;

@Named
@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class TestDAO extends DAO<Test, Long> {

   private static final long serialVersionUID = 1L;  
   
   
   public Test findByUid(String uid) {
      List<Test> tests = findAllByProperty("uid", uid);
      if (tests.size() > 0)
         return tests.get(0);
      return null;
   }
}