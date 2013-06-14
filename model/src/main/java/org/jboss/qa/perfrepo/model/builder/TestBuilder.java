package org.jboss.qa.perfrepo.model.builder;

import java.util.ArrayList;
import java.util.Collection;

import org.jboss.qa.perfrepo.model.Metric;
import org.jboss.qa.perfrepo.model.Test;
import org.jboss.qa.perfrepo.model.TestMetric;

/**
 * {@link Test} builder.
 * 
 * @author Michal Linhard (mlinhard@redhat.com)
 * 
 */
public class TestBuilder {
   private Test test;

   public TestBuilder() {
      test = new Test();
   }

   public TestBuilder name(String name) {
      test.setName(name);
      return this;
   }

   public TestBuilder description(String description) {
      test.setDescription(description);
      return this;
   }

   public TestBuilder groupId(String groupId) {
      test.setGroupId(groupId);
      return this;
   }

   public TestBuilder uid(String uid) {
      test.setUid(uid);
      return this;
   }

   public Test build() {
      Test ret = test;
      test = new Test();
      return ret;
   }

   public TestBuilder metric(String name, String comparator, String description) {
      return new MetricBuilder(this, addMetric(new Metric())).name(name).comparator(comparator).description(description).test();
   }

   public TestBuilder metric(String name, String description) {
      return new MetricBuilder(this, addMetric(new Metric())).name(name).description(description).test();
   }

   public TestBuilder metric(String name) {
      return new MetricBuilder(this, addMetric(new Metric())).name(name).test();
   }

   public MetricBuilder metric() {
      return new MetricBuilder(this, addMetric(new Metric()));
   }

   private Metric addMetric(Metric metric) {
      Collection<TestMetric> intermediates = test.getTestMetrics();
      if (intermediates == null) {
         intermediates = new ArrayList<TestMetric>();
         test.setTestMetrics(intermediates);
      }
      TestMetric intermediate = new TestMetric();
      intermediate.setMetric(metric);
      intermediates.add(intermediate);

      return metric;
   }

}
