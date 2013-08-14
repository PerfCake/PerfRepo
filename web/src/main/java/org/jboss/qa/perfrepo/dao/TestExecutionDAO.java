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
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.jboss.qa.perfrepo.model.Metric;
import org.jboss.qa.perfrepo.model.Tag;
import org.jboss.qa.perfrepo.model.Test;
import org.jboss.qa.perfrepo.model.TestExecution;
import org.jboss.qa.perfrepo.model.TestExecutionParameter;
import org.jboss.qa.perfrepo.model.TestExecutionTag;
import org.jboss.qa.perfrepo.model.Value;
import org.jboss.qa.perfrepo.model.to.MetricReportTO.DataPoint;
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
      CriteriaBuilder cb = criteriaBuilder();

      if (search.getStartedFrom() != null) {
         criteria.where(cb.greaterThanOrEqualTo(root.<Date> get("started"), search.getStartedFrom()));
      }
      if (search.getStartedTo() != null) {
         criteria.where(cb.lessThanOrEqualTo(root.<Date> get("started"), search.getStartedTo()));
      }
      if (search.getTags() != null && !"".equals(search.getTags())) {
         Join<TestExecution, TestExecutionTag> tegRoot = root.join("testExecutionTags");
         Join<TestExecutionTag, Tag> tagRoot = tegRoot.join("tag");
         Object[] tags = search.getTags().split(" ");
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
      // this isn't very ellegant, but Postgres 8.4 doesn't allow GROUP BY only with id
      // this feature is allowed only since Postgres 9.1+
      criteria.groupBy(root.get("test"), root.get("id"), root.get("name"), root.get("started"));
      return findByCustomCriteria(criteria);
   }

   /**
    * 
    * @param request
    * @return All
    */
   public List<DataPoint> searchValues(Long testId, String metricName, String paramName, List<String> tagList) {
      boolean useTags = tagList != null && !tagList.isEmpty();
      CriteriaBuilder cb = criteriaBuilder();
      CriteriaQuery<DataPoint> criteria = cb.createQuery(DataPoint.class);
      // test executions
      Root<TestExecution> rExec = criteria.from(TestExecution.class);
      // test joined via test exec.
      Join<TestExecution, Test> rTest_Exec = rExec.join("test");
      // test execution parameters
      Join<TestExecution, TestExecutionParameter> rParam = rExec.join("parameters");
      // values
      Join<TestExecution, Value> rValue = rExec.join("values");
      // metrics
      Join<Value, Metric> rMetric = rValue.join("metric");
      // test joined via metric
      Join<Metric, Test> rTest_Metric = rMetric.join("testMetrics").join("test");
      // tag
      Join<TestExecution, Tag> rTag = null;
      Predicate pTagNameInFixedList = cb.and(); // default for this predicate is true
      Predicate pHavingAllTagsPresent = cb.and();
      if (useTags) {
         rTag = rExec.join("testExecutionTags").join("tag");
         pTagNameInFixedList = rTag.get("name").in(cb.parameter(List.class, "tagList"));
         pHavingAllTagsPresent = cb.ge(cb.count(rTag.get("id")), cb.parameter(Long.class, "tagListSize"));
      }

      Predicate pMetricNameFixed = cb.equal(rMetric.get("name"), cb.parameter(String.class, "metricName"));
      Predicate pParameterNameFixed = cb.equal(rParam.get("name"), cb.parameter(String.class, "paramName"));
      Predicate pTestFixed = cb.equal(rTest_Exec.get("id"), cb.parameter(Long.class, "testId"));
      Predicate pMetricFromSameTest = cb.equal(rTest_Metric.get("id"), rTest_Exec.get("id"));
      Predicate pHavingExactlyOneValue = cb.equal(cb.count(rValue.get("id")), cb.literal(1)); // only single valued test executions

      criteria.where(cb.and(pMetricNameFixed, pParameterNameFixed, pTagNameInFixedList, pTestFixed, pMetricFromSameTest));
      criteria.select(cb.construct(DataPoint.class, rParam.get("value"), rValue.get("resultValue"), rExec.get("id")));
      criteria.groupBy(rParam.get("value"), rValue.get("resultValue"), rExec.get("id"));
      criteria.having(cb.and(pHavingExactlyOneValue, pHavingAllTagsPresent));
      criteria.orderBy(cb.asc(rParam.get("value")));

      TypedQuery<DataPoint> query = query(criteria);
      query.setParameter("testId", testId);
      query.setParameter("metricName", metricName);
      query.setParameter("paramName", paramName);
      if (useTags) {
         query.setParameter("tagList", tagList);
         query.setParameter("tagListSize", new Long(tagList.size()));
      }
      return query.getResultList();
   }
}