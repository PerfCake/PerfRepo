package org.jboss.qa.perfrepo.controller;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.qa.perfrepo.model.user.User;
import org.jboss.qa.perfrepo.service.UserService;
import org.jboss.qa.perfrepo.session.UserSession;
import org.jboss.qa.perfrepo.viewscope.ViewScoped;

/**
 * Backing bean user profile.
 * 
 * @author Michal Linhard (mlinhard@redhat.com)
 * 
 */
@Named
@ViewScoped
public class UserProfileController extends BaseController {

   @Inject
   private UserService userService;

   @Inject
   private UserSession userSession;

   private User user;

   /**
    * called on preRenderView
    */
   public void preRender() throws Exception {
      reloadSessionMessages();
      user = userService.getFullUser(userSession.getUser().getId());
   }

   public User getUser() {
      return user;
   }

}
