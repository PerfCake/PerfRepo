package org.jboss.qa.perfrepo.service;

import org.jboss.qa.perfrepo.dao.UserDAO;
import org.jboss.qa.perfrepo.dao.UserPropertyDAO;
import org.jboss.qa.perfrepo.model.User;
import org.jboss.qa.perfrepo.model.UserProperty;
import org.jboss.qa.perfrepo.model.util.EntityUtil;
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
import java.util.Collection;
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
   private TestService testService;

   @Override
   public User createUser(User user) throws ServiceException {
      if (user.getId() != null) {
         throw new IllegalArgumentException("can't create with id");
      }
      if (!user.getUsername().equals(userInfo.getUserName())) {
         throw new ServiceException(ServiceException.Codes.NOT_YOU, null, "Only logged-in user can change his own properties");
      }
      User newUser = userDAO.create(user).clone();
      newUser.setProperties(new ArrayList<UserProperty>(0));
      return newUser;
   }

   @Override
   public User updateUser(User user) throws ServiceException {
      User oldUser = checkThisUser(user);
      // currently you can update only e-mail
      oldUser.setEmail(user.getEmail());
      return userDAO.update(oldUser);
   }

   @Override
   public Map<String, String> getUserProperties() {
      List<UserProperty> ups = userPropertyDAO.findByUserName(userInfo.getUserName());
      return transformToMap(ups);
   }

   @Override
   public void storeProperties(String prefix, Map<String, String> properties) {
      User user = getFullUser(userInfo.getUserName());
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

   @Override
   public List<FavoriteParameter> getFavoriteParameters() {
      List<UserProperty> userProperties = userPropertyDAO.findByUserName(userInfo.getUserName());

      List<FavoriteParameter> favoriteParameters = new ArrayList<FavoriteParameter>();
      if (userProperties != null) {
         for (UserProperty prop : userProperties) {
            if (prop.getName().startsWith(UserService.FAV_PARAM_KEY_PREFIX)) {
               favoriteParameters.add(FavoriteParameter.fromString(prop.getValue()));
            }
         }
      }

      return favoriteParameters;
   }

   @Override
   public void addFavoriteParameter(long testId, String paramName, String label) throws ServiceException {
      User user = userDAO.findByUsername(userInfo.getUserName());

      FavoriteParameter fp = new FavoriteParameter();
      fp.setLabel(label);
      fp.setParameterName(paramName);
      fp.setTestId(testId);


      setProperty(favPropKey(testId, paramName), fp.toString(), user);
   }

   @Override
   public void removeFavoriteParameter(long testId, String paramName) throws ServiceException {
      User user = userDAO.findByUsername(userInfo.getUserName());
      setProperty(favPropKey(testId, paramName), null, user);
   }

   @Override
   public User getFullUser(String userName) {
      User user = userDAO.findByUsername(userName);
      if (user == null) {
         return null;
      }
      user = user.clone();
      List<UserProperty> properties = userPropertyDAO.findByUserId(user.getId());
      user.setProperties(EntityUtil.clone(properties));
      return user;
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

   private String favPropKey(long testId, String paramName) {
      return FAV_PARAM_KEY_PREFIX + testId + "." + paramName;
   }

   private void setProperty(String name, String value, User user) throws ServiceException {
      if (user != null) {
         UserProperty existingProp = user.findProperty(name);
         if (existingProp == null && value != null) {
            UserProperty newProp = new UserProperty();
            newProp.setName(name);
            newProp.setValue(value);
            newProp.setUser(user.clone());
            newProp = updateUserProperty(newProp);
            if (user.getProperties() == null) {
               user.setProperties(new ArrayList<UserProperty>());
            }
            user.getProperties().add(newProp);
         } else {
            if (value == null) {
               UserProperty toDelete = existingProp.clone();
               deleteUserProperty(toDelete);
               user.getProperties().remove(existingProp);
            } else if (!value.equals(existingProp.getValue())) {
               UserProperty toUpdate = existingProp.clone();
               toUpdate.setValue(value);
               toUpdate = updateUserProperty(toUpdate);
               user.getProperties().remove(existingProp);
               user.getProperties().add(toUpdate);
            }
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

   private void deleteUserProperty(UserProperty property) throws ServiceException {
      checkThisUser(property.getUser());
      UserProperty property2 = userPropertyDAO.find(property.getId());
      if (property2 != null) {
         userPropertyDAO.delete(property2);
      }
   }

   private UserProperty updateUserProperty(UserProperty property) throws ServiceException {
      User user = checkThisUser(property.getUser());
      property.setUser(user);
      if (property.getId() == null) {
         return userPropertyDAO.create(property);
      } else {
         return userPropertyDAO.update(property);
      }
   }

   private UserProperty createUserProperty(User user, String name, String value) {
      UserProperty up = new UserProperty();
      up.setUser(user);
      up.setName(name);
      up.setValue(value);
      return up;
   }

   private User checkThisUser(User user) throws ServiceException {
      if (user == null || user.getId() == null) {
         throw new IllegalArgumentException("user id required");
      }
      User oldUser = userDAO.find(user.getId());
      if (oldUser == null) {
         throw new ServiceException(ServiceException.Codes.USER_NOT_FOUND, null, "Couldn't find user with ID " + user.getId());
      }
      if (!oldUser.getUsername().equals(userInfo.getUserName())) {
         throw new ServiceException(ServiceException.Codes.NOT_YOU, null, "Only logged-in user can change his own properties");
      }
      return oldUser;
   }
}
