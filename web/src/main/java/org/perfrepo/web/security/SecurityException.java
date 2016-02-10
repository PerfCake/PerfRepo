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
package org.perfrepo.web.security;

/**
 * Exception for security related stuff
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public class SecurityException extends java.lang.SecurityException {

   private static final long serialVersionUID = 48883207192287688L;

   private String keyToResourceBundle;
   private Object[] params;

   /**
    * @param keyToResourceBundle used for GUI
    * @param params
    */
   public SecurityException(String keyToResourceBundle, String... params) {
      this(keyToResourceBundle, null, params);
   }

   /**
    * @param keyToResourceBundle used for GUI
    * @param params
    */
   public SecurityException(String keyToResourceBundle, Throwable cause, String... params) {
      super(cause);
      this.keyToResourceBundle = keyToResourceBundle;
      this.params = params;
   }

   public Object[] getParams() {
      return params;
   }

   public String getKeyToResourceBundle() {
      return keyToResourceBundle;
   }

}
