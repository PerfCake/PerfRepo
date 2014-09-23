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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Named;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.jboss.qa.perfrepo.model.Metric;
import org.jboss.qa.perfrepo.model.Tag;
import org.jboss.qa.perfrepo.model.Test;
import org.jboss.qa.perfrepo.model.TestExecution;
import org.jboss.qa.perfrepo.model.TestExecutionParameter;
import org.jboss.qa.perfrepo.model.TestExecutionTag;
import org.jboss.qa.perfrepo.model.Value;
import org.jboss.qa.perfrepo.model.to.MetricReportTO.DataPoint;
import org.jboss.qa.perfrepo.model.to.TestExecutionSearchTO;
import org.jboss.qa.perfrepo.model.to.TestExecutionSearchTO.ParamCriteria;
import org.jboss.qa.perfrepo.model.util.EntityUtil;
import org.jboss.qa.perfrepo.util.TagUtils;

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

   /**
    * Fetch test via JPA relationship.
    * 
    * @param exec
    * @return
    */
   public static TestExecution fetchTest(TestExecution exec) {
      exec.setTest(exec.getTest().clone());
      exec.getTest().setTestExecutions(null);
      exec.getTest().setTestMetrics(null);
      return exec;
   }

   /**
    * Fetch tags via JPA relationships.
    * 
    * @param testExecution
    * @return TestExecution with fetched tags
    */
   public static TestExecution fetchTags(TestExecution testExecution) {
      Collection<TestExecutionTag> cloneTags = new ArrayList<TestExecutionTag>();
      for (TestExecutionTag interObject : testExecution.getTestExecutionTags()) {
         cloneTags.add(interObject.cloneWithTag());
      }
      testExecution.setTestExecutionTags(cloneTags);
      return testExecution;
   }

   /**
    * Fetch parameters via JPA relationships.
    * 
    * @param testExecution
    * @return TestExecution with fetched parameters
    */
   public static TestExecution fetchParameters(TestExecution testExecution) {
      testExecution.setParameters(EntityUtil.clone(testExecution.getParameters()));
      return testExecution;
   }

   /**
    * Fetch attachments via JPA relationships.
    * 
    * @param testExecution
    * @return TestExecution with fetched attachments
    */
   public static TestExecution fetchAttachments(TestExecution testExecution) {
      testExecution.setAttachments(EntityUtil.clone(testExecution.getAttachments()));
      return testExecution;
   }

   /**
    * Fetch values with parameters via JPA relationships.
    * 
    * @param testExecution
    * @return TestExecution with fetched values
    */
   public static TestExecution fetchValues(TestExecution testExecution) {
      List<Value> cloneValues = new ArrayList<Value>();
      if (testExecution.getValues() != null) {
	      for (Value v : testExecution.getValues()) {
	         cloneValues.add(v.cloneWithParameters());
	      }
	      testExecution.setValues(cloneValues);
      }
      return testExecution;
   }

   public List<TestExecution> findByTestAndJob(Long testId, Long jobId) {
      Test test = new Test();
      test.setId(testId);
      CriteriaQuery<TestExecution> criteria = createCriteria();
      Root<TestExecution> root = criteria.from(TestExecution.class);
      criteria.select(root);
      CriteriaBuilder cb = criteriaBuilder();
      Predicate p = cb.and(cb.equal(root.get("test"), test), cb.equal(root.get("jobId"), jobId));
      criteria.where(p);
      return query(criteria).getResultList();
   }

   public List<TestExecution> searchTestExecutions(TestExecutionSearchTO search, TestExecutionParameterDAO paramDAO) {
      CriteriaQuery<TestExecution> criteria = createCriteria();
      CriteriaBuilder cb = criteriaBuilder();
      List<String> tags = TagUtils.parseTags(search.getTags() !=null ? search.getTags().toLowerCase() : "");
      List<String> tmpTags = new ArrayList<String>();
      List<String> excludedTags = new ArrayList<String>();

      //tags beginning with "-" put into separate list and without into different one, not using
      //remove() because of UnsupportedOperationException
      Iterator<String> iterator = tags.iterator();
      while(iterator.hasNext()) {
         String value = iterator.next();
         if(value.isEmpty()) {
            continue;
         }

         if(value.startsWith("-")) {
            excludedTags.add(value.substring(1));
         }
         else {
            tmpTags.add(value);
         }
      }
      tags = tmpTags;

      Root<TestExecution> rExec = criteria.from(TestExecution.class);

      Predicate pStartedFrom = cb.and();
      Predicate pStartedTo = cb.and();
      Predicate pTagNameInFixedList = cb.and();
      Predicate pExcludedTags = cb.and();
      Predicate pTestName = cb.and();
      Predicate pTestUID = cb.and();
      Predicate pParamsMatch = cb.and();
      Predicate pHavingAllTagsPresent = cb.and();

      // construct criteria
      if (search.getStartedFrom() != null) {
         pStartedFrom = cb.greaterThanOrEqualTo(rExec.<Date> get("started"), cb.parameter(Date.class, "startedFrom"));
      }
      if (search.getStartedTo() != null) {
         pStartedTo = cb.lessThanOrEqualTo(rExec.<Date> get("started"), cb.parameter(Date.class, "startedTo"));
      }
      if(!tags.isEmpty() || !excludedTags.isEmpty()) {
         Join<TestExecution, Tag> rTag = rExec.joinCollection("testExecutionTags").join("tag");
         if (!tags.isEmpty()) {
            pTagNameInFixedList = cb.lower(rTag.<String>get("name")).in(cb.parameter(List.class, "tagList"));
            pHavingAllTagsPresent = cb.ge(cb.count(rTag.get("id")), cb.parameter(Long.class, "tagListSize"));
         }

         if (!excludedTags.isEmpty()) {
            Subquery<TestExecution> sq = criteria.subquery(TestExecution.class);
            Root sqRoot = sq.from(TestExecution.class);
            Join<TestExecution, Tag> sqTag = sqRoot.joinCollection("testExecutionTags").join("tag");
            sq.select(sqRoot.get("id"));
            sq.where(cb.lower(sqTag.<String>get("name")).in(cb.parameter(List.class, "excludedTagList")));

            pExcludedTags = cb.not(rExec.get("id").in(sq));
         }
      }
      if (search.getTestName() != null && !"".equals(search.getTestName())) {
         Join<TestExecution, Test> rTest = rExec.join("test");
         pTestName = cb.like(rTest.<String> get("name"), cb.parameter(String.class, "testName"));
      }
      if (search.getTestUID() != null && !"".equals(search.getTestUID())) {
         Join<TestExecution, Test> rTest = rExec.join("test");
         pTestUID = cb.like(rTest.<String> get("uid"), cb.parameter(String.class, "testUID"));
      }
      List<String> displayedParams = null;
      if (search.getParameters() != null && !search.getParameters().isEmpty()) {
         displayedParams = new ArrayList<String>(search.getParameters().size());
         for (ParamCriteria pc : search.getParameters()) {
            if (pc.isDisplayed()) {
               displayedParams.add(pc.getName());
            }
            if (pc.getValue() == null || "".equals(pc.getValue().trim())) {
               pc.setValue("%");
            }
         }
         for (int pCount = 1; pCount < search.getParameters().size() + 1; pCount++) {
            Join<TestExecution, TestExecutionParameter> rParam = rExec.join("parameters");
            pParamsMatch = cb.and(pParamsMatch, cb.equal(rParam.get("name"), cb.parameter(String.class, "paramName" + pCount)));
            pParamsMatch = cb.and(pParamsMatch, cb.like(rParam.<String> get("value"), cb.parameter(String.class, "paramValue" + pCount)));
         }
      }

      // construct query
      criteria.select(rExec);
      criteria.where(cb.and(pStartedFrom, pStartedTo, pTagNameInFixedList, pExcludedTags, pTestName, pTestUID, pParamsMatch));
      criteria.having(pHavingAllTagsPresent);
      // this isn't very ellegant, but Postgres 8.4 doesn't allow GROUP BY only with id
      // this feature is allowed only since Postgres 9.1+
      criteria.groupBy(rExec.get("test"), rExec.get("id"), rExec.get("name"), rExec.get("locked"), rExec.get("started"), rExec.get("jobId"), rExec.get("comment"));
      TypedQuery<TestExecution> query = query(criteria);

      // set parameters
      if (search.getStartedFrom() != null) {
         query.setParameter("startedFrom", search.getStartedFrom());
      }
      if (search.getStartedTo() != null) {
         query.setParameter("startedTo", search.getStartedTo());
      }
      if (!tags.isEmpty()) {
         query.setParameter("tagList", tags);
         query.setParameter("tagListSize", new Long(tags.size()));
      }
      if (!excludedTags.isEmpty()) {
         query.setParameter("excludedTagList", excludedTags);
      }
      if (search.getTestName() != null && !"".equals(search.getTestName())) {
         query.setParameter("testName", search.getTestName());
      }
      if (search.getTestUID() != null && !"".equals(search.getTestUID())) {
         query.setParameter("testUID", search.getTestUID());
      }
      if (search.getParameters() != null && !search.getParameters().isEmpty()) {
         int pCount = 1;
         for (ParamCriteria paramCriteria : search.getParameters()) {
            query.setParameter("paramName" + pCount, paramCriteria.getName());
            query.setParameter("paramValue" + pCount, paramCriteria.getValue());
            pCount++;
         }
      }
      List<TestExecution> tmp = query.getResultList();
      List<TestExecution> r = EntityUtil.clone(tmp);
      List<Long> execIds = EntityUtil.extractIds(r);
      if (displayedParams != null && !displayedParams.isEmpty() && !execIds.isEmpty() && paramDAO != null) {
         List<TestExecutionParameter> allParams = paramDAO.find(execIds, displayedParams);
         Map<Long, List<TestExecutionParameter>> paramsByExecId = new HashMap<Long, List<TestExecutionParameter>>();
         for (TestExecutionParameter param : allParams) {
            List<TestExecutionParameter> paramListForExec = paramsByExecId.get(param.getTestExecution().getId());
            if (paramListForExec == null) {
               paramListForExec = new ArrayList<TestExecutionParameter>(displayedParams.size());
               paramsByExecId.put(param.getTestExecution().getId(), paramListForExec);
            }
            paramListForExec.add(param);
         }
         for (TestExecution exec : r) {
            List<TestExecutionParameter> paramListForExec = paramsByExecId.get(exec.getId());
            exec.setParameters(paramListForExec == null ? Collections.<TestExecutionParameter> emptyList() : paramListForExec);
            exec.setTestExecutionTags(EntityUtil.clone(exec.getTestExecutionTags()));
            for (TestExecutionTag tet : exec.getTestExecutionTags()) {
               tet.setTag(tet.getTag().clone());
            }
         }
      } else {
         for (TestExecution exec : r) {
            exec.setParameters(Collections.<TestExecutionParameter> emptyList());
            exec.setTestExecutionTags(EntityUtil.clone(exec.getTestExecutionTags()));
            for (TestExecutionTag tet : exec.getTestExecutionTags()) {
               tet.setTag(tet.getTag().clone());
            }
         }
      }
      return r;
   }

   public List<TestExecution> getTestExecutions(List<String> tags, List<String> testUIDs) {
	   CriteriaQuery<TestExecution> criteria = createCriteria();
	   CriteriaBuilder cb = criteriaBuilder();
	   Root<TestExecution> rExec = criteria.from(TestExecution.class);
      Join<TestExecution, Test> rTest = rExec.join("test");
      Predicate pTestUID = rTest.<String> get("uid").in(cb.parameter(List.class, "testUID"));
      Join<TestExecution, Tag> rTag = rExec.join("testExecutionTags").join("tag");
      Predicate pTagNameInFixedList = rTag.get("name").in(cb.parameter(List.class, "tagList"));
      Predicate pHavingAllTagsPresent = cb.ge(cb.count(rTag.get("id")), cb.parameter(Long.class, "tagListSize"));

	   criteria.select(rExec);
	   criteria.where(cb.and(pTagNameInFixedList, pTestUID));
	   criteria.having(pHavingAllTagsPresent);
	   criteria.groupBy(rExec.get("test"), rExec.get("id"), rExec.get("name"), rExec.get("locked"), rExec.get("started"), rExec.get("jobId"), rExec.get("comment"));

      TypedQuery<TestExecution> query = query(criteria);
	   query.setParameter("testUID", testUIDs);
      query.setParameter("tagList", tags);
      query.setParameter("tagListSize", new Long(tags.size()));

      List<TestExecution> result = EntityUtil.clone(query.getResultList());
      for (TestExecution exec : result) {
         TestExecutionDAO.fetchTest(exec);
         TestExecutionDAO.fetchParameters(exec);
         TestExecutionDAO.fetchTags(exec);
         TestExecutionDAO.fetchValues(exec);
      }

      return result;
   }

   /**
    * Finds all values used for computing MetricHistory report
    *
    * @param testId
    * @param metricName
    * @param tagList
    * @param limitSize
    * @return List of DataPoint objects
    */
   public List<DataPoint> searchValues(Long testId, String metricName, List<String> tagList, int limitSize) {
      boolean useTags = tagList != null && !tagList.isEmpty();
      CriteriaBuilder cb = criteriaBuilder();
      CriteriaQuery<DataPoint> criteria = cb.createQuery(DataPoint.class);
      // test executions
      Root<TestExecution> rExec = criteria.from(TestExecution.class);
      // test joined via test exec.
      Join<TestExecution, Test> rTest_Exec = rExec.join("test");
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
      Predicate pTestFixed = cb.equal(rTest_Exec.get("id"), cb.parameter(Long.class, "testId"));
      Predicate pMetricFromSameTest = cb.equal(rTest_Metric.get("id"), rTest_Exec.get("id"));

      //sort by date
      criteria.select(cb.construct(DataPoint.class, rExec.get("started"), rValue.get("resultValue"), rExec.get("id")));
      criteria.where(cb.and(pMetricNameFixed, pTagNameInFixedList, pTestFixed, pMetricFromSameTest));
      criteria.groupBy(rValue.get("resultValue"), rExec.get("id"), rExec.get("started"));
      criteria.orderBy(cb.desc(rExec.get("started")));

      criteria.having(pHavingAllTagsPresent);

      TypedQuery<DataPoint> query = query(criteria);
      query.setParameter("testId", testId);
      query.setParameter("metricName", metricName);

      if (useTags) {
         query.setParameter("tagList", tagList);
         query.setParameter("tagListSize", new Long(tagList.size()));
      }
      query.setMaxResults(limitSize);
      return query.getResultList();
   }

   public Double getValueForMetric(Long execId, String metricName) {
      CriteriaBuilder cb = criteriaBuilder();
      CriteriaQuery<Double> criteria = cb.createQuery(Double.class);
      // test executions
      Root<TestExecution> rExec = criteria.from(TestExecution.class);
      // test joined via test exec.
      Join<TestExecution, Test> rTest_Exec = rExec.join("test");
      // values
      Join<TestExecution, Value> rValue = rExec.join("values");
      // metrics
      Join<Value, Metric> rMetric = rValue.join("metric");
      // test joined via metric
      Join<Metric, Test> rTest_Metric = rMetric.join("testMetrics").join("test");

      Predicate pMetricNameFixed = cb.equal(rMetric.get("name"), cb.parameter(String.class, "metricName"));
      Predicate pExecFixed = cb.equal(rExec.get("id"), cb.parameter(Long.class, "execId"));
      Predicate pMetricFromSameTest = cb.equal(rTest_Metric.get("id"), rTest_Exec.get("id"));
      criteria.multiselect(rValue.get("resultValue"));
      criteria.where(cb.and(pMetricNameFixed, pExecFixed, pMetricFromSameTest));

      TypedQuery<Double> query = query(criteria);
      query.setParameter("execId", execId);
      query.setParameter("metricName", metricName);

      List<Double> r = query.getResultList();
      if (r.isEmpty()) {
         return null;
      } else {
         return r.get(0);
      }
   }
}