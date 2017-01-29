package org.perfrepo.web.service;

import org.perfrepo.web.model.Alert;
import org.perfrepo.web.model.Metric;
import org.perfrepo.web.model.Test;
import org.perfrepo.web.model.TestExecution;

import java.util.List;

/**
 * Service dedicated to handle alerting feature.
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public interface AlertingService {

   /**
    * Returns alert by id.
    *
    * @param id
    * @return
    */
   Alert getAlert(Long id);

   /**
    * Creates new alert.
    *
    * @param alert
    * @return
    */
   Alert createAlert(Alert alert);

   /**
    * Updates existing alert.
    *
    * @param alert
    * @return
    */
   Alert updateAlert(Alert alert);

   /**
    * Removes existing alert.
    *
    * @param alert
    */
   void removeAlert(Alert alert);

   /**
    * Retrieves all alerts assigned to test provided.
    *
    * @param test
    * @return
    */
   List<Alert> getAlertsList(Test test);

   /**
    * Goes through all alerts that are valid for this test execution and checks if they passed. Basically fulfills the
    * alerting feature.
    *
    * @param testExecution
    */
   void processAlerts(TestExecution testExecution);

   /**
    * Parses the condition string and checks for all the syntax errors.
    *
    * @param condition
    * @param metric
    */
   void checkConditionSyntax(String condition, Metric metric);
}
