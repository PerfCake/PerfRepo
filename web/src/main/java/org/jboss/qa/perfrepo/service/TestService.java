package org.jboss.qa.perfrepo.service;

import java.util.List;

import org.jboss.qa.perfrepo.model.Metric;
import org.jboss.qa.perfrepo.model.Test;

public interface TestService {
   
   public Test getTest(Long id);
   
   public Test storeTest(Test test);   
   
   public void deleteTest(Test test);
   
   public Test updateTest(Test test);
   
   public List<Test> getAllTests();
   
   public Test getOrCreateTest(Test test);
   
   public Metric getMetric(Long id);
   
   public Metric storeMetric(Metric metric); 
   
   public List<Metric> getAllMetrics();
   
   public void deleteMetric(Metric metric);

}
