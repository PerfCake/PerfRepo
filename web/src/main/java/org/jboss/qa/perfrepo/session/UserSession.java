package org.jboss.qa.perfrepo.session;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.jboss.qa.perfrepo.controller.ControllerBase;
import org.jboss.qa.perfrepo.model.User;
import org.jboss.qa.perfrepo.security.UserInfo;
import org.jboss.qa.perfrepo.service.UserService;
import org.jboss.qa.perfrepo.model.FavoriteParameter;

/**
 * Holds information about user.
 * 
 * @author Michal Linhard (mlinhard@redhat.com)
 * 
 */
@Named(value = "userSession")
@SessionScoped
public class UserSession implements Serializable{

   @Inject
   private UserService userService;

   @Inject
   private UserInfo userInfo;

   private User user;

   @PostConstruct
   public void init() {
      refresh();
   }

   public void refresh() {
      user = userService.getFullUser(userInfo.getUserId());
   }

   public User getUser() {
      return user;
   }
}
