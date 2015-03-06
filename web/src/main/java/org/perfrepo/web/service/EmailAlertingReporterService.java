package org.perfrepo.web.service;

import org.perfrepo.model.Alert;
import org.perfrepo.model.TestExecution;
import org.perfrepo.model.user.User;
import org.perfrepo.web.dao.UserDAO;

import javax.inject.Inject;
import javax.mail.MessagingException;
import java.util.List;

/**
 * Implementation of {@link AlertingReporterService} that react to the failed alert
 * by sending an email to every subscribed user.
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public class EmailAlertingReporterService implements AlertingReporterService {

   @Inject
   private Mailer mailer;

   @Inject
   private UserDAO userDAO;

   @Override
   public void reportAlert(List<Alert> alerts, TestExecution testExecution) {
      if(alerts == null || alerts.isEmpty()) {
         return;
      }

      List<User> subscribers = userDAO.findSubscribersForTest(testExecution.getTest().getId());

      String message = composeMessage(alerts, testExecution);
      String subject = composeSubject(testExecution);
      for(User subscriber: subscribers) {
         try {
            mailer.sendEmail(subscriber.getEmail(), subject, message);
         } catch (MessagingException e) {
            e.printStackTrace();  // TODO: handle this properly
         }
      }
   }

   private String composeSubject(TestExecution testExecution) {
      return "PerfRepo - some alerts on test " + testExecution.getTest().getName() + " failed.";
   }

   private String composeMessage(List<Alert> alerts, TestExecution testExecution) {
      StringBuilder message = new StringBuilder();

      message.append("Hello,\n\n");
      message.append("after uploading of results of test execution of test " + testExecution.getTest().getName());
      message.append(" with following info: \n\n");

      message.append("Test execution ID: " + testExecution.getId() + "\n");
      message.append("Test execution name: " + testExecution.getName() + "\n");
      message.append("Execution date: " + testExecution.getStarted() + "\n");

      message.append("\n");

      message.append("some of the alerts conditions failed. ");
      message.append("Alerts that failed: \n\n");
      for(Alert alert: alerts) {
         message.append("Alert name: " + alert.getName() + "\n");
      }

      message.append("\n");

      message.append("It's possible that regression occurred. Please, take a look at this test execution. \n\n");
      message.append("Sincerely yours,\n");
      message.append("PerfRepo Alerting");

      return message.toString();
   }
}
