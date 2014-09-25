package org.jboss.qa.perfrepo.service;

import org.jboss.qa.perfrepo.model.Test;
import org.jboss.qa.perfrepo.model.FavoriteParameter;
import org.jboss.qa.perfrepo.model.user.User;
import org.jboss.qa.perfrepo.service.exceptions.ServiceException;

import java.util.List;
import java.util.Map;

public interface UserService {

   /**
    * Create new user.
    *
    * @param user
    * @return user
    * @throws org.jboss.qa.perfrepo.service.exceptions.ServiceException
    */
   public User createUser(User user) throws ServiceException;

   /**
    * Update user
    *
    * @param user
    * @return user
    * @throws ServiceException
    */
   public User updateUser(User user) throws ServiceException;

   /**
    * Retrieves managed entity of user
    *
    * @param id
    * @return user
    */
   public User getUser(Long id);

   /**
    * Retrieves managed entity of currently logged user
    * @return user
    */
   public User getLoggedUser();

   /**
    * Retrieves id of currently logged user
    * @return userId
    */
   public Long getLoggedUserId();

   /**
    * Retrieves name of currently logged user
    * @return username
    */
   public String getLoggedUserName();

   /**
    * Retrieves if user is assign in defined group
    * @param guid
    * @return boolean
    */
   public boolean isLoggedUserInGroup(String guid);

   /**
    * Return all information about user as detached entity (e.g. cloned)
    *
    * @param userName
    * @return User with properties.
    */
   public User getFullUser(String userName);

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
	 * Creates or updates userProperties with given prefix
	 * @param prefix
	 * @param properties
	 */
	public void storeProperties(String prefix, Map<String, String> properties);

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
