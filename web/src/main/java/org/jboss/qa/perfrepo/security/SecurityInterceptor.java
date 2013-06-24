/* 
 * Copyright 2013 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.qa.perfrepo.security;

import java.lang.reflect.Field;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.qa.perfrepo.model.Test;


@Secure @Interceptor
public class SecurityInterceptor {
   
   private static final String FIND_TEST_ID = "FIND_TEST_ID";
   
   
   @Inject
   private EntityManager em;
   
   @Inject
   UserInfo userInfo;
   
     
   @AroundInvoke
   public Object invoke(InvocationContext ctx) throws Exception {
       Object[] params = ctx.getParameters();       
       for (Object param : params) {
          if (param.getClass().getAnnotation(Entity.class) != null) {
             String guid = getGroupId(param);
             if (guid != null) {
                if (!userInfo.isUserInRole(guid)) {
                   throw new SecurityException();
                }
             }
          }
       }
       return ctx.proceed();
   }

   public String getGroupId(Object entity) throws Exception {     
      Field queryName = entity.getClass().getDeclaredField(FIND_TEST_ID);
      if (queryName != null) {
         String query = (String)queryName.get(String.class);
         Query q = em.createNamedQuery(query);
         q.setParameter("entity", entity);
         return ((Test)q.getSingleResult()).getGroupId();
      }
      return null;     
   }
}
