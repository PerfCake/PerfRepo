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

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.jboss.qa.perfrepo.auth.AuthorizationService;
import org.jboss.qa.perfrepo.model.Entity;
import org.jboss.qa.perfrepo.model.auth.SecuredEntity;
import org.jboss.qa.perfrepo.util.MessageUtils;

@Secure
@Interceptor
public class SecurityInterceptor {

	@Inject
	private AuthorizationService authorizationService;

	@AroundInvoke
	public Object invoke(InvocationContext ctx) throws Exception {
		Object[] params = ctx.getParameters();
		Secure secureAnnotation = ctx.getMethod().getAnnotation(Secure.class);
		for (Object param : params) {
			if (param.getClass().getAnnotation(SecuredEntity.class) != null && param instanceof Entity<?>) {
				if (!authorizationService.isUserAuthorizedFor(secureAnnotation.accessType(), (Entity<?>)param)) {
					throw new SecurityException(MessageUtils.getMessage("securityException.101"));
				}
			}
		}
		return ctx.proceed();
	}
}
