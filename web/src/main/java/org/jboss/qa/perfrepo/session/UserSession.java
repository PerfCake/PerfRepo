package org.jboss.qa.perfrepo.session;

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
import org.jboss.qa.perfrepo.model.UserProperty;
import org.jboss.qa.perfrepo.security.UserInfo;
import org.jboss.qa.perfrepo.service.ServiceException;
import org.jboss.qa.perfrepo.service.TestService;
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

   private static final String FAV_PARAM_KEY_PREFIX = "fav.param.";

   @PostConstruct
   public void init() {
      refreshUser();
   }

   public User refreshUser() {
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
            if (prop.getName().startsWith(FAV_PARAM_KEY_PREFIX)) {
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

   private String favPropKey(long testId, String paramName) {
      return FAV_PARAM_KEY_PREFIX + testId + "." + paramName;
   }

   public void addFavoriteParameter(long testId, String paramName, String label) {
      FavoriteParameter fp = new FavoriteParameter();
      fp.setLabel(label);
      fp.setParameterName(paramName);
      fp.setTestId(testId);
      setProperty(favPropKey(testId, paramName), fp.toString());
      FavoriteParameter prev = findFavoriteParameter(testId, paramName);
      if (prev != null) {
         prev.setLabel(label);
      } else {
         favoriteParameters.add(fp);
      }
   }

   public void removeFavoriteParameter(long testId, String paramName) {
      FavoriteParameter prev = findFavoriteParameter(testId, paramName);
      if (prev != null) {
         favoriteParameters.remove(prev);
      }
      setProperty(favPropKey(testId, paramName), null);
   }

   public FavoriteParameter findFavoriteParameter(long testId, String paramName) {
      for (FavoriteParameter fp : favoriteParameters) {
         if (fp.getTestId() == testId && paramName.equals(fp.getParameterName())) {
            return fp;
         }
      }
      return null;
   }

   public String getProperty(String name) {
      return userProperties.get(name);
   }

   public void setProperty(String name, String value) {
      if (user != null) {
         try {
            UserProperty existingProp = user.findProperty(name);
            if (existingProp == null && value != null) {
               UserProperty newProp = new UserProperty();
               newProp.setName(name);
               newProp.setValue(value);
               newProp.setUser(user.clone());
               newProp = testService.updateUserProperty(newProp);
               if (user.getProperties() == null) {
                  user.setProperties(new ArrayList<UserProperty>());
               }
               user.getProperties().add(newProp);
            } else {
               if (value == null) {
                  UserProperty toDelete = existingProp.clone();
                  testService.deleteUserProperty(toDelete);
                  user.getProperties().remove(existingProp);
               } else if (!value.equals(existingProp.getValue())) {
                  UserProperty toUpdate = existingProp.clone();
                  toUpdate.setValue(value);
                  toUpdate = testService.updateUserProperty(toUpdate);
                  user.getProperties().remove(existingProp);
                  user.getProperties().add(toUpdate);
               }
            }
            if (value != null) {
               userProperties.put(name, value);
            } else {
               userProperties.remove(name);
            }
         } catch (ServiceException e) {
            log.error("Error while saving property", e);
            addMessageFor(e);
         }
      }
   }

   public User getUser() {
      return user;
   }
}
