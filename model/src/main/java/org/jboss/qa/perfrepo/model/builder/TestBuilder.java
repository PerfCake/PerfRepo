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

   /**
    * Create new {@link TestBuilder}.
    */
   public TestBuilder() {
      test = new Test();
   }

   /**
    * Set the test name.
    * 
    * @param name
    * @return this {@link TestBuilder}
    */
   public TestBuilder name(String name) {
      test.setName(name);
      return this;
   }

   /**
    * Set the test description.
    * 
    * @param description
    * @return this {@link TestBuilder}
    */
   public TestBuilder description(String description) {
      test.setDescription(description);
      return this;
   }

   /**
    * Set the test groupId.
    * 
    * @param groupId
    * @return this {@link TestBuilder}
    */
   public TestBuilder groupId(String groupId) {
      test.setGroupId(groupId);
      return this;
   }

   /**
    * Set the test uid.
    * 
    * @param uid
    * @return this {@link TestBuilder}
    */
   public TestBuilder uid(String uid) {
      test.setUid(uid);
      return this;
   }

   /**
    * Creates a new {@link Test} based on defined values.
    * 
    * @return the {@link Test}
    */
   public Test build() {
      Test ret = test;
      test = new Test();
      return ret;
   }

   /**
    * Shortcut for metric().name(name).comparator(comparator).description(description).test()
    * 
    * @param name
    * @param comparator
    * @param description
    * @return this {@link TestBuilder}
    */
   public TestBuilder metric(String name, String comparator, String description) {
      return metric().name(name).comparator(comparator).description(description).test();
   }

   /**
    * Shortcut for metric().name(name).description(description).test()
    * 
    * @param name
    * @param description
    * @return this {@link TestBuilder}
    */
   public TestBuilder metric(String name, String description) {
      return metric().name(name).description(description).test();
   }

   /**
    * Shortcut for metric().name(name).test()
    * 
    * @param name
    * @return this {@link TestBuilder}
    */
   public TestBuilder metric(String name) {
      return metric().name(name).test();
   }

   /**
    * Add a new {@link Metric}.
    * 
    * @return new {@link MetricBuilder}
    */
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
