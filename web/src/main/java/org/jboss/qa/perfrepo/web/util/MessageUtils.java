package org.jboss.qa.perfrepo.web.util;

import org.jboss.qa.perfrepo.web.service.exceptions.ServiceException;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * MessageUtils class for creating localized messages by the message code
 *
 * @author Pavel Drozd
 */
public class MessageUtils {

	private static ResourceBundle bundle = ResourceBundle.getBundle("lang.strings");

	/**
	 * Gets a formatted localized message for the given key
	 *
	 * @param messageCode
	 * @param params
	 * @return String
	 */
	public static String getMessage(String messageCode, Object... params) {
		return MessageFormat.format(bundle.getString(messageCode), params);
	}

	/**
	 * Returns localized string for enumeration value
	 *
	 * @param anEnum
	 * @return String
	 */
	public String getEnum(Enum<?> anEnum) {
		return bundle.getString("enum." + anEnum.getClass().getName() + "." + anEnum.toString());
	}

	/**
	 * Returns localized message for {@link ServiceException}
	 *
	 * @param e
	 * @return String
	 */
	public static String getMessage(ServiceException e) {
		return MessageFormat.format(bundle.getString("serviceException." + e.getCode()), e.getParams());
	}
}
