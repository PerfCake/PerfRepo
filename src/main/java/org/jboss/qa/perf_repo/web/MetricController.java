package org.jboss.qa.perf_repo.web;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.qa.perfrepo.model.Metric;
import org.jboss.qa.perfrepo.service.TestService;

@Named
@RequestScoped
public class MetricController implements Serializable {

   private static final long serialVersionUID = 1L;

   @Inject
   TestService testService;

   private Metric metric = null;

   private List<Metric> metricList = null;

   public Metric getMetric() {
      String id = null;
      if (metric == null) {
         if ((id = getRequestParam("metricId")) != null) {
            metric = testService.getMetric(Long.valueOf(id));
         } else {
            metric = new Metric();
         }         
      }
      return metric;
   }

   public List<Metric> getMetricList() {
      if (metricList == null) {               
         metricList = testService.getAllMetrics();
      }
      return metricList;
   }   

   public String update() {
      if (metric != null) {
         testService.storeMetric(metric);
      }
      return "MetricList";
   }

   public String create() {
      if (metric != null) {
         testService.storeMetric(metric);
      }
      return "MetricList";
   }

   public String delete(Metric metric) {
      testService.deleteMetric(metric);
      return "MetricList";
   }

   public Map<String, String> getRequestParams() {
      Map<String, String> map = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
      return map;
   }

   public String getRequestParam(String name) {
      return getRequestParams().get(name);
   }

   public String getRequestParam(String name, String _default) {
      String ret = getRequestParam(name);
      if (ret == null) {
         return _default;
      } else {
         return ret;
      }
   }
}