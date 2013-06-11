package org.jboss.qa.perfrepo.service;

/**
 * Exception in service layer.
 * 
 * @author Michal Linhard (mlinhard@redhat.com)
 * 
 */
public class ServiceException extends Exception {

   public ServiceException() {
      super();
   }

   public ServiceException(String message, Throwable cause) {
      super(message, cause);
   }

   public ServiceException(String message) {
      super(message);
   }

   public ServiceException(Throwable cause) {
      super(cause);
   }

}
