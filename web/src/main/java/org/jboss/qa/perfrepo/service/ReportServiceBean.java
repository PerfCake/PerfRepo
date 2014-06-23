package org.jboss.qa.perfrepo.service;

import org.jboss.qa.perfrepo.dao.ReportDAO;
import org.jboss.qa.perfrepo.model.User;
import org.jboss.qa.perfrepo.model.UserProperty;
import org.jboss.qa.perfrepo.model.report.Report;
import org.jboss.qa.perfrepo.security.UserInfo;
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
 * Implements @link{ReportService}.
 *
 * @author Jiri Holusa <jholusa@redhat.com>
 */
@Named
@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ReportServiceBean implements ReportService {

   @Inject
   private UserInfo userInfo;

   @Inject
   private ReportDAO reportDAO;

   @Override
   public List<Report> getAllUsersReports() {
      return getAllReports(userInfo.getUserName());
   }

   @Override
   public void removeReport(Long id) throws ServiceException {
      Report report = reportDAO.find(id);
      reportDAO.delete(report);
   }

   private List<Report> getAllReports(String username) {
      List<Report> result = new ArrayList<Report>();
      result.addAll(reportDAO.findTestsByUser(username));

      return result;
   }

   @Override
   public Report createReport(Report report) {
      return reportDAO.create(report);
   }

   @Override
   public Report updateReport(Report report) {
      return reportDAO.update(report);
   }

   @Override
   public Report getReport(Long id) {
      return reportDAO.find(id);
   }

   @Override
   public Long getMaxId() {
      return reportDAO.findMaxId();
   }
}
