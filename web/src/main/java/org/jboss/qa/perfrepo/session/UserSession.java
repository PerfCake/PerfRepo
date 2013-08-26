package org.jboss.qa.perfrepo.session;

import java.util.ArrayList;
import java.util.HashMap;
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

   @PostConstruct
   public void init() {
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
      } else {
         if (user.getProperties() != null) {
            for (UserProperty prop : user.getProperties()) {
               userProperties.put(prop.getName(), prop.getValue());
            }
         }
      }
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
               if (value == null || !value.equals(existingProp.getValue())) {
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
