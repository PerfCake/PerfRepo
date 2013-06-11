package org.jboss.qa.perfrepo.rest;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;

@Logged
@Interceptor
public class LoggingInterceptor {
   private static final Logger log = Logger.getLogger("org.jboss.qa.perfrepo.REST");

   @AroundInvoke
   public Object logInvocation(InvocationContext context) throws Exception {
      try {
         return context.proceed();
      } catch (SecurityException e) {
         log.error("Error in REST service", e);
         return Response.status(Status.UNAUTHORIZED).entity(e.getMessage()).build();
      } catch (Exception e) {
         log.error("Error in REST service", e);
         return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
      }
   }
}
