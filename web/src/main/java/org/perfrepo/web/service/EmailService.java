package org.perfrepo.web.service;

import javax.mail.MessagingException;

/**
 * Email operations.
 *
 * @author Jakub Markos <jmarkos@redhat.com>
 */
public interface EmailService {

   public final String PERFREPOEMAIL = "perfrepo@redhat.com";

   /**
    *
    * @param recipient email address of the recipient
    * @param subject
    * @param body text of the email
    * @throws javax.mail.MessagingException
    */
   public void sendEmail(String recipient, String subject, String body) throws MessagingException;

}
