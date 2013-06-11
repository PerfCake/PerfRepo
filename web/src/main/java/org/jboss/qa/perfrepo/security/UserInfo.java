package org.jboss.qa.perfrepo.security;

import java.security.Principal;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Named;

@Named
@Stateless
public class UserInfo {

   @Resource
   private SessionContext sessionContext;

   public String getUserName() {
      Principal principal = sessionContext.getCallerPrincipal();
      return principal == null ? null : principal.getName();
   }

   public boolean isUserInRole(String guid) {
      return sessionContext.isCallerInRole(guid);
   }
}
