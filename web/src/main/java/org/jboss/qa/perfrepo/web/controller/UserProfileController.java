package org.jboss.qa.perfrepo.web.controller;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.qa.perfrepo.model.user.User;
import org.jboss.qa.perfrepo.web.service.UserService;
import org.jboss.qa.perfrepo.web.service.exceptions.ServiceException;
import org.jboss.qa.perfrepo.web.session.UserSession;
import org.jboss.qa.perfrepo.web.viewscope.ViewScoped;

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

   private String oldPassword;
   private String newPassword;
   private String newPasswordConfirmation;

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

   public void update() {
      if(user == null) {
         throw new IllegalArgumentException("User cannot be null.");
      }

      try {
         User updatedUser = userService.updateUser(user);
         redirectWithMessage("/profile", INFO, "page.user.updatedSuccesfully");
      }
      catch(ServiceException ex) {
         addMessage(ERROR, "page.user.errorUsernameAlreadyExists", ex.getMessage());
      }
      catch(SecurityException ex) {
         addMessage(ERROR, "page.test.errorSecurityException");
      }
   }

   public void changePassword() {
      if(user == null) {
         throw new IllegalArgumentException("User cannot be null.");
      }

      if(newPassword == null || !newPassword.equals(newPasswordConfirmation)) {
         addMessage(ERROR, "page.user.newPasswordsNotEqual");
         return;
      }

      try {
         userService.changePassword(oldPassword, newPassword);
         redirectWithMessage("/profile", INFO, "page.user.updatedSuccesfully");
      }
      catch(ServiceException ex) {
         addMessage(ERROR, "page.user.oldPasswordDoesntMatch");
      }
   }

   public void setOldPassword(String oldPassword) {
      this.oldPassword = oldPassword;
   }

   public void setNewPassword(String newPassword) {
      this.newPassword = newPassword;
   }

   public void setNewPasswordConfirmation(String newPasswordConfirmation) {
      this.newPasswordConfirmation = newPasswordConfirmation;
   }

   public String getOldPassword() {
      return oldPassword;
   }

   public String getNewPassword() {
      return newPassword;
   }

   public String getNewPasswordConfirmation() {
      return newPasswordConfirmation;
   }
}
