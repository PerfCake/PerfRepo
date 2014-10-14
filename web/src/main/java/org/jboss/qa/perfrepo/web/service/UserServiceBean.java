package org.jboss.qa.perfrepo.web.service;

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

import org.jboss.qa.perfrepo.web.dao.FavoriteParameterDAO;
import org.jboss.qa.perfrepo.web.dao.TestDAO;
import org.jboss.qa.perfrepo.web.dao.UserDAO;
import org.jboss.qa.perfrepo.web.dao.UserPropertyDAO;
import org.jboss.qa.perfrepo.model.FavoriteParameter;
import org.jboss.qa.perfrepo.model.Test;
import org.jboss.qa.perfrepo.model.UserProperty;
import org.jboss.qa.perfrepo.model.user.Group;
import org.jboss.qa.perfrepo.model.user.User;
import org.jboss.qa.perfrepo.model.util.EntityUtils;
import org.jboss.qa.perfrepo.web.service.exceptions.ServiceException;

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
         throw new IllegalArgumentException("Can't create with id");
      }
      if (!user.getId().equals(getLoggedUser().getId())) {
         throw new ServiceException(ServiceException.Codes.NOT_YOU, null, "Only logged-in user can change his own properties");
      }
      User newUser = userDAO.create(user).clone();
      newUser.setProperties(new ArrayList<UserProperty>(0));
      return newUser;
   }

   @Override
   public Map<String, String> getUserProperties() {
      List<UserProperty> ups = userPropertyDAO.findByUserId(getLoggedUser().getId());
      return transformToMap(ups);
   }

   @Override
   public void addFavoriteParameter(Test test, String paramName, String label) throws ServiceException {
      User user = userDAO.get(getLoggedUser().getId());
      Test testEntity = testDAO.get(test.getId());

      FavoriteParameter fp = favoriteParameterDAO.findByTestAndParamName(paramName, test.getId(), getLoggedUser().getId());
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
      FavoriteParameter fp = favoriteParameterDAO.findByTestAndParamName(paramName, test.getId(), getLoggedUser().getId());

      if(fp != null) {
         favoriteParameterDAO.remove(fp);
      }
   }

   @Override
   public User getFullUser(Long id) {
      User user = userDAO.get(id);
      if (user == null) {
         return null;
      }

      Collection<UserProperty> properties = userPropertyDAO.findByUserId(user.getId());
      Collection<FavoriteParameter> favoriteParameters = user.getFavoriteParameters();
      Collection<Group> groups = user.getGroups();

      User clonedUser = user.clone();
      clonedUser.setProperties(EntityUtils.clone(properties));
      clonedUser.setFavoriteParameters(EntityUtils.clone(favoriteParameters));
      clonedUser.setGroups(EntityUtils.clone(groups));

      return clonedUser;
   }

   @Override
   public User getUser(Long id) {
      return userDAO.get(id);
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
   public boolean isUserInGroup(Long userId, Long groupId) {
	   User user = getUser(userId);
	   if (user != null && user.getGroups() != null) {
		   for(Group group: user.getGroups()) {
			   if (groupId.equals(group.getId())) {
				   return true;
			   }
		   }
	   }
	   return false;
   }

   @Override
   public List<FavoriteParameter> getFavoriteParametersForTest(Test test) {
      User user = getLoggedUser();
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
}
