package org.perfrepo.web.service;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Named;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Implements @link{EmailService}.
 *
 * @author Jakub Markos <jmarkos@redhat.com>
 */
@Named
@Stateless
public class EmailServiceBean implements EmailService {

   @Resource(mappedName = "java:jboss/mail/redhatsmtp")
   private Session emailSession;

   @Override
   public void sendEmail(String recipient, String subject, String body) throws MessagingException {
      Message message = new MimeMessage(emailSession);
      message.setFrom(new InternetAddress(EmailService.PERFREPOEMAIL));
      Address toAddress = new InternetAddress(recipient);
      message.addRecipient(Message.RecipientType.TO, toAddress);
      message.setSubject(subject);
      message.setContent(body, "text/plain");
      Transport.send(message);
   }
}
