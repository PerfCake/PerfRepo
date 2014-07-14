package org.jboss.qa.perfrepo.util;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * Util class for creating localized messages by the message code
 * @author Pavel Drozd
 *
 */
public class MessageUtils {
	
	private static ResourceBundle bundle = ResourceBundle.getBundle("lang.strings");
	
	/**
	 * Gets a formatted localized message for the given key 
	 * @param messageCode
	 * @param params
	 * @return String
	 */
	public static String getMessage(String messageCode, Object...params) {
		return MessageFormat.format(bundle.getString(messageCode), params);	    
	}

}
