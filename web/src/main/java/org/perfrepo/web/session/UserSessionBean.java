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
package org.perfrepo.web.session;

import org.perfrepo.enums.OrderBy;
import org.perfrepo.web.model.user.User;
import org.perfrepo.web.security.authentication.AuthenticatedUser;
import org.perfrepo.web.security.authentication.AuthenticatedUserInfo;
import org.perfrepo.web.service.UserService;
import org.perfrepo.web.service.search.ReportSearchCriteria;
import org.perfrepo.web.service.search.TestExecutionSearchCriteria;
import org.perfrepo.web.service.search.TestSearchCriteria;

import javax.inject.Inject;

/**
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public class UserSessionBean implements UserSession {

   @Inject
   @AuthenticatedUser
   private AuthenticatedUserInfo userInfo;

   @Inject
   private UserService userService;

   @Override
   public User getLoggedUser() {
      User user = null;
      if (userInfo != null) {
         user = userInfo.getUser();
      }
      return user;
   }

   @Override
   public TestSearchCriteria getTestSearchCriteria() {
      if (userInfo == null) {
         return null;
      }

      if (userInfo.getTestSearchCriteria() == null) {
         TestSearchCriteria defaultSearchCriteria = new TestSearchCriteria();
         defaultSearchCriteria.setLimitHowMany(20);
         defaultSearchCriteria.setOrderBy(OrderBy.NAME_ASC);
         defaultSearchCriteria.setGroups(userService.getUserGroups(userInfo.getUser()));

         userInfo.setTestSearchCriteria(defaultSearchCriteria);
      }

      return userInfo.getTestSearchCriteria();
   }

   @Override
   public TestExecutionSearchCriteria getTestExecutionSearchCriteria() {
      if (userInfo == null) {
         return null;
      }

      if (userInfo.getTestExecutionSearchCriteria() == null) {
         TestExecutionSearchCriteria defaultSearchCriteria = new TestExecutionSearchCriteria();
         defaultSearchCriteria.setLimitHowMany(20);
         defaultSearchCriteria.setOrderBy(OrderBy.DATE_DESC);
         defaultSearchCriteria.setGroups(userService.getUserGroups(userInfo.getUser()));

         userInfo.setTestExecutionSearchCriteria(defaultSearchCriteria);
      }

      return userInfo.getTestExecutionSearchCriteria();
   }

   @Override
   public void setTestSearchCriteria(TestSearchCriteria criteria) {
      if (userInfo == null) {
         return;
      }

      userInfo.setTestSearchCriteria(criteria);
   }

   @Override
   public void setTestExecutionSearchCriteria(TestExecutionSearchCriteria criteria) {
      if (userInfo == null) {
         return;
      }

      userInfo.setTestExecutionSearchCriteria(criteria);
   }

   @Override
   public ReportSearchCriteria getReportSearchCriteria() {
      if (userInfo == null) {
         return null;
      }

      if (userInfo.getReportSearchCriteria() == null) {
         ReportSearchCriteria defaultCriteria = new ReportSearchCriteria();
         defaultCriteria.setLimitHowMany(20);
         //TODO: add some defaults
         userInfo.setReportSearchCriteria(defaultCriteria);
      }

      return userInfo.getReportSearchCriteria();
   }

   @Override
   public void setReportSearchCriteria(ReportSearchCriteria criteria) {

   }

   @Override
   public ComparisonSession getComparisonSession() {
      if (userInfo == null) {
         return null;
      }

      if (userInfo.getComparisonSession() == null) {
         ComparisonSession session = new ComparisonSession();
         userInfo.setComparisonSession(session);
      }

      return userInfo.getComparisonSession();
   }

   @Override
   public void setComparisonSession(ComparisonSession session) {

   }
}
