package org.jboss.qa.perfrepo.security;

import java.security.Principal;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;

@RequestScoped
public class UserInfo {
   
   public String getUserName() {      
      Principal principal = FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal();
      if (principal != null) {
         return principal.getName();
      }
      return null;
   }
   
   public boolean isUserInRole(String guid) {
      return FacesContext.getCurrentInstance().getExternalContext().isUserInRole(guid);
   }

}
