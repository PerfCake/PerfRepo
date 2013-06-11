package org.jboss.qa.perfrepo.dao;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.ejb.Stateless;
import javax.inject.Named;

import org.jboss.qa.perfrepo.model.Metric;
import org.jboss.qa.perfrepo.model.Test;
import org.jboss.qa.perfrepo.model.TestMetric;

/**
 * DAO for {@link TestMetric}.
 * 
 * @author Pavel Drozd (pdrozd@redhat.com)
 * @author Michal Linhard (mlinhard@redhat.com)
 */
@Named
@Stateless
public class TestMetricDAO extends DAO<TestMetric, Long> {

   /**
    * Find all metrics measured by given test
    * 
    * @param test
    * @return collection of metrics
    */
   public List<TestMetric> findByTest(Test test) {
      return findAllByProperty("test", test);
   }

   /**
    * Find all tests using to given metric.
    * 
    * @param metric
    * @return collection of tests
    */
   public List<TestMetric> findByMetric(Metric metric) {
      return findAllByProperty("metric", metric);
   }

   /**
    * Find {@link TestMetric} intermediate table object for given test and metric.
    * 
    * @param test
    * @param metric
    * @return
    */
   public TestMetric find(Test test, Metric metric) {
      Map<String, Object> params = new TreeMap<>();
      params.put("test", test.getId());
      params.put("metric", metric.getId());
      List<TestMetric> list = findByNamedQuery(TestMetric.FIND_TEST_METRIC, params);
      if (list == null || list.isEmpty()) {
         return null;
      } else if (list.size() == 1) {
         return list.get(0);
      } else {
         throw new IllegalStateException("Can't have two test_metric rows with same (test_id, metric_id)");
      }
   }
}