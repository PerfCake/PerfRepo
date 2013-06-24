package org.jboss.qa.perfrepo.dao;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
   
   /**
    * Returns all metrics by name prefix, which belong tests with defined group id
    * @param namePart
    * @param guid
    * @return
    */
   public List<Metric> getMetricByNameAndGroup(String name, String groupId) {
      Map<String, Object> params = new TreeMap<>();
      params.put("groupId", groupId);
      params.put("name", name);
      return findByNamedQuery(Metric.FIND_BY_NAME_GROUPID, params);
   }
   
   /**
    * Returns all metrics which belong tests with defined group id
    * @param namePart
    * @param guid
    * @return
    */
   public List<Metric> getMetricByGroup(String groupId) {
      Map<String, Object> params = new TreeMap<>();
      params.put("groupId", groupId);      
      return findByNamedQuery(Metric.FIND_BY_GROUPID, params);
   }

}