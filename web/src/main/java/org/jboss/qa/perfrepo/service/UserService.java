package org.jboss.qa.perfrepo.service;

import org.jboss.qa.perfrepo.model.User;

import java.util.Map;

public interface UserService {

   public static final String FAV_PARAM_KEY_PREFIX = "fav.param.";
	
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
    * Adds favorite parameter of user to the test
    *
    * @param testId
    * @param paramName
    * @param label
    */
   public void addFavoriteParameter(long testId, String paramName, String label, User user) throws ServiceException;

   /**
    * Removes favorite parameter of the test from user
    *
    * @param testId
    * @param paramName
    * @param user
    */
   public void removeFavoriteParameter(long testId, String paramName, User user) throws ServiceException;

}
