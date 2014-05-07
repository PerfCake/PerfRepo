package org.jboss.qa.perfrepo.session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.jboss.qa.perfrepo.controller.ControllerBase;
import org.jboss.qa.perfrepo.model.User;
import org.jboss.qa.perfrepo.model.UserProperty;
import org.jboss.qa.perfrepo.security.UserInfo;
import org.jboss.qa.perfrepo.service.ServiceException;
import org.jboss.qa.perfrepo.service.TestService;
import org.jboss.qa.perfrepo.service.UserService;
import org.jboss.qa.perfrepo.util.FavoriteParameter;

/**
 * Holds information about user.
 * 
 * @author Michal Linhard (mlinhard@redhat.com)
 * 
 */
@Named(value = "userSession")
@SessionScoped
public class UserSession extends ControllerBase {
   private static final Logger log = Logger.getLogger(UserSession.class);

   @Inject
   private UserInfo userInfo;

   @Inject
   private TestService testService;

   private User user;

   private Map<String, String> userProperties = new HashMap<String, String>();

   private List<FavoriteParameter> favoriteParameters = new ArrayList<FavoriteParameter>();

   @PostConstruct
   public void init() {
      refreshUser();
   }

   public User refreshUser() {
	   //TODO: do not store full user with all properties
      user = testService.getFullUser(userInfo.getUserName());
      if (user == null) {
         log.error("Couldn't find user \"" + userInfo.getUserName() + "\". Creating new user entry.");
         User newUser = new User();
         newUser.setUsername(userInfo.getUserName());
         newUser.setEmail(userInfo.getUserName() + "@example.com");
         try {
            user = testService.createUser(newUser);
         } catch (ServiceException e) {
            addMessageFor(e);
         }
      }
      if (user == null) {
         return null;
      }
      if (user.getProperties() != null) {
         for (UserProperty prop : user.getProperties()) {
            userProperties.put(prop.getName(), prop.getValue());
            if (prop.getName().startsWith(UserService.FAV_PARAM_KEY_PREFIX)) {
               favoriteParameters.add(FavoriteParameter.fromString(prop.getValue()));
            }
         }
      }
      return user;
   }

   public List<FavoriteParameter> getFavoriteParametersFor(long testId) {
      List<FavoriteParameter> r = new ArrayList<FavoriteParameter>();
      for (FavoriteParameter fp : favoriteParameters) {
         if (fp.getTestId() == testId) {
            r.add(fp);
         }
      }
      return r;
   }

   public User getUser() {
      return user;
   }

   public Map<String, String> getUserProperties() {
      return userProperties;
   }

   public List<FavoriteParameter> getFavoriteParameters() {
      return favoriteParameters;
   }
}
