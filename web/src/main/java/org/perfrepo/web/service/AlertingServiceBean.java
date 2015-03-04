package org.perfrepo.web.service;

import org.perfrepo.model.Alert;
import org.perfrepo.model.Metric;
import org.perfrepo.model.Test;
import org.perfrepo.model.TestExecution;
import org.perfrepo.model.Value;
import org.perfrepo.web.alerting.ConditionChecker;
import org.perfrepo.web.dao.AlertDAO;
import org.perfrepo.web.dao.MetricDAO;
import org.perfrepo.web.dao.TestDAO;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of {@link org.perfrepo.web.service.AlertingService}
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
@Named
@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class AlertingServiceBean implements AlertingService {

   @Inject
   private AlertDAO alertDAO;

   @Inject
   private TestDAO testDAO;

   @Inject
   private MetricDAO metricDAO;

   @Inject
   private AlertingReporterService emailService;

   @Inject
   private ConditionChecker conditionChecker;

   @Inject
   private AlertingReporterService alertingReporterService;

   @Override
   public Alert createAlert(Alert alert) {
      makeManaged(alert);
      return alertDAO.create(alert);
   }

   @Override
   public Alert updateAlert(Alert alert) {
      makeManaged(alert);
      return alertDAO.update(alert);
   }

   @Override
   public List<Alert> getAlertsList(Test test) {
      List<Alert> alertsList = new ArrayList<>();
      if(test != null) {
         alertsList.addAll(test.getAlerts());
      }

      return alertsList;
   }

   @Override
   public void processAlerts(TestExecution testExecution) {
      Test test = testExecution.getTest();
      Collection<Value> values = testExecution.getValues();

      Map<Metric, Double> results = new HashMap<>();
      for(Value value: values) {
         results.put(value.getMetric(), value.getResultValue());
      }

      for(Metric metric: results.keySet()) {
         List<Alert> alerts = alertDAO.getByTestAndMetric(test, metric);
         for(Alert alert: alerts) {
            if(!conditionChecker.checkCondition(alert.getCondition(), results.get(metric), test, metric)) {
               alertingReporterService.reportAlert(alert, testExecution);
            }
         }
      }
   }

   /**
    * Helper method. Retrieves all the associated entities, because we need them
    * in managed state.
    *
    * @param alert
    */
   private void makeManaged(Alert alert) {
      Test test = testDAO.get(alert.getTest().getId());
      alert.setTest(test);

      Metric metric = metricDAO.get(alert.getMetric().getId());
      alert.setMetric(metric);
   }

}
