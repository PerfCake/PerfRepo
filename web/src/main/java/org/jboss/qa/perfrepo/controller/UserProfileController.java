package org.jboss.qa.perfrepo.controller;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.qa.perfrepo.model.User;
import org.jboss.qa.perfrepo.security.UserInfo;
import org.jboss.qa.perfrepo.service.TestService;
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
public class UserProfileController extends ControllerBase {

   @Inject
   private UserService userService;

   @Inject
   private UserInfo userInfo;

   private User user;

   /**
    * called on preRenderView
    */
   public void preRender() throws Exception {
      reloadSessionMessages();
      user = userService.getFullUser(userInfo.getUserName());
   }

   public User getUser() {
      return user;
   }

}
