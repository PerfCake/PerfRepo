package org.jboss.qa.perfrepo.model.builder;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.jboss.qa.perfrepo.model.Tag;
import org.jboss.qa.perfrepo.model.Test;
import org.jboss.qa.perfrepo.model.TestExecution;
import org.jboss.qa.perfrepo.model.TestExecutionParameter;
import org.jboss.qa.perfrepo.model.TestExecutionTag;
import org.jboss.qa.perfrepo.model.Value;

/**
 * {@link TestExecution} builder.
 * 
 * @author Michal Linhard (mlinhard@redhat.com)
 * 
 */
public class TestExecutionBuilder {
   private TestExecution testExecution;

   public TestExecutionBuilder() {
      this.testExecution = new TestExecution();
   }

   public TestExecutionBuilder name(String name) {
      testExecution.setName(name);
      return this;
   }

   public TestExecutionBuilder started(Date started) {
      testExecution.setStarted(started);
      return this;
   }

   private TestExecutionParameter addParameter(TestExecutionParameter param) {
      Collection<TestExecutionParameter> parameters = testExecution.getParameters();
      if (parameters == null) {
         parameters = new HashSet<TestExecutionParameter>();
         testExecution.setParameters((Set<TestExecutionParameter>) parameters);
      }
      parameters.add(param);
      return param;
   }

   private TestExecutionParameter addParameter(String name, String value) {
      return addParameter(new TestExecutionParameter(name, value));
   }

   public TestExecutionBuilder parameter(String name, String value) {
      addParameter(name, value);
      return this;
   }

   private Tag addTag(String tag) {
      Collection<TestExecutionTag> testExecutionTags = testExecution.getTestExecutionTags();
      if (testExecutionTags == null) {
         testExecutionTags = new HashSet<TestExecutionTag>();
         testExecution.setTestExecutionTags((Set<TestExecutionTag>) testExecutionTags);
      }
      TestExecutionTag intermediate = new TestExecutionTag();
      Tag tagObj = new Tag();
      tagObj.setName(tag);
      intermediate.setTag(tagObj);
      testExecutionTags.add(intermediate);
      return tagObj;
   }

   public TestExecutionBuilder tag(String tag) {
      addTag(tag);
      return this;
   }

   private Value addValue(Value value) {
      Collection<Value> values = testExecution.getValues();
      if (values == null) {
         values = new HashSet<Value>();
         testExecution.setValues((Set<Value>) values);
      }
      values.add(value);
      return value;
   }

   public ValueBuilder value() {
      return new ValueBuilder(this, addValue(new Value()));
   }

   public TestExecutionBuilder value(String metricName, Double value, String paramName, String paramValue) {
      return value().metricName(metricName).resultValue(value).parameter(paramName, paramValue).execution();
   }

   public TestExecutionBuilder value(String metricName, Double value) {
      return value().metricName(metricName).resultValue(value).execution();
   }

   public TestExecutionBuilder testId(Long testId) {
      Test test = testExecution.getTest();
      if (test == null) {
         test = new Test();
         testExecution.setTest(test);
      }
      test.setId(testId);
      return this;
   }

   public TestExecution build() {
      TestExecution r = testExecution;
      testExecution = new TestExecution();
      return r;
   }

}
