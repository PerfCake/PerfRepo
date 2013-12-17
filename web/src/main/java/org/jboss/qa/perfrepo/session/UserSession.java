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
   private static final String REPORT_KEY_PREFIX = "report.";

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

   private Map<String, String> getUserProperties(User user) {
      Map<String, String> userProperties = new HashMap<String, String>();
      for (UserProperty prop : user.getProperties()) {
         userProperties.put(prop.getName(), prop.getValue());
      }
      return userProperties;
   }

   public Map<String, String> getReportProperties(String userName, String reportId) {
      User user = testService.getFullUser(userName);
      if (user == null) {
         return null;
      } else {
         return getReportProperties(getUserProperties(user), reportId);
      }
   }

   private List<String> getAllReportIds(Map<String, String> userProperties) {
      Set<String> rset = new HashSet<String>();
      List<String> r = new ArrayList<String>();
      for (Entry<String, String> entry : userProperties.entrySet()) {
         if (entry.getKey().startsWith(REPORT_KEY_PREFIX)) {
            String tmpkey = entry.getKey().substring(REPORT_KEY_PREFIX.length());
            int dotidx = tmpkey.indexOf(".");
            if (dotidx == -1) {
               rset.add(tmpkey);
            } else {
               rset.add(tmpkey.substring(0, dotidx));
            }
         }
      }
      r.addAll(rset);
      return r;
   }

   public List<String> getAllReportIds() {
      return getAllReportIds(userProperties);
   }

   public void removeReport(String reportId) {
      try {
         String reportPrefix = REPORT_KEY_PREFIX + reportId + ".";
         Set<String> keysToRemove = new HashSet<String>();
         for (Entry<String, String> entry : userProperties.entrySet()) {
            if (entry.getKey().startsWith(reportPrefix)) {
               keysToRemove.add(entry.getKey());
            }
         }
         testService.multiUpdateProperties(getUser(), keysToRemove, Collections.<String, String> emptyMap());
         // update userProperties collection if this didn't throw any exception
         for (String keyToRemove : keysToRemove) {
            userProperties.remove(keyToRemove);
         }
      } catch (ServiceException e) {
         log.error("Error while removing report " + reportId, e);
         addMessageFor(e);
      }
   }

   public void setReportProperties(String reportId, Map<String, String> props) {
      try {
         String reportPrefix = REPORT_KEY_PREFIX + reportId + ".";
         Set<String> keysToRemove = new HashSet<String>();
         Map<String, String> keysToAdd = new HashMap<String, String>();
         for (Entry<String, String> entry : userProperties.entrySet()) {
            if (entry.getKey().startsWith(reportPrefix)) {
               keysToRemove.add(entry.getKey());
            }
         }
         for (Entry<String, String> entry : props.entrySet()) {
            String translatedKey = reportPrefix + entry.getKey();
            keysToRemove.remove(translatedKey); // don't remove this, just update
            keysToAdd.put(translatedKey, entry.getValue());
         }
         testService.multiUpdateProperties(getUser(), keysToRemove, keysToAdd);
         // update userProperties collection if this didn't throw any exception
         for (String keyToRemove : keysToRemove) {
            userProperties.remove(keyToRemove);
         }
         userProperties.putAll(keysToAdd);
      } catch (ServiceException e) {
         log.error("Error while setting properties for report " + reportId, e);
         addMessageFor(e);
      }
   }

   public Map<String, String> getReportProperties(String reportId) {
      return getReportProperties(userProperties, reportId);
   }

   private Map<String, String> getReportProperties(Map<String, String> userProperties, String reportId) {
      String reportPrefix = REPORT_KEY_PREFIX + reportId + ".";
      Map<String, String> reportProperties = new HashMap<String, String>();
      for (Entry<String, String> entry : userProperties.entrySet()) {
         if (entry.getKey().startsWith(reportPrefix)) {
            reportProperties.put(entry.getKey().substring(reportPrefix.length()), entry.getValue());
         }
      }
      return reportProperties;
   }
}
