package org.jboss.qa.perfrepo.service;

import org.jboss.qa.perfrepo.model.User;
import org.jboss.qa.perfrepo.service.ServiceException.Codes;
import org.jboss.qa.perfrepo.util.FavoriteParameter;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface UserService {

   public static final String FAV_PARAM_KEY_PREFIX = "fav.param.";

   /**
    * Create new user.
    *
    * @param user
    * @return
    * @throws ServiceException
    */
   public User createUser(User user) throws ServiceException;

   /**
    * Update user
    *
    * @param user
    * @return
    * @throws ServiceException
    */
   public User updateUser(User user) throws ServiceException;

   /**
    *
    * @param userName
    * @return User with properties.
    */
   User getFullUser(String userName);
	
	/**
	 * Returns all user properties
	 * @return
	 */
	public Map<String, String> getUserProperties();

	/**
	 * Returns user properties with given prefix
	 * @param prefix
	 * @return
	 */
	public Map<String, String> getUserProperties(String prefix);

	/**
	 * Creates or updates given userProperties
	 * @param properties
	 */
	public void storeProperties(Map<String, String> properties);

	/**
	 * Creates or updates userProperties with given prefix
	 * @param prefix
	 * @param properties
	 */
	public void storeProperties(String prefix, Map<String, String> properties);

   /**
    * Updates a set of current user's properties in one transaction.
    *
    * @param keysToRemove These properties will be removed
    * @param toUpdate These will be created or updated.
    * @throws ServiceException
    */
   void multiUpdateProperties(Collection<String> keysToRemove, Map<String, String> toUpdate) throws ServiceException;

	/**
	 * Replaces all properties with given prefix
	 * @param prefix
	 * @param properties
	 */
	public void replacePropertiesWithPrefix(String prefix, Map<String, String> properties);
	
	/**
	 * Returns true if user has properties with given prefix
	 * @param prefix
	 * @return
	 */
	public boolean userPropertiesPrefixExists(String prefix);

   /**
    * Returns favorite parameters of current user. Therefore parses the UserProperties to FavoriteParameter helper objects.
    *
    * @return
    */
   public List<FavoriteParameter> getFavoriteParameters();

   /**
    * Adds favorite parameter of user to the test
    *
    * @param testId
    * @param paramName
    * @param label
    */
   public void addFavoriteParameter(long testId, String paramName, String label) throws ServiceException;

   /**
    * Removes favorite parameter of the test from user
    *
    * @param testId
    * @param paramName
    * @param user
    */
   public void removeFavoriteParameter(long testId, String paramName) throws ServiceException;

}
