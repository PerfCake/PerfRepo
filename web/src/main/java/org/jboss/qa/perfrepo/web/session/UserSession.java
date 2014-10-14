package org.jboss.qa.perfrepo.web.session;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.qa.perfrepo.model.user.User;
import org.jboss.qa.perfrepo.web.service.UserService;

/**
 * Holds information about user.
 * 
 * @author Michal Linhard (mlinhard@redhat.com)
 * 
 */
@Named(value = "userSession")
@SessionScoped
public class UserSession implements Serializable{

   private static final long serialVersionUID = 1487959021438612784L;

   @Inject
   private UserService userService;

   private User user;

   @PostConstruct
   public void init() {
      refresh();
   }

   public void refresh() {
      user = userService.getFullUser(userService.getLoggedUser().getId());
   }

   public User getUser() {
      return user;
   }

   public String logout() {
	  FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
	  return "HomeRedirect";
   }

}
