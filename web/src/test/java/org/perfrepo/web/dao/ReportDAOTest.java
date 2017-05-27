package org.perfrepo.web.dao;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.perfrepo.enums.report.ReportType;
import org.perfrepo.web.model.report.Report;
import org.perfrepo.web.model.user.User;
import org.perfrepo.web.util.TestUtils;

import javax.inject.Inject;
import javax.transaction.Status;
import javax.transaction.UserTransaction;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link ReportDAO}
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
@RunWith(Arquillian.class)
public class ReportDAOTest {

   @Inject
   private ReportDAO reportDAO;

   @Inject
   private UserDAO userDAO;

   @Inject
   private UserTransaction userTransaction;

   private User[] users;
   private Report[] reports;


   @Deployment
   public static Archive<?> createDeployment() {
      return TestUtils.createDeployment();
   }

   @Before
   public void init() throws Exception {
      userTransaction.begin();

      users = new User[] {
              userDAO.create(createUser("user1")),
              userDAO.create(createUser("user2"))
      };

      reports = new Report[] {
              reportDAO.create(createReport("report1", users[0])),
              reportDAO.create(createReport("report2", users[0])),
              reportDAO.create(createReport("report3", users[0])),
              reportDAO.create(createReport("report4", users[1]))
      };

      userTransaction.commit();
      userTransaction.begin();
   }

   @After
   public void cleanUp() throws Exception {
      if (userTransaction.getStatus() == Status.STATUS_ACTIVE) {
         userTransaction.commit();
      } else {
         userTransaction.rollback();
      }

      userTransaction.begin();

      reportDAO.getAll().forEach(reportDAO::remove);
      userDAO.getAll().forEach(userDAO::remove);

      userTransaction.commit();
   }

   private User createUser(String prefix) {
      User user = new User();
      user.setFirstName(prefix + "_first_name");
      user.setLastName(prefix + "_last_name");
      user.setEmail(prefix + "@email.com");
      user.setUsername(prefix + "_username");
      user.setPassword(prefix + "_password");

      return user;
   }

   private Report createReport(String prefix, User user) {
      Report report = new Report();
      report.setName(prefix + "_name");
      report.setDescription(prefix + "_description");
      report.setType(ReportType.METRIC_HISTORY);
      report.setUser(user);

      return report;
   }

}
