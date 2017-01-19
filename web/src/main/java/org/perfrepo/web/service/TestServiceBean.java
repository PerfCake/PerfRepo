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
package org.perfrepo.web.service;

import org.perfrepo.web.model.Metric;
import org.perfrepo.web.model.Test;
import org.perfrepo.web.model.to.SearchResultWrapper;
import org.perfrepo.web.model.user.Group;
import org.perfrepo.web.model.user.User;
import org.perfrepo.web.dao.MetricDAO;
import org.perfrepo.web.dao.TestDAO;
import org.perfrepo.web.dao.UserDAO;
import org.perfrepo.web.security.AuthEntity;
import org.perfrepo.web.security.Secured;
import org.perfrepo.web.service.search.TestSearchCriteria;
import org.perfrepo.web.service.validation.annotation.ValidMetric;
import org.perfrepo.web.service.validation.annotation.ValidTest;
import org.perfrepo.web.session.UserSession;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.perfrepo.web.service.validation.ValidationType.DUPLICATE_CHECK;
import static org.perfrepo.web.service.validation.ValidationType.EXISTS;
import static org.perfrepo.web.service.validation.ValidationType.ID_NULL;
import static org.perfrepo.web.service.validation.ValidationType.SEMANTIC_CHECK;

/**
 * Implements {@link TestService}.
 *
 * @author Pavel Drozd (pdrozd@redhat.com)
 * @author Michal Linhard (mlinhard@redhat.com)
 * @author Jiri Holusa (jholusa@redhat.com)
 */
@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class TestServiceBean implements TestService {

   @Inject
   private TestDAO testDAO;

   @Inject
   private MetricDAO metricDAO;

   @Inject
   private UserService userService;

   @Inject
   private UserDAO userDAO;

   @Inject
   private UserSession userSession;

   @Inject
   private GroupService groupService;

   /******** Methods related to test ********/

   @Secured
   @Override
   public Test createTest(@ValidTest(type = { ID_NULL, SEMANTIC_CHECK, DUPLICATE_CHECK})
                          @AuthEntity(messageKey = "authorization.test.cannotCreateOrModifyTestInGroupIfNotInIt") Test test) {
      Set<Metric> metrics = test.getMetrics();
      test.setMetrics(new HashSet<>());
      Test createdTest = testDAO.create(test);

      for (Metric metric: metrics) {
         addMetric(metric, test);
      }

      return createdTest;
   }

   @Secured
   @Override
   public Test updateTest(@AuthEntity(messageKey = "authorization.test.cannotCreateOrModifyTestInGroupIfNotInIt")
                          @ValidTest(type = { EXISTS, SEMANTIC_CHECK, DUPLICATE_CHECK}) Test test) {
      return testDAO.merge(test);
   }

   @Secured
   @Override
   public void removeTest(@AuthEntity(messageKey = "authorization.test.cannotCreateOrModifyTestInGroupIfNotInIt") @ValidTest Test test) {
      Test managedTest = testDAO.get(test.getId());

      Set<Metric> metrics = getMetricsForTest(managedTest);
      for (Metric metric: metrics) {
         removeMetricFromTest(metric, test);
      }

      // corresponding objects like test executions, alerts etc. will be removed by database cascade
      testDAO.remove(managedTest);
   }

   @Override
   public Test getTest(Long id) {
      return testDAO.get(id);
   }

   @Override
   public Test getTest(String uid) {
      return testDAO.findByUid(uid);
   }

   @Override
   public List<Test> getAllTests() {
      return testDAO.getAll();
   }

   @Override
   public SearchResultWrapper<Test> getTestsForUser(User user) {
      Set<Group> userGroups = userService.getUserGroups(user);
      TestSearchCriteria searchCriteria = new TestSearchCriteria();
      searchCriteria.setGroups(userGroups);

      return searchTests(searchCriteria);
   }

   @Override
   public SearchResultWrapper<Test> searchTests(TestSearchCriteria search) {
      return testDAO.searchTests(search);
   }

   @Override
   public List<Test> getTestsByUidPrefix(String prefix) {
      List<Test> tests = testDAO.findByUIDPrefix(prefix);
      return tests.stream()
              .filter(test -> groupService.isUserInGroup(userSession.getLoggedUser(), test.getGroup()))
              .collect(Collectors.toList());
   }

   /******** Methods related to metric ********/

   @Secured
   @Override
   public Metric addMetric(@ValidMetric(type = { SEMANTIC_CHECK }) Metric metric, @AuthEntity(messageKey = "authorization.test.cannotCreateOrModifyTestInGroupIfNotInIt") @ValidTest Test test) {
      Test managedTest = testDAO.get(test.getId());

      Metric managedMetric = metricDAO.getByName(metric.getName());
      if (managedMetric == null) { // creating completely new metric
         managedMetric = metricDAO.create(metric);
      }

      managedMetric.getTests().add(managedTest);
      managedTest.getMetrics().add(managedMetric);

      return managedMetric;
   }

   //TODO: add @Secured and figure out authorization for metric
   @Override
   public Metric updateMetric(@ValidMetric(type = { EXISTS, SEMANTIC_CHECK, DUPLICATE_CHECK }) Metric metric) {
      return metricDAO.merge(metric);
   }

   @Secured
   @Override
   public void removeMetricFromTest(@ValidMetric Metric metric, @AuthEntity(messageKey = "authorization.test.cannotCreateOrModifyTestInGroupIfNotInIt") @ValidTest Test test) {
      Metric managedMetric = metricDAO.get(metric.getId());
      Test managedTest = testDAO.get(test.getId());

      managedMetric.getTests().remove(managedTest);
      managedTest.getMetrics().remove(managedMetric);

      if (managedMetric.getTests().isEmpty()) {
         metricDAO.remove(managedMetric);
      }
   }

   @Override
   public Metric getMetric(Long id) {
      return metricDAO.get(id);
   }

   @Override
   public Set<Metric> getMetricsForTest(Test test) {
      Test managedTest = testDAO.get(test.getId());
      return metricDAO.getMetricsByTest(managedTest);
   }

   /******** Methods related to subscribers ********/

   @Override
   public void addSubscriber(@ValidTest Test test) {
      Test managedTest = testDAO.get(test.getId());
      User managedUser = userDAO.get(userSession.getLoggedUser().getId());

      managedTest.getSubscribers().add(managedUser);
      managedUser.getSubscribedTests().add(managedTest);
   }

   @Override
   public void removeSubscriber(@ValidTest Test test) {
      Test managedTest = testDAO.get(test.getId());
      User managedUser = userDAO.get(userSession.getLoggedUser().getId());

      managedTest.getSubscribers().remove(managedUser);
      managedUser.getSubscribedTests().remove(test);
   }

   @Override
   public boolean isUserSubscribed(User user, @ValidTest Test test) {
      Test managedTest = testDAO.get(test.getId());
      User managedUser = userDAO.get(user.getId());

      return managedTest.getSubscribers().contains(managedUser);
   }

}
