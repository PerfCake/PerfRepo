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

import java.util.Date;
import java.util.List;

import javax.inject.Named;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import org.jboss.qa.perfrepo.model.Tag;
import org.jboss.qa.perfrepo.model.Test;
import org.jboss.qa.perfrepo.model.TestExecution;
import org.jboss.qa.perfrepo.model.TestExecutionTag;
import org.jboss.qa.perfrepo.model.to.TestExecutionSearchTO;

/**
 * DAO for {@link TestExecution}
 * 
 * @author Pavel Drozd (pdrozd@redhat.com)
 * @author Michal Linhard (mlinhard@redhat.com)
 * 
 */
@Named
public class TestExecutionDAO extends DAO<TestExecution, Long> {

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
         criteria.where(cb.greaterThanOrEqualTo(root.<Date> get("started"), search.getStartedFrom()));
      }
      if (search.getStartedTo() != null) {
         criteria.where(cb.lessThanOrEqualTo(root.<Date> get("started"), search.getStartedTo()));
      }
      if (search.getTags() != null && !"".equals(search.getTags())) {
         Join<TestExecution, TestExecutionTag> tegRoot = root.join("testExecutionTags");
         Join<TestExecutionTag, Tag> tagRoot = tegRoot.join("tag");
         Object[] tags = search.getTags().split(";");
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

   //   public TestExecution getFullTestExecution(Long id) {
   //      return findWithDepth(id, "parameters", "values.parameters", "testExecutionTags.tag");
   //   }

}