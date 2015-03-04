package org.perfrepo.web.service;

import org.perfrepo.model.Alert;
import org.perfrepo.model.TestExecution;

/**
 * Generic interface for how to react when some alert on test fails.
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public interface AlertingReporterService {

   /**
    * TODO: document this
    *
    * @param alert
    * @param testExecution
    */
   public void reportAlert(Alert alert, TestExecution testExecution);

}
