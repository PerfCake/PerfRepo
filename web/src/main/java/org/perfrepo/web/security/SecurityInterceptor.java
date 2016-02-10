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

import org.apache.commons.beanutils.PropertyUtils;
import org.perfrepo.model.Entity;
import org.perfrepo.model.auth.SecuredEntity;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

@Secured
@Interceptor
public class SecurityInterceptor {

   @Inject
   private AuthorizationService authorizationService;

   @AroundInvoke
   public Object invoke(InvocationContext ctx) throws Exception {
      Object[] params = ctx.getParameters();
      Secured secureAnnotation = ctx.getMethod().getAnnotation(Secured.class);
      if (params.length > 0) {
         //just verify first attribute
         Object param = params[0];
         SecuredEntity se = param.getClass().getAnnotation(SecuredEntity.class);
         if (se != null && param instanceof Entity<?>) {
            Entity<?> entity = (Entity<?>) param;
            if (entity.getId() == null) {
               //create mode, need to verify parent entity
               entity = (Entity<?>) PropertyUtils.getProperty(entity, se.parent());
            }
            if (!authorizationService.isUserAuthorizedFor(secureAnnotation.accessType(), entity)) {
               throw new org.perfrepo.web.security.SecurityException("securityException.permissionDenied", ctx.getMethod().getName(), param.getClass().getSimpleName(), ((Entity<?>) param).getId().toString());
            }
         }
      }
      return ctx.proceed();
   }
}
