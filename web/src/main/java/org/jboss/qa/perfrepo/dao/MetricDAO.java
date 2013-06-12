package org.jboss.qa.perfrepo.dao;

import java.util.List;

import javax.inject.Named;

import org.jboss.qa.perfrepo.model.Metric;

@Named
public class MetricDAO extends DAO<Metric, Long> {

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