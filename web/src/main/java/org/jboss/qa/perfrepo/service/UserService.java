package org.jboss.qa.perfrepo.service;

import java.util.Map;

public interface UserService {
	
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

}
