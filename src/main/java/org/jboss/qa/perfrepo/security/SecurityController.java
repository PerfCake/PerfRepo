package org.jboss.qa.perfrepo.security;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

@Named
@SessionScoped
public class SecurityController implements Serializable {

   private static final long serialVersionUID = 1L;
   
   public boolean isAdministrator() {
      return FacesContext.getCurrentInstance().getExternalContext().isUserInRole("perfrepoadmin");
   }

   public String logout() {
      FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
      return "Home";
   }
}
