package org.jboss.qa.perfrepo.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Named;

import org.jboss.qa.perfrepo.model.Metric;

@Named
@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class MetricDAO extends DAO<Metric, Long> {

   private static final long serialVersionUID = 1L;
   
   
   public Metric findByName(String name) {
      List<Metric> metrics = findAllByProperty("name", name);
      if (metrics.size() > 0)
         return metrics.get(0);
      return null;
   }
   
   public List<Metric> getMetrics() {
      return findAll();
   }
      
}