package org.perfrepo.web.service;

import org.perfrepo.web.model.Alert;
import org.perfrepo.web.model.TestExecution;

import java.util.List;
import java.util.Map;

/**
 * Generic interface for how to react when some alert on test fails.
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public interface AlertingReporterService {

   /**
    * Suppose that the alert has failed. Calling this method will trigger appropriate procedure of reporting the failed
    * alert, e.g. send an email, log it into file etc. Depends on the implementation.
    *
    * @param alerts
    * @param testExecution
    */
   public void reportAlert(List<Alert> alerts, TestExecution testExecution);

   /**
    * Allows to pass the values of variables used in the condition that failed (for every alert). This can be used for
    * creating more user-friendly alert messages.
    *
    * @param variables
    */
   public void setConditionVariables(Map<Alert, Map<String, Object>> variables);

}
