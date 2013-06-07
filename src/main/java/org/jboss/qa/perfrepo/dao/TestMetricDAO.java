package org.jboss.qa.perfrepo.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Named;

import org.jboss.qa.perfrepo.model.Test;
import org.jboss.qa.perfrepo.model.TestMetric;

@Named
@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class TestMetricDAO extends DAO<TestMetric, Long> {

   private static final long serialVersionUID = 1L;
   

   public List<TestMetric> findByTest(Test test) {
      return findAllByProperty("test", test);
   }
   
}