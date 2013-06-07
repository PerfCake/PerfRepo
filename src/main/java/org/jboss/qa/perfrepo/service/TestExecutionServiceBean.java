package org.jboss.qa.perfrepo.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.qa.perfrepo.dao.MetricDAO;
import org.jboss.qa.perfrepo.dao.TagDAO;
import org.jboss.qa.perfrepo.dao.TestExecutionDAO;
import org.jboss.qa.perfrepo.dao.TestExecutionParameterDAO;
import org.jboss.qa.perfrepo.dao.TestExecutionTagDAO;
import org.jboss.qa.perfrepo.dao.ValueDAO;
import org.jboss.qa.perfrepo.dao.ValueParameterDAO;
import org.jboss.qa.perfrepo.model.Metric;
import org.jboss.qa.perfrepo.model.Tag;
import org.jboss.qa.perfrepo.model.Test;
import org.jboss.qa.perfrepo.model.TestExecution;
import org.jboss.qa.perfrepo.model.TestExecutionParameter;
import org.jboss.qa.perfrepo.model.TestExecutionSearchTO;
import org.jboss.qa.perfrepo.model.TestExecutionTag;
import org.jboss.qa.perfrepo.model.Value;
import org.jboss.qa.perfrepo.model.ValueParameter;
import org.jboss.qa.perfrepo.security.Secure;

@Named
@Stateless
public class TestExecutionServiceBean implements TestExecutionService {

   @Inject
   TestExecutionDAO testExecutionDAO;

   @Inject
   TestExecutionParameterDAO testExecutionParameterDAO;

   @Inject
   TagDAO tagDAO;
   
   @Inject
   TestExecutionTagDAO testExecutionTagDAO;
   
   @Inject
   ValueDAO valueDAO;
   
   @Inject
   ValueParameterDAO valueParameterDAO;
   
   @Inject
   TestService testService;
   
   @Inject
   MetricDAO metricDAO;
   
   @Override   
   @Secure
   public TestExecution storeTestExecution(TestExecution te) {      
      //test
      Test test = testService.getOrCreateTest(te.getTest());      
      te.setTest(test);
      TestExecution storedTestExecution = testExecutionDAO.create(te);
      // execution params
      if (te.getTestExecutionParameters() != null && te.getTestExecutionParameters().size() > 0) {
         for (TestExecutionParameter param : te.getTestExecutionParameters()) {
            param.setTestExecution(storedTestExecution);
            testExecutionParameterDAO.create(param);
         }
      }
      // tags
      if (te.getTestExecutionTags() != null && te.getTestExecutionTags().size() > 0) {
         for (TestExecutionTag teg : te.getTestExecutionTags()) {
            Tag tag = tagDAO.findByName(teg.getTag().getName());
            if (tag == null) {
               tag = tagDAO.create(teg.getTag());
            }
            teg.setTag(tag);
            teg.setTestExecution(storedTestExecution);
            testExecutionTagDAO.create(teg);
         }
      }
      // values
      if (te.getValues() != null && te.getValues().size() > 0) {
         for (Value value : te.getValues()) {
            value.setTestExecution(storedTestExecution);
            
            Metric metric = metricDAO.findByName(value.getMetric().getName());
            if (metric == null) {
               metric = metricDAO.create(value.getMetric());
            }
            value.setMetric(metric);            
            valueDAO.create(value);
            if (value.getValueParameters() != null && value.getValueParameters().size() > 0) {
               for (ValueParameter vp : value.getValueParameters()) {
                  vp.setValue(value);
                  valueParameterDAO.create(vp);
               }
            }
         }
      }
      return storedTestExecution;
   }
   
   public List<TestExecution> getTestExecutions(Collection<Long> ids) {
      List<TestExecution> result = new ArrayList<TestExecution>();
      for (Long id : ids) {
         TestExecution testExecution = testExecutionDAO.getFullTestExecution(id);
         result.add(testExecution);
      }
      return result;      
   }
   
   
   public List<TestExecution> searchTestExecutions(TestExecutionSearchTO search) {
      List<TestExecution> result = testExecutionDAO.searchTestExecutions(search);      
      return result;
   }

}
