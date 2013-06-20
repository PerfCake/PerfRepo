package org.jboss.qa.perfrepo.dao;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.jboss.qa.perfrepo.model.Test;
import org.jboss.qa.perfrepo.model.to.TestSearchTO;

@Named
public class TestDAO extends DAO<Test, Long> {

   public Test findByUid(String uid) {
      List<Test> tests = findAllByProperty("uid", uid);
      if (tests.size() > 0)
         return tests.get(0);
      return null;
   }
   
   public List<Test> searchTests(TestSearchTO search) {
      CriteriaQuery<Test> criteria = createCriteria();
      Root<Test> root = criteria.from(Test.class);
      criteria.select(root);
      CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
      List<Predicate> predicates = new ArrayList<Predicate>();
      if (search.getName() != null && !"".equals(search.getName())) {
         predicates.add(cb.equal(root.get("name"), search.getName()));
      }
      if (search.getUid() != null && !"".equals(search.getUid())) {
         predicates.add(cb.equal(root.get("uid"), search.getUid()));
      }
      if (search.getGroupId() != null && !"".equals(search.getGroupId())) {
         predicates.add(cb.equal(root.get("groupId"), search.getGroupId()));
      }
      if (predicates.size() > 0) {
         criteria.where(predicates.toArray(new Predicate[0]));
      }
      return findByCustomCriteria(criteria);
   }
   
}