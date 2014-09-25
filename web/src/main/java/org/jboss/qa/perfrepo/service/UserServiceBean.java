package org.jboss.qa.perfrepo.service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.qa.perfrepo.dao.FavoriteParameterDAO;
import org.jboss.qa.perfrepo.dao.TestDAO;
import org.jboss.qa.perfrepo.dao.UserDAO;
import org.jboss.qa.perfrepo.dao.UserPropertyDAO;
import org.jboss.qa.perfrepo.model.FavoriteParameter;
import org.jboss.qa.perfrepo.model.Test;
import org.jboss.qa.perfrepo.model.UserProperty;
import org.jboss.qa.perfrepo.model.user.Group;
import org.jboss.qa.perfrepo.model.user.User;
import org.jboss.qa.perfrepo.model.util.EntityUtils;
import org.jboss.qa.perfrepo.service.exceptions.ServiceException;

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
   private TestDAO testDAO;

   @Inject
   private FavoriteParameterDAO favoriteParameterDAO;

   @Resource
   private SessionContext sessionContext;

   @Override
   public User createUser(User user) throws ServiceException {
      if (user.getId() != null) {
         throw new IllegalArgumentException("can't create with id");
      }
      if (!user.getId().equals(getLoggedUserId())) {
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
      List<UserProperty> ups = userPropertyDAO.findByUserId(getLoggedUserId());
      return transformToMap(ups);
   }

   @Override
   public void storeProperties(String prefix, Map<String, String> properties) {
      User user = getFullUser(getLoggedUserId());
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
   public void addFavoriteParameter(Test test, String paramName, String label) throws ServiceException {
      User user = userDAO.find(getLoggedUserId());
      Test testEntity = testDAO.find(test.getId());

      FavoriteParameter fp = favoriteParameterDAO.findByTestAndParamName(paramName, test.getId(), getLoggedUserId());
      if(fp == null) {
         fp = new FavoriteParameter();
      }

      fp.setLabel(label);
      fp.setParameterName(paramName);
      fp.setTest(testEntity);
      fp.setUser(user);

      favoriteParameterDAO.create(fp);
   }

   @Override
   public void removeFavoriteParameter(Test test, String paramName) throws ServiceException {
      FavoriteParameter fp = favoriteParameterDAO.findByTestAndParamName(paramName, test.getId(), getLoggedUserId());

      if(fp != null) {
         favoriteParameterDAO.delete(fp);
      }
   }

   @Override
   public User getFullUser(String userName) {
      User user = userDAO.findByUsername(userName);
      if (user == null) {
         return null;
      }

      List<UserProperty> properties = userPropertyDAO.findByUserId(user.getId());
      Collection<FavoriteParameter> favoriteParameters = user.getFavoriteParameters();

      User clonedUser = user.clone();
      clonedUser.setProperties(EntityUtils.clone(properties));
      clonedUser.setFavoriteParameters(EntityUtils.clone(favoriteParameters));

      return user;
   }

   @Override
   public User getUser(Long id) {
      return userDAO.find(id);
   }

   @Override
   public User getLoggedUser() {
	   Principal principal = sessionContext.getCallerPrincipal();
	   User user = null;
	   if (principal != null) {
		   user = userDAO.findByUsername(principal.getName());
	   }
	   return user;
   }

   @Override
   public String getLoggedUserName() {
	   Principal principal = sessionContext.getCallerPrincipal();
	   if (principal != null) {
		   return principal.getName();
	   }
	   return null;
   }

   public Long getLoggedUserId() {
	   User user = getLoggedUser();
	   if (user != null) {
		   return user.getId();
	   }
	   return null;
   }

   public boolean isLoggedUserInGroup(String guid) {
	   User user = getLoggedUser();
	   if (user != null && user.getGroups() != null) {
         for(Group group: user.getGroups()) {
            if(group.getName().equals(guid)) {
               return true;
            }
         }
	   }
	   return false;
   }

   @Override
   public User getFullUser(Long id) {
      User user = userDAO.find(id);
      if (user == null) {
         return null;
      }

      List<UserProperty> properties = userPropertyDAO.findByUserId(user.getId());
      Collection<FavoriteParameter> favoriteParameters = user.getFavoriteParameters();

      User clonedUser = user.clone();
      clonedUser.setProperties(EntityUtils.clone(properties));
      clonedUser.setFavoriteParameters(EntityUtils.clone(favoriteParameters));

      return user;
   }

   @Override
   public List<FavoriteParameter> getFavoriteParametersForTest(Test test) {
      User user = getUser(getLoggedUserId());
      List<FavoriteParameter> result = new ArrayList<FavoriteParameter>();
      for(FavoriteParameter favoriteParameter: user.getFavoriteParameters()) {
         if(favoriteParameter.getTest().getId().equals(test.getId())) {
            result.add(favoriteParameter);
         }
      }

      return result;
   }

   private Map<String, String> transformToMap(List<UserProperty> ups) {
      Map<String, String> map = new HashMap<String, String>();
      for (UserProperty up : ups) {
         map.put(up.getName(), up.getValue());
      }
      return map;
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
      if (!oldUser.getId().equals(getLoggedUserId())) {
         throw new ServiceException(ServiceException.Codes.NOT_YOU, null, "Only logged-in user can change his own properties");
      }
      return oldUser;
   }
}
