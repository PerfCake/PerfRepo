package org.jboss.qa.perfrepo.model.builder;

import org.jboss.qa.perfrepo.model.Metric;

/**
 * {@link Metric} builder.
 * 
 * @author Michal Linhard (mlinhard@redhat.com)
 * 
 */
public class MetricBuilder {

   private TestBuilder parentBuilder;
   private Metric metric;

   MetricBuilder(TestBuilder parentBuilder, Metric metric) {
      this.parentBuilder = parentBuilder;
      this.metric = metric;
   }

   public MetricBuilder name(String name) {
      metric.setName(name);
      return this;
   }

   public MetricBuilder comparator(String comparator) {
      metric.setComparator(comparator);
      return this;
   }

   public MetricBuilder description(String description) {
      metric.setDescription(description);
      return this;
   }

   public TestBuilder test() {
      return parentBuilder;
   }

}
