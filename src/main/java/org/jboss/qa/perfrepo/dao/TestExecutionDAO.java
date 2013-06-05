package org.jboss.qa.perfrepo.dao;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Named;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import org.jboss.qa.perfrepo.model.Tag;
import org.jboss.qa.perfrepo.model.Test;
import org.jboss.qa.perfrepo.model.TestExecution;
import org.jboss.qa.perfrepo.model.TestExecutionSearchTO;
import org.jboss.qa.perfrepo.model.TestExecutionTag;
import org.jboss.qa.perfrepo.model.Value;

@Named
@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class TestExecutionDAO extends DAO<TestExecution, Long> {

   private static final long serialVersionUID = 1L;
   
   public List<TestExecution> findByTest(Long testId) {
      Test test = new Test();
      test.setId(testId);
      return findAllByProperty("test", test);
   }
   
   
   public List<TestExecution> searchTestExecutions(TestExecutionSearchTO search) {
      CriteriaQuery<TestExecution> criteria = createCriteria();
      Root<TestExecution> root = criteria.from(TestExecution.class);
      criteria.select(root);
      CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
      
      if (search.getStartedFrom() != null) {
         criteria.where(cb.greaterThanOrEqualTo(root.<Date>get("started"), search.getStartedFrom()));
      }
      if (search.getStartedTo() != null) {
         criteria.where(cb.lessThanOrEqualTo(root.<Date>get("started"), search.getStartedTo()));
      }
      if (search.getTags() != null && !"".equals(search.getTags())) {
         Join<TestExecution, TestExecutionTag> tegRoot = root.join("testExecutionTags");
         Join<TestExecutionTag, Tag> tagRoot = tegRoot.join("tag");
         String[] tags = search.getTags().split(";");
         criteria.where((tagRoot.get("name").in(tags)));         
         criteria.having(cb.greaterThanOrEqualTo(cb.count(tagRoot), Long.valueOf(tags.length)));
      }
      if (search.getTestName() != null && !"".equals(search.getTestName())) {
         Join<TestExecution, Test> testRoot = root.join("test");
         criteria.where(cb.equal(testRoot.get("name"), search.getTestName()));
      }
      if (search.getTestUID() != null && !"".equals(search.getTestUID())) {
         Join<TestExecution, Test> testRoot = root.join("test");
         criteria.where(cb.equal(testRoot.get("uid"), search.getTestUID()));
      }
      criteria.groupBy(root.get("id"));      
      return findByCustomCriteria(criteria);
   }
   
   public TestExecution getFullTestExecution(Long id) {
      
      return findWithDepth(id, "testExecutionParameters", "values.valueParameters", "testExecutionTags.tag");
//      CriteriaQuery<TestExecution> criteria = createCriteria();
//      Root<TestExecution> root = criteria.from(TestExecution.class);
//      
//      CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
//      //root.join("test");
//      Join<TestExecution, TestExecutionTag> tegRoot = root.joinCollection("testExecutionTags", JoinType.LEFT);      
//      Fetch<TestExecution, Value> valuesRoot = root.fetch("values");      
//      tegRoot.join("tag");      
//      //root.fetch("testExecutionParameters", JoinType.LEFT);
//      valuesRoot.fetch("valueParameters", JoinType.LEFT);
//      criteria.select(root);
//    
//      criteria.where(cb.equal(root.get("id"), id));                  
//      return findByCustomCriteriaSingle(criteria);
      
      
   }
   
}