package org.jboss.qa.perfrepo.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.qa.perfrepo.dao.UserDAO;
import org.jboss.qa.perfrepo.dao.UserPropertyDAO;
import org.jboss.qa.perfrepo.model.User;
import org.jboss.qa.perfrepo.model.UserProperty;
import org.jboss.qa.perfrepo.security.UserInfo;

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
		   for (UserProperty up :ups) {
			   map.put(up.getName(), up.getValue());
		   }
		   return map;
	   }
	   
	   private Map<String, String> transformToMap(List<UserProperty> ups, String namePrefix) {
		   Map<String, String> map = new HashMap<String, String>();
		   for (UserProperty up :ups) {
			   if (up.getName().startsWith(namePrefix)) {
				   map.put(up.getName().substring(namePrefix.length(), up.getName().length()), up.getValue());
			   }
		   }
		   return map;
	   }
	   
	   private User getCurrentUser() {
		   return userDAO.findByUsername(userInfo.getUserName());
	   }

}
