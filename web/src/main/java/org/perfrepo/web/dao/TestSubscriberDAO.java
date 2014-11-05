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
package org.perfrepo.web.dao;

import org.perfrepo.model.Test;
import org.perfrepo.model.TestSubscriber;
import org.perfrepo.model.user.User;

import javax.inject.Named;
import java.util.List;

/**
 * DAO for {@link TestSubscriber}.
 *
 * @author Jakub Markos (jmarkos@redhat.com)
 */
@Named
public class TestSubscriberDAO extends DAO<TestSubscriber, Long> {

   /**
    * Find all users subscribed to given test
    *
    * @param test
    * @return collection of users
    */
   public List<TestSubscriber> findByTest(Test test) {
      return getAllByProperty("test", test);
   }

   /**
    * Find all tests to which the given user is subscribed
    *
    * @param user
    * @return collection of tests
    */
   public List<TestSubscriber> findByUser(User user) {
      return getAllByProperty("user", user);
   }

}