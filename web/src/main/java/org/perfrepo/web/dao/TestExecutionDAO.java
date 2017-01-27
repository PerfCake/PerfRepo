/**
 * PerfRepo
 * <p>
 * Copyright (C) 2015 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.perfrepo.web.dao;

import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.perfrepo.enums.OrderBy;
import org.perfrepo.web.model.Metric;
import org.perfrepo.web.model.Tag;
import org.perfrepo.web.model.Test;
import org.perfrepo.web.model.TestExecution;
import org.perfrepo.web.model.TestExecutionParameter;
import org.perfrepo.web.model.Value;
import org.perfrepo.web.model.ValueParameter;
import org.perfrepo.web.model.to.MultiValueResultWrapper;
import org.perfrepo.web.model.to.SearchResultWrapper;
import org.perfrepo.web.model.to.SingleValueResultWrapper;
import org.perfrepo.web.model.to.TestExecutionSearchCriteria;
import org.perfrepo.web.model.user.Group;

import javax.inject.Inject;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * DAO for {@link TestExecution}
 *
 * @author Pavel Drozd (pdrozd@redhat.com)
 * @author Michal Linhard (mlinhard@redhat.com)
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public class TestExecutionDAO extends DAO<TestExecution, Long> {

   @Inject
   private TestExecutionParameterDAO testExecutionParameterDAO;

   @Inject
   private TagDAO tagDAO;

   /**
    * Returns test executions with property value between selected boundaries. This can be applied only on
    * Comparable values, otherwise the result is undefined.
    *
    * @param propertyName property to be search (filtered) on
    * @param from lower boundary
    * @param to upper boundary
    * @return list of according test executions
    */
   public <T extends Comparable<? super T>> List<TestExecution> getAllByPropertyBetween(String propertyName, T from, T to) {
      CriteriaQuery<TestExecution> resultQuery = createCriteria();
      CriteriaBuilder cb = criteriaBuilder();

      Root<TestExecution> root = resultQuery.from(TestExecution.class);
      resultQuery.select(root);
      resultQuery.where(cb.between(root.<T>get(propertyName), from, to));

      return query(resultQuery).getResultList();
   }

   /**
    * Allows to search test executions by many complex criterias.
    *
    * @param search
    * @return
    */
   public SearchResultWrapper<TestExecution> searchTestExecutions(TestExecutionSearchCriteria search) {
      CriteriaBuilder cb = criteriaBuilder();

      //normalize tags
      List<String> tags = search.getTags().stream().map(tag -> tag.getName().toLowerCase()).collect(Collectors.toList());
      List<String> excludedTags = new ArrayList<>();
      List<String> includedTags = new ArrayList<>();
      divideTags(tags, includedTags, excludedTags);

      int lastQueryResultsCount = processSearchCountQuery(search, includedTags, excludedTags);

      CriteriaQuery<TestExecution> criteria = (CriteriaQuery) createSearchSubquery(cb.createQuery(TestExecution.class), search, includedTags, excludedTags);
      Root<TestExecution> root = (Root<TestExecution>) criteria.getRoots().toArray()[0];
      criteria.select(root);
      setOrderBy(criteria, search.getOrderBy(), root);

      TypedQuery<TestExecution> query = query(criteria);
      fillParameterValues(query, search, includedTags, excludedTags);

      //handle pagination
      int firstResult = search.getLimitFrom() == null ? 0 : search.getLimitFrom();
      query.setFirstResult(firstResult);
      if (search.getLimitHowMany() != null) {
         query.setMaxResults(search.getLimitHowMany());
      }

      List<TestExecution> result = query.getResultList();
      orderResultsByParameters(result, search);

      return new SearchResultWrapper<>(result, lastQueryResultsCount);
   }

   /**
    * Retrieves result values of the test executions assigned to specific metric.
    * Behaviour on multi-value test execution in undefined
    *
    * @param search search criteria object
    * @param metric
    * @return
    */
   public List<SingleValueResultWrapper> searchValues(TestExecutionSearchCriteria search, Metric metric) {
      CriteriaBuilder cb = criteriaBuilder();
      CriteriaQuery<SingleValueResultWrapper> criteriaQuery = cb.createQuery(SingleValueResultWrapper.class);

      List<TestExecution> testExecutions = searchTestExecutions(search).getResult();
      List<Long> testExecutionIds = testExecutions.stream().map(TestExecution::getId).collect(Collectors.toList());

      Root<TestExecution> testExecution = criteriaQuery.from(TestExecution.class);
      Join<TestExecution, Value> valueJoin = testExecution.join("values");
      Join<Value, Metric> metricJoin = valueJoin.join("metric");

      Predicate selectedMetric = cb.equal(metricJoin.get("id"), metric.getId());
      Predicate selectedTestExecutions = testExecution.get("id").in(testExecutionIds);

      criteriaQuery.select(cb.construct(SingleValueResultWrapper.class, valueJoin.get("resultValue"), testExecution.get("id"), testExecution.get("started")));
      criteriaQuery.where(cb.and(selectedMetric, selectedTestExecutions));
      //TODO: this won't work correctly with ordering by parameter value, fix it according to searchMultiValues
      //TODO: this will be fixed when re-doing Metric history report
      criteriaQuery.orderBy(cb.asc(testExecution.get("started")));
      criteriaQuery.groupBy(testExecution.get("id"), valueJoin.get("resultValue"), testExecution.get("started"));

      TypedQuery<SingleValueResultWrapper> query = query(criteriaQuery);

      return query.getResultList();
   }

   /**
    * Retrieves result multi-values of the test executions assigned to specific metric.
    * Behaviour on singe-value test execution in undefined.
    *
    * @param search search criteria object
    * @param metric
    * @return
    */
   public List<MultiValueResultWrapper> searchMultiValues(TestExecutionSearchCriteria search, Metric metric) {
      CriteriaBuilder cb = criteriaBuilder();
      CriteriaQuery<Tuple> criteriaQuery = cb.createQuery(Tuple.class);

      List<TestExecution> testExecutions = searchTestExecutions(search).getResult();
      List<Long> testExecutionIds = testExecutions.stream().map(TestExecution::getId).collect(Collectors.toList());

      Root<TestExecution> testExecution = criteriaQuery.from(TestExecution.class);
      Join<TestExecution, Value> valueJoin = testExecution.join("values");
      Join<Value, Metric> metricJoin = valueJoin.join("metric");
      Join<Value, ValueParameter> valueParameterJoin = valueJoin.join("parameters");
      Join<TestExecution, TestExecutionParameter> executionParameterJoin = null;

      Predicate selectedMetric = cb.equal(metricJoin.get("id"), metric.getId());
      Predicate selectedTestExecutions = testExecution.get("id").in(testExecutionIds);
      Predicate labelParameter = cb.and();

      Path<?> labelPath = testExecution.get("started");
      // we have to add the "started" to group by, as it's workaround for PostgreSQL 8, because we order by it by default
      // and PostgreSQL 8 requires to have all used columns in the GROUP BY
      List<Expression<?>> groupBy = new ArrayList<>(Arrays.asList(testExecution.get("id"), valueJoin.get("resultValue"), valueParameterJoin.get("name"), valueParameterJoin.get("paramValue"), testExecution.get("started")));

      if (search.getLabelParameter() != null) {
         executionParameterJoin = testExecution.join("parameters");
         labelParameter = cb.equal(executionParameterJoin.get("name"), search.getLabelParameter());
         labelPath = executionParameterJoin.get("value");
         groupBy.add(labelPath); // workaround for PostgreSQL 8, if label used, add it to GROUP BY, see the comment above
      }

      criteriaQuery.multiselect(valueJoin.get("resultValue").alias("resultValue"),
                                valueParameterJoin.get("name").alias("valueParameterName"),
                                valueParameterJoin.get("paramValue").alias("valueParameterValue"),
                                testExecution.get("id").alias("execId"),
                                labelPath.alias("label")
      );

      criteriaQuery.where(cb.and(selectedMetric, selectedTestExecutions, labelParameter));
      criteriaQuery.orderBy(cb.asc(testExecution.get("started")));
      criteriaQuery.groupBy(groupBy);

      TypedQuery<Tuple> query = query(criteriaQuery);
      List<Tuple> queryResult = query.getResultList();

      Map<Long, MultiValueResultWrapper> unsortedResult = new HashMap<>();

      //this way of computing might seem strange, but is necessary. We need to keep ordering by
      //test executions. However, retrieved tuples are one for every value. Therefore, we perform
      //something like "group by test execution ID" on the results and construct result wrappers.
      for (Tuple tuple : queryResult) {
         Long execId = tuple.get("execId", Long.class);

         unsortedResult.putIfAbsent(execId, new MultiValueResultWrapper(execId, tuple.get("label")));

         String valueParameterName = tuple.get("valueParameterName", String.class);
         String valueParameterValue = tuple.get("valueParameterValue", String.class);
         Double resultValue = tuple.get("resultValue", Double.class);

         unsortedResult.get(execId).addValue(valueParameterName, valueParameterValue, resultValue);
      }

      //perform after-sort, the ResultWrappers have to be in the same order as retrieved test executions
      List<MultiValueResultWrapper> finalSortedResult = new ArrayList<>();
      testExecutions.stream().filter(execution -> unsortedResult.keySet().contains(execution.getId())).forEach(execution -> finalSortedResult.add(unsortedResult.get(execution.getId())));

      return finalSortedResult;
   }

   /**
    * Helper method. Adds ordering to the query depending on the criteria option.
    *
    * @param criteria
    * @param orderBy
    * @param root
    */
   private void setOrderBy(CriteriaQuery criteria, OrderBy orderBy, Root root) {
      CriteriaBuilder cb = criteriaBuilder();

      //ignoring other OrderBy options (like PARAMETER), because it's not possible to order
      //by values for specific parameter name in SQL. Therefore if the the option is PARAMETER(ASC|DESC)
      //it's ordered afterwards, just like filterResultByParameters, see orderResultsByParameters
      Order order;
      switch (orderBy) {
         case DATE_ASC:
            order = cb.asc(root.get("started"));
            break;
         case DATE_DESC:
            order = cb.desc(root.get("started"));
            break;
         case NAME_ASC:
            order = cb.asc(root.get("name"));
            break;
         case NAME_DESC:
            order = cb.desc(root.get("name"));
            break;
         default:
            order = cb.desc(root.get("started"));
      }

      criteria.orderBy(order);
   }

   /**
    * Helper method. Creates a criteria query of Criteria API for searching of test executions. Because we also
    * sometimes need to limit the number of results and it's not possible to easily reuse the query for both
    * count query and actual retrieval, this construction of criteria is extracted into this method to avoid code
    * duplication. Also makes the code more readable.
    *
    * @param search
    */
   private AbstractQuery createSearchSubquery(AbstractQuery criteriaQuery, TestExecutionSearchCriteria search, List<String> includedTags, List<String> excludedTags) {
      AbstractQuery criteria = criteriaQuery;
      CriteriaBuilder cb = criteriaBuilder();

      //initialize predicates
      Predicate pIds = cb.and();
      Predicate pStartedFrom = cb.and();
      Predicate pStartedTo = cb.and();
      Predicate pExcludedTags = cb.and();
      Predicate pTestName = cb.and();
      Predicate pTestUID = cb.and();
      Predicate pTestGroups = cb.and();
      Predicate pParamsMatch = cb.and();
      List<Predicate> pTags = new ArrayList<>();

      Root<TestExecution> rExec = criteria.from(TestExecution.class);

      // construct criteria
      if (search.getIds() != null && !search.getIds().isEmpty()) {
         pIds = rExec.<Long>get("id").in(cb.parameter(Set.class, "ids"));
      }
      if (search.getStartedFrom() != null) {
         pStartedFrom = cb.greaterThanOrEqualTo(rExec.<Date>get("started"), cb.parameter(Date.class, "startedFrom"));
      }
      if (search.getStartedTo() != null) {
         pStartedTo = cb.lessThanOrEqualTo(rExec.<Date>get("started"), cb.parameter(Date.class, "startedTo"));
      }
      if (!includedTags.isEmpty() || !excludedTags.isEmpty()) {
         Path<Collection<Tag>> tagsPath = rExec.<Collection<Tag>>get("tags");
         if (!includedTags.isEmpty()) {
            for (int i = 0; i < includedTags.size(); i++) {

               Predicate includedTag = cb.isMember(cb.parameter(Tag.class, "tag" + i), tagsPath);
               pTags.add(includedTag);
            }
         }

         if (!excludedTags.isEmpty()) {
            for (int i = 0; i < excludedTags.size(); i++) {
               Predicate excludedTag = cb.isNotMember(cb.parameter(Tag.class, "excludedTag" + i), tagsPath);
               pTags.add(excludedTag);
            }
         }
      }
      if (search.getTestName() != null && !"".equals(search.getTestName())) {
         Join<TestExecution, Test> rTest = rExec.join("test");
         pTestName = cb.like(cb.lower(rTest.<String>get("name")), cb.parameter(String.class, "testName"));
      }
      if (!search.getTestUIDs().isEmpty()) {
         Join<TestExecution, Test> rTest = rExec.join("test");
         pTestUID = cb.lower(rTest.<String>get("uid")).in(cb.parameter(Set.class, "testUIDs"));
      }
      if (!search.getGroups().isEmpty()) {
         Join<TestExecution, Group> rGroup = rExec.join("test").join("group");
         pTestGroups = cb.and(rGroup.get("name").in(cb.parameter(Set.class, "groupNames")));
      }
      if (search.getParameters() != null && !search.getParameters().isEmpty()) {
         for (int pCount = 1; pCount < search.getParameters().size() + 1; pCount++) {
            Join<TestExecution, TestExecutionParameter> rParam = rExec.join("parameters");
            pParamsMatch = cb.and(pParamsMatch, cb.equal(rParam.get("name"), cb.parameter(String.class, "paramName" + pCount)));
            pParamsMatch = cb.and(pParamsMatch, cb.like(rParam.<String>get("value"), cb.parameter(String.class, "paramValue" + pCount)));
         }
      }
      // construct query
      Predicate whereClause = cb.and(pIds, pStartedFrom, pStartedTo, pExcludedTags, pTestName, pTestUID, pTestGroups, pParamsMatch);
      for (Predicate predicate: pTags) {
         whereClause = cb.and(whereClause, predicate);
      }
      criteria.where(whereClause);
      // this isn't very elegant, but Postgres 8.4 doesn't allow GROUP BY only with id
      // this feature is allowed only since Postgres 9.1+
      criteria.groupBy(rExec.get("test"), rExec.get("id"), rExec.get("name"), rExec.get("started"), rExec.get("comment"));

      return criteria;
   }

   /**
    * Helper method. During the search of test executions, if there is a limit set, we have to count
    * the test executions suit the conditions.
    *
    * @param search
    * @param includedTags
    * @param excludedTags
    * @return
    */
   private Integer processSearchCountQuery(TestExecutionSearchCriteria search, List<String> includedTags, List<String> excludedTags) {
      CriteriaBuilder cb = criteriaBuilder();

      CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
      Root<TestExecution> root = countQuery.from(TestExecution.class);
      countQuery.select(cb.countDistinct(root));

      Subquery<Long> subquery = (Subquery) createSearchSubquery(countQuery.subquery(Long.class), search, includedTags, excludedTags);
      Root<TestExecution> subqueryRoot = (Root<TestExecution>) subquery.getRoots().toArray()[0];
      subquery.select(subqueryRoot.<Long>get("id"));

      countQuery.where(cb.in(root.get("id")).value(subquery));
      TypedQuery<Long> typedCountQuery = query(countQuery);
      fillParameterValues(typedCountQuery, search, includedTags, excludedTags);

      Long count = typedCountQuery.getSingleResult();
      return count.intValue();
   }

   /**
    * Performs ordering on test executions by values of specified parameter.
    *
    * @param testExecutions
    * @param search
    */
   private void orderResultsByParameters(List<TestExecution> testExecutions, TestExecutionSearchCriteria search) {
      if (!Arrays.asList(OrderBy.PARAMETER_ASC,
                         OrderBy.PARAMETER_DESC,
                         OrderBy.VERSION_ASC,
                         OrderBy.VERSION_DESC).contains(search.getOrderBy())) {
         return;
      }

      Collections.sort(testExecutions,
                       (o1, o2) -> {
                          String o1paramValue = o1.getParameters().get(search.getOrderByParameter()).getValue();
                          int orderCoefficient = search.getOrderBy() == OrderBy.PARAMETER_ASC || search.getOrderBy() == OrderBy.VERSION_ASC ? 1 : -1;
                          if (o1paramValue == null) {
                             return orderCoefficient * 1;
                          }

                          return orderCoefficient * performCompare(o1paramValue, o2.getParameters().get(search.getOrderByParameter()).getValue(), search);
                       });
   }

   /**
    * According to selected ordering mechanism, this method performs the comparison.
    *
    * @param value1
    * @param value2
    * @param search
    * @return
    */
   private int performCompare(String value1, String value2, TestExecutionSearchCriteria search) {
      if (search.getOrderBy() == OrderBy.VERSION_ASC || search.getOrderBy() == OrderBy.VERSION_DESC) {
         DefaultArtifactVersion version1 = new DefaultArtifactVersion(value1);
         DefaultArtifactVersion version2 = new DefaultArtifactVersion(value2);

         return version1.compareTo(version2);
      }

      return value1.compareTo(value2);
   }

   /**
    * Helper method. Search query is quite complicated and has a lot of parameters. This method
    * assigns the value to every predefined parameter.
    *
    * @param query
    * @param search
    * @param includedTags
    * @param excludedTags
    */
   private void fillParameterValues(TypedQuery query, TestExecutionSearchCriteria search, List<String> includedTags, List<String> excludedTags) {
      if (search.getIds() != null && !search.getIds().isEmpty()) {
         query.setParameter("ids", search.getIds());
      }
      if (search.getStartedFrom() != null) {
         query.setParameter("startedFrom", search.getStartedFrom());
      }
      if (search.getStartedTo() != null) {
         query.setParameter("startedTo", search.getStartedTo());
      }
      if (!includedTags.isEmpty()) {
         for (int i = 0; i < includedTags.size(); i++) {
            Tag tag = tagDAO.findByName(includedTags.get(i));
            query.setParameter("tag" + i, tag);
         }
      }
      if (!excludedTags.isEmpty()) {
         for (int i = 0; i < excludedTags.size(); i++) {
            Tag tag = tagDAO.findByName(excludedTags.get(i));
            query.setParameter("excludedTag" + i, tag);
         }
      }
      if (search.getTestName() != null && !"".equals(search.getTestName())) {
         if (search.getTestName().endsWith("*")) {
            String pattern = search.getTestName().replace('*', '%');
            query.setParameter("testName", pattern);
         } else {
            query.setParameter("testName", search.getTestName().toLowerCase());
         }
      }
      if (!search.getTestUIDs().isEmpty()) {
         query.setParameter("testUIDs", search.getTestUIDs());
      }
      if (!search.getGroups().isEmpty()) {
         query.setParameter("groupNames", search.getGroups().stream().map(Group::getName).collect(Collectors.toSet()));
      }
      if (search.getParameters() != null && !search.getParameters().isEmpty()) {
         int pCount = 1;
         for (String key: search.getParameters().keySet()) {
            query.setParameter("paramName" + pCount, key);
            if (search.getParameters().get(key).endsWith("*")) {
               String pattern = search.getParameters().get(key).replace('*', '%').toLowerCase();
               query.setParameter("paramValue" + pCount, pattern);
            } else {
               query.setParameter("paramValue" + pCount, search.getParameters().get(key).toLowerCase());
            }
            pCount++;
         }
      }
   }

   /**
    * Helper method. Divides the list of tags to two groups - included and excluded tags. Excluded tags have
    * prefix '-'. Divides and stores it into the parameters.
    *
    * @param inputTags
    * @param outputExcluded
    * @param outputIncluded
    * @return
    */
   private void divideTags(List<String> inputTags, List<String> outputIncluded, List<String> outputExcluded) {
      Iterator<String> iterator = inputTags.iterator();
      while (iterator.hasNext()) {
         String value = iterator.next();
         if (value.isEmpty()) {
            continue;
         }

         if (value.startsWith("-")) {
            outputExcluded.add(value.substring(1));
         } else {
            outputIncluded.add(value);
         }
      }
   }
}