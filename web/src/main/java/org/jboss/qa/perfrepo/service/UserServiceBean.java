package org.jboss.qa.perfrepo.service;

import org.jboss.qa.perfrepo.dao.UserDAO;
import org.jboss.qa.perfrepo.dao.UserPropertyDAO;
import org.jboss.qa.perfrepo.model.User;
import org.jboss.qa.perfrepo.model.UserProperty;
import org.jboss.qa.perfrepo.security.UserInfo;
import org.jboss.qa.perfrepo.session.UserSession;
import org.jboss.qa.perfrepo.util.FavoriteParameter;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Named
@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class UserServiceBean implements UserService {

   @Inject
   private UserDAO userDAO;

   @Inject
   private UserPropertyDAO userPropertyDAO;

   @Inject
   private UserInfo userInfo;

   @Inject
   private UserSession userSession;

   @Inject
   private TestService testService;

   public Map<String, String> getUserProperties() {
      List<UserProperty> ups = userPropertyDAO.findByUserName(userInfo.getUserName());
      return transformToMap(ups);
   }

   public boolean userPropertiesPrefixExists(String prefix) {
      List<UserProperty> ups = userPropertyDAO.findByUserName(userInfo.getUserName(), prefix);
      return ups.size() > 0;
   }

   public Map<String, String> getUserProperties(String prefix) {
      List<UserProperty> ups = userPropertyDAO.findByUserName(userInfo.getUserName(), prefix);
      return transformToMap(ups, prefix);
   }

   public void storeProperties(Map<String, String> properties) {
      storeProperties("", properties);
   }

   public void storeProperties(String prefix, Map<String, String> properties) {
      User user = getCurrentUser();
      for (String key : properties.keySet()) {
         UserProperty up = userPropertyDAO.findByUserIdAndName(user.getId(), prefix + key);
         if (up != null) {
            up.setValue(properties.get(key));
            userPropertyDAO.update(up);
         } else {
            UserProperty p = new UserProperty();
            p.setName(prefix + key);
            p.setUser(user);
            p.setValue(properties.get(key));
            userPropertyDAO.create(p);
         }
      }
   }

   public void addFavoriteParameter(long testId, String paramName, String label, User user) throws ServiceException{
      FavoriteParameter fp = new FavoriteParameter();
      fp.setLabel(label);
      fp.setParameterName(paramName);
      fp.setTestId(testId);

      setProperty(favPropKey(testId, paramName), fp.toString(), user);
      FavoriteParameter prev = findFavoriteParameter(testId, paramName, userSession.getFavoriteParameters());
      if (prev != null) {
         prev.setLabel(label);
      } else {
         userSession.getFavoriteParameters().add(fp);
      }
   }

   public void removeFavoriteParameter(long testId, String paramName, User user) throws ServiceException{
      FavoriteParameter prev = findFavoriteParameter(testId, paramName, userSession.getFavoriteParameters());
      if (prev != null) {
         userSession.getFavoriteParameters().remove(prev);
      }
      setProperty(favPropKey(testId, paramName), null, user);
   }

   public void replacePropertiesWithPrefix(String prefix, Map<String, String> properties) {
      User user = getCurrentUser();
      List<UserProperty> ups = userPropertyDAO.findByUserName(user.getUsername(), prefix);
      for (UserProperty p : ups) {
         if (!properties.containsKey(p.getName().substring(prefix.length()))) {
            userPropertyDAO.delete(p);
         }
      }
      storeProperties(prefix, properties);
   }

   private Map<String, String> transformToMap(List<UserProperty> ups) {
      Map<String, String> map = new HashMap<String, String>();
      for (UserProperty up : ups) {
         map.put(up.getName(), up.getValue());
      }
      return map;
   }

   private Map<String, String> transformToMap(List<UserProperty> ups, String namePrefix) {
      Map<String, String> map = new HashMap<String, String>();
      for (UserProperty up : ups) {
         if (up.getName().startsWith(namePrefix)) {
            map.put(up.getName().substring(namePrefix.length(), up.getName().length()), up.getValue());
         }
      }
      return map;
   }

   private User getCurrentUser() {
      return userDAO.findByUsername(userInfo.getUserName());
   }

   private String favPropKey(long testId, String paramName) {
      return FAV_PARAM_KEY_PREFIX + testId + "." + paramName;
   }

   private void setProperty(String name, String value, User user) throws ServiceException{
      if (user != null) {
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
            userSession.getUserProperties().put(name, value);
         } else {
            userSession.getUserProperties().remove(name);
         }
      }
   }

   private FavoriteParameter findFavoriteParameter(long testId, String paramName, List<FavoriteParameter> favoriteParameters) {
      for (FavoriteParameter fp : favoriteParameters) {
         if (fp.getTestId() == testId && paramName.equals(fp.getParameterName())) {
            return fp;
         }
      }
      return null;
   }



}
