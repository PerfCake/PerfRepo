package org.jboss.qa.perfrepo.controller;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.qa.perfrepo.model.User;
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
   private UserSession userSession;

   /**
    * called on preRenderView
    */
   public void preRender() throws Exception {
      reloadSessionMessages();
   }

   public User getUser() {
      return userSession.getUser();
   }
}
