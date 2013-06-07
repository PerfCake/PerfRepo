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
