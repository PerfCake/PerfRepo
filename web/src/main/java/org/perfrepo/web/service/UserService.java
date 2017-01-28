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

import org.perfrepo.web.model.FavoriteParameter;
import org.perfrepo.web.model.Test;
import org.perfrepo.web.model.user.Group;
import org.perfrepo.web.model.user.User;
import org.perfrepo.web.service.exceptions.IncorrectPasswordException;
import org.perfrepo.web.service.validation.annotation.ValidUser;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.perfrepo.web.service.validation.ValidationType.DUPLICATE_CHECK;
import static org.perfrepo.web.service.validation.ValidationType.EXISTS;
import static org.perfrepo.web.service.validation.ValidationType.ID_NULL;
import static org.perfrepo.web.service.validation.ValidationType.SEMANTIC_CHECK;

/**
 * Service for {@link User} entity.
 */
public interface UserService {

   /**
    * Create new user. THE USER'S PASSWORD MUST BE IN PLAIN TEXT!
    *
    * @param user
    * @return user
    */
   public User createUser(@ValidUser(type = { ID_NULL, SEMANTIC_CHECK, DUPLICATE_CHECK}) User user);

   /**
    * Updates user. THE USER'S PASSWORD MUST BE IN PLAIN TEXT!
    *
    * @param user
    * @return user
    */
   public User updateUser(@ValidUser(type = { EXISTS, SEMANTIC_CHECK, DUPLICATE_CHECK}) User user);

   /**
    * Deletes user
    *
    * @param user
    */
   public void removeUser(@ValidUser User user);

   /**
    * Retrieves managed entity of user
    *
    * @param id
    * @return user
    */
   public User getUser(Long id);

   /**
    * Return all information about user as detached entity (e.g. cloned)
    *
    * @param username
    * @return user
    */
   public User getUser(String username);

   /**
    * Retrieves all users
    *
    * @return
    */
   public List<User> getAllUsers();

   /**
    * Retrieves all user groups
    *
    * @param user
    * @return
    */
   public Set<Group> getUserGroups(User user);

   /**
    * Changes user password. Provides check if old password is equal to "current" password
    * and if yes then encrypts the new password and stores it.
    *
    * @param oldPassword
    * @param newPassword
    */
   public void changePassword(String oldPassword, String newPassword) throws IncorrectPasswordException;

   /**
    * Detects if user is group admin of any group.
    *
    * @param user
    * @return
    */
   public boolean isUserGroupAdmin(User user);

   /**
    * Detects if user is group admin of provided group.
    *
    * @param user
    * @param group
    * @return
    */
   public boolean isUserGroupAdmin(User user, Group group);

   /**
    * Returns all user properties
    *
    * @param user
    * @return user properties
    */
   public Map<String, String> getUserProperties(User user);

   /**
    * Updates user properties.
    *
    * @param properties
    */
   public void updateUserProperties(Map<String, String> properties);

   /**
    * Adds favorite parameter of user to the test
    *
    * @param parameter
    */
   public void createFavoriteParameter(FavoriteParameter parameter);

   /**
    * Updates favorite parameter of user to the test
    *
    * @param parameter
    */
   public void updateFavoriteParameter(FavoriteParameter parameter);

   /**
    * Removes favorite parameter of the test from user
    *
    * @param parameter
    */
   public void removeFavoriteParameter(FavoriteParameter parameter);

   /**
    * Returns list of favorite parameters that has user selected to specific test
    *
    * @param test
    * @return list of favorite parameters
    */
   public List<FavoriteParameter> getFavoriteParametersForTest(Test test);


}
