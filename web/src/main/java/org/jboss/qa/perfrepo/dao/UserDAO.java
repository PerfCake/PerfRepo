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

import org.jboss.qa.perfrepo.model.user.User;

/**
 * DAO for {@link User}
 * 
 * @author Michal Linhard (mlinhard@redhat.com)
 * 
 */
@Named
public class UserDAO extends DAO<User, Long> {

   public User findByUsername(String name) {
      List<User> users = getAllByProperty("username", name);
      if (users.size() > 0) {
         return users.get(0);
      }
      return null;
   }

}