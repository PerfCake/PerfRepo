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

import org.perfrepo.model.FavoriteParameter;
import org.perfrepo.model.Test;
import org.perfrepo.model.user.Group;
import org.perfrepo.model.user.User;
import org.perfrepo.web.service.exceptions.ServiceException;

import java.util.List;
import java.util.Map;

public interface UserService {

   /**
    * Create new user.
    *
    * @param user
    * @return user
    * @throws org.perfrepo.web.service.exceptions.ServiceException
    */
   public User createUser(User user) throws ServiceException;

   /**
    * Updates user
    *
    * @param user
    * @return user
    * @throws ServiceException
    */
   public User updateUser(User user) throws ServiceException;

   /**
    * Changes user password. Provides check if old password is equal to "current" password
    * and if yes then encrypts the new password and stores it
    *
    * @param oldPassword
    * @param newPassword
    * @throws ServiceException
    */
   public void changePassword(String oldPassword, String newPassword) throws ServiceException;

   /**
    * Retrieves managed entity of user
    *
    * @param id
    * @return user
    */
   public User getUser(Long id);

   /**
    * Retrieves managed entity of group
    *
    * @param id
    * @return group
    */
   public Group getGroup(Long id);

   /**
    * Retrieves all users
    *
    * @param prefix
    * @return
    */
   public List<User> getUsers();

   /**
    * Retrieves all groups
    *
    * @return
    */
   public List<Group> getGroups();

   /**
    * Retrieves all user group names
    * @return
    */
   public List<String> getLoggedUserGroupNames();

   /**
    * Retrieves currently logged user
    *
    * @return
    */
   public User getLoggedUser();

   /**
    * Retrieves if logged user is assign in defined group
    *
    * @param guid
    * @return boolean
    */
   public boolean isLoggedUserInGroup(String guid);

   /**
    * Retrieves if user is assign in defined group
    *
    * @param userId
    * @param groupId
    * @return
    */
   public boolean isUserInGroup(Long userId, Long groupId);

   /**
    * Return all information about user as detached entity (e.g. cloned)
    *
    * @param id
    * @return User with properties.
    */
   public User getFullUser(Long id);

   /**
    * Return all information about user as detached entity (e.g. cloned)
    *
    * @param username
    * @return User with properties.
    */
   public User getFullUser(String username);

   /**
    * Returns all user properties
    *
    * @return user properties
    */
   public Map<String, String> getUserProperties();

   /**
    * Adds favorite parameter of user to the test
    *
    * @param test
    * @param paramName
    * @param label
    */
   public void addFavoriteParameter(Test test, String paramName, String label);

   /**
    * Adds user parameter
    *
    * @param paramName
    * @param value
    */
   public void addUserProperty(String paramName, String value);

   /**
    * Removes favorite parameter of the test from user
    *
    * @param test
    * @param paramName
    */
   public void removeFavoriteParameter(Test test, String paramName);

   /**
    * Returns list of favorite parameters that has current user selected to specific test
    *
    * @param test
    * @return list of favorite parameters
    */
   public List<FavoriteParameter> getFavoriteParametersForTest(Test test);
}
