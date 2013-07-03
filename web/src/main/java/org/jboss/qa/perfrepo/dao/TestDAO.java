/* 
 * Copyright 2013 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.qa.perfrepo.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.log4j.Logger;
import org.jboss.qa.perfrepo.model.Test;
import org.jboss.qa.perfrepo.model.to.TestSearchTO;

/**
 * DAO for {@link Test}
 * 
 * @author Pavel Drozd (pdrozd@redhat.com)
 * @author Michal Linhard (mlinhard@redhat.com)
 * 
 */
@Named
public class TestDAO extends DAO<Test, Long> {

   private static final Logger log = Logger.getLogger(TestDAO.class);

   private static final String FIND_TEST_ID = "FIND_TEST_ID";

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

   public Test findTestForEntity(Object entity) {
      try {
         Field queryName = entity.getClass().getDeclaredField(FIND_TEST_ID);
         if (queryName != null) {
            String query = (String) queryName.get(String.class);
            Query q = createNamedQuery(query);
            q.setParameter("entity", entity);
            return ((Test) q.getSingleResult());
         }
      } catch (IllegalAccessException e) {
         log.error("Problem with reflection", e);
      } catch (NoSuchFieldException e) {
         log.error("Problem with reflection", e);
      } catch (SecurityException e) {
         log.error("Problem with reflection", e);
      }
      return null;
   }
}