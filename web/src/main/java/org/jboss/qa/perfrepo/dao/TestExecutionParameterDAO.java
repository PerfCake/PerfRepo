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

import java.util.List;

import javax.inject.Named;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.jboss.qa.perfrepo.model.Test;
import org.jboss.qa.perfrepo.model.TestExecutionParameter;

/**
 * DAO for {@link TestExecutionParameter}
 * 
 * @author Pavel Drozd (pdrozd@redhat.com)
 * @author Michal Linhard (mlinhard@redhat.com)
 * 
 */
@Named
public class TestExecutionParameterDAO extends DAO<TestExecutionParameter, Long> {

   public List<String> getAllSelectionExecutionParams(Long testId) {
      TypedQuery<String> q = createNamedQuery(TestExecutionParameter.FIND_BY_TEST_ID, String.class);
      q.setParameter("testId", testId);
      return q.getResultList();
   }

   public boolean hasTestParam(Long testId, String paramName) {
      CriteriaBuilder cb = criteriaBuilder();
      CriteriaQuery<Long> criteria = cb.createQuery(Long.class);

      Root<TestExecutionParameter> rParam = criteria.from(TestExecutionParameter.class);
      Join<TestExecutionParameter, Test> rTest = rParam.join("testExecution").join("test");

      Predicate pFixedTest = cb.equal(rTest.get("id"), cb.parameter(Long.class, "testId"));
      Predicate pFixedName = cb.equal(rParam.get("name"), cb.parameter(String.class, "paramName"));

      criteria.where(cb.and(pFixedTest, pFixedName));
      criteria.select(cb.count(rParam.get("id")));

      TypedQuery<Long> query = query(criteria);
      query.setParameter("testId", testId);
      query.setParameter("paramName", paramName);
      return query.getSingleResult() != 0l;
   }
}