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
package org.perfrepo.web.util;

import org.perfrepo.web.service.exceptions.ServiceException;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * MessageUtils class for creating localized messages by the message code
 *
 * @author Pavel Drozd
 */
public class MessageUtils {

   private static ResourceBundle bundle = ResourceBundle.getBundle("lang.strings");

   private MessageUtils() { }

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
   public static String getEnum(Enum<?> anEnum) {
      return bundle.getString("enum." + anEnum.getClass().getName() + "." + anEnum.toString());
   }

   /**
    * Returns localized message for {@link ServiceException}
    *
    * @param e
    * @return String
    */
   public static String getMessage(ServiceException e) {
      return MessageFormat.format(bundle.getString(e.getKeyToResourceBundle()), e.getParams());
   }

   /**
    * Returns localized message for {@link org.perfrepo.web.security.SecurityException}
    *
    * @param e
    * @return String
    */
   public static String getMessage(org.perfrepo.web.security.SecurityException e) {
      return MessageFormat.format(bundle.getString(e.getKeyToResourceBundle()), e.getParams());
   }
}
