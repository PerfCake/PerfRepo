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

   @Inject
   private UserService userService;

   private Map<String, String> userProperties = new HashMap<String, String>();

   private List<FavoriteParameter> favoriteParameters = new ArrayList<FavoriteParameter>();

   @PostConstruct
   public void init() {
      refresh();
   }

   public void refresh() {
      userProperties = userService.getUserProperties();
      favoriteParameters = userService.getFavoriteParameters();
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

   public Map<String, String> getUserProperties() {
      return userProperties;
   }
}
