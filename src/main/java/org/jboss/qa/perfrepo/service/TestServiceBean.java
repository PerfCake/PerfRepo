package org.jboss.qa.perfrepo.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.qa.perfrepo.dao.MetricDAO;
import org.jboss.qa.perfrepo.dao.TestDAO;
import org.jboss.qa.perfrepo.dao.TestMetricDAO;
import org.jboss.qa.perfrepo.model.Metric;
import org.jboss.qa.perfrepo.model.Test;
import org.jboss.qa.perfrepo.model.TestMetric;
import org.jboss.qa.perfrepo.security.Secure;

@Named
@Stateless
public class TestServiceBean implements TestService {
   
   @Inject
   TestDAO testDAO;
   
   @Inject
   MetricDAO metricDAO;
   
   @Inject
   TestMetricDAO testMetricDAO;

   @Override
   public Test storeTest(Test test) {   
      //TODO: set guid
      Test createdTest = testDAO.create(test);      
      //store metrics
      if (test.getTestMetrics() != null && test.getTestMetrics().size() > 0) {
         for (TestMetric tm : test.getTestMetrics()) {
            Metric metric = storeMetric(tm.getMetric());            
            tm.setMetric(metric);
            tm.setTest(createdTest);
            testMetricDAO.create(tm);            
         }
      }
      return createdTest;     
   }
   
   public Test getOrCreateTest(Test test) {
      Test storedTest = testDAO.findByUid(test.getUid());
      if (storedTest == null) {
         storedTest = storeTest(test);
      }
      return storedTest;
   }
   
   public Test getTest(Long id) {
      return testDAO.get(id);
   }
   
   public List<Test> getAllTests() {
      return testDAO.findAll();
   }
   
   @Secure
   public Test updateTest(Test test) {
      return testDAO.update(test);
   }
   
   @Secure
   public void deleteTest(Test test) {
      Test t = testDAO.get(test.getId());
      //TODO: delete test executions
      testDAO.delete(t);
   }   
   
   public Metric getMetric(Long id) {
      return metricDAO.get(id);
   }
   
   public Metric storeMetric(Metric metric) {
      Metric m = metricDAO.findByName(metric.getName());
      if (m == null) {
         m = metricDAO.create(metric);
      } 
      return m;
   }   
   
   @Secure
   public Metric updateMetric(Metric metric) {
      return metricDAO.update(metric);
   }
  
   public List<Metric> getAllMetrics() {
      return metricDAO.getMetrics();
   }
   
   @Secure
   public void deleteMetric(Metric metric) {
      Metric m  = metricDAO.get(metric.getId());
      metricDAO.delete(m);
   }

}
