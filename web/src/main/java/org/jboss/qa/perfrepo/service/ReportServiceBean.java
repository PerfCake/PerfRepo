package org.jboss.qa.perfrepo.service;

import org.jboss.qa.perfrepo.model.User;
import org.jboss.qa.perfrepo.model.UserProperty;
import org.jboss.qa.perfrepo.session.UserSession;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Implements @lin{ReportService}
 *
 * @author Jiri Holusa <jholusa@redhat.com>
 */
@Named
@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ReportServiceBean implements ReportService {

   @Inject
   private TestService testService;

   @Inject
   private UserSession userSession;

   @Inject
   private UserService userService;

   @Override
   public List<String> getAllReportIds() {
      return getAllReportIds(userSession.getUserProperties());
   }

   @Override
   public void removeReport(String reportId) throws ServiceException{
      String reportPrefix = REPORT_KEY_PREFIX + reportId + ".";
      Set<String> keysToRemove = new HashSet<String>();
      for (Map.Entry<String, String> entry : userSession.getUserProperties().entrySet()) {
         if (entry.getKey().startsWith(reportPrefix)) {
            keysToRemove.add(entry.getKey());
         }
      }

      userService.multiUpdateProperties(keysToRemove, Collections.<String, String> emptyMap());
      // update userProperties collection if this didn't throw any exception
      for (String keyToRemove : keysToRemove) {
         userSession.getUserProperties().remove(keyToRemove);
      }
   }

   @Override
   public void setReportProperties(String reportId, Map<String, String> props) throws ServiceException{
      String reportPrefix = REPORT_KEY_PREFIX + reportId + ".";
      Set<String> keysToRemove = new HashSet<String>();
      Map<String, String> keysToAdd = new HashMap<String, String>();
      for (Map.Entry<String, String> entry : userSession.getUserProperties().entrySet()) {
         if (entry.getKey().startsWith(reportPrefix)) {
            keysToRemove.add(entry.getKey());
         }
      }
      for (Map.Entry<String, String> entry : props.entrySet()) {
         String translatedKey = reportPrefix + entry.getKey();
         keysToRemove.remove(translatedKey); // don't remove this, just update
         keysToAdd.put(translatedKey, entry.getValue());
      }

      userService.multiUpdateProperties(keysToRemove, keysToAdd);
      // update userProperties collection if this didn't throw any exception
      for (String keyToRemove : keysToRemove) {
         userSession.getUserProperties().remove(keyToRemove);
      }
      userSession.getUserProperties().putAll(keysToAdd);
   }

   @Override
   public Map<String, String> getReportProperties(String reportId) {
      return getReportProperties(userSession.getUserProperties(), reportId);
   }

   @Override
   public Map<String, String> getReportProperties(String userName, String reportId) {
      User user = userService.getFullUser(userName);
      if (user == null) {
         return null;
      } else {
         return getReportProperties(getUserProperties(user), reportId);
      }
   }

   private Map<String, String> getUserProperties(User user) {
      Map<String, String> userProperties = new HashMap<String, String>();
      for (UserProperty prop : user.getProperties()) {
         userProperties.put(prop.getName(), prop.getValue());
      }
      return userProperties;
   }

   private List<String> getAllReportIds(Map<String, String> userProperties) {
      Set<String> rset = new HashSet<String>();
      List<String> r = new ArrayList<String>();
      for (Map.Entry<String, String> entry : userProperties.entrySet()) {
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

   private Map<String, String> getReportProperties(Map<String, String> userProperties, String reportId) {
      String reportPrefix = REPORT_KEY_PREFIX + reportId + ".";
      Map<String, String> reportProperties = new HashMap<String, String>();
      for (Map.Entry<String, String> entry : userProperties.entrySet()) {
         if (entry.getKey().startsWith(reportPrefix)) {
            reportProperties.put(entry.getKey().substring(reportPrefix.length()), entry.getValue());
         }
      }
      return reportProperties;
   }
}
