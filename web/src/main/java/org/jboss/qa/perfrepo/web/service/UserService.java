package org.jboss.qa.perfrepo.web.service;

import org.jboss.qa.perfrepo.model.Test;
import org.jboss.qa.perfrepo.model.FavoriteParameter;
import org.jboss.qa.perfrepo.model.user.User;
import org.jboss.qa.perfrepo.web.service.exceptions.ServiceException;

import java.util.List;
import java.util.Map;

public interface UserService {

   /**
    * Create new user.
    *
    * @param user
    * @return user
    * @throws org.jboss.qa.perfrepo.web.service.exceptions.ServiceException
    */
   public User createUser(User user) throws ServiceException;

   /**
    * Retrieves managed entity of user
    *
    * @param id
    * @return user
    */
   public User getUser(Long id);

   /**
    * Retrieves currently logged user
    *
    * @return
    */
   public User getLoggedUser();

   /**
    * Retrieves if logged user is assign in defined group
    * @param guid
    * @return boolean
    */
   public boolean isLoggedUserInGroup(String guid);

   /**
    * Retrieves if user is assign in defined group
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
   public void addFavoriteParameter(Test test, String paramName, String label) throws ServiceException;

   /**
    * Removes favorite parameter of the test from user
    *
    * @param test
    * @param paramName
    */
   public void removeFavoriteParameter(Test test, String paramName) throws ServiceException;

   /**
    * Returns list of favorite parameters that has current user selected to specific test
    *
    * @param test
    * @return list of favorite parameters
    */
   public List<FavoriteParameter> getFavoriteParametersForTest(Test test);

}
