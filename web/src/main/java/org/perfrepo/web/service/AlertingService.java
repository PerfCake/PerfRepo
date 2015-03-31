package org.perfrepo.web.service;

import org.perfrepo.model.Alert;
import org.perfrepo.model.Metric;
import org.perfrepo.model.Test;
import org.perfrepo.model.TestExecution;

import java.util.List;

/**
 * Service dedicated to handle alerting feature.
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public interface AlertingService {

   /**
    * Creates new alert.
    *
    * @param alert
    * @return
    */
   public Alert createAlert(Alert alert);

   /**
    * Updates existing alert.
    *
    * @param alert
    * @return
    */
   public Alert updateAlert(Alert alert);

   /**
    * Removes existing alert.
    *
    * @param alert
    */
   public void removeAlert(Alert alert);

   /**
    * Retrieves all alerts assigned to test provided.
    *
    * @param test
    * @return
    */
   public List<Alert> getAlertsList(Test test);

   /**
    * Goes through all alerts that are valid for this test execution and checks if they passed.
    * Basically fulfills the alerting feature.
    *
    * @param testExecution
    */
   public void processAlerts(TestExecution testExecution);

   /**
    * Parses the condition string and checks for all the syntax errors.
    *
    * @param condition
    * @param metric
    */
   public void checkConditionSyntax(String condition, Metric metric);
}
