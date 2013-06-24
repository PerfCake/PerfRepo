/* 
 * Copyright 2013 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

   /**
    * Create a new {@link TestExecutionBuilder}.
    */
   public TestExecutionBuilder() {
      this.testExecution = new TestExecution();
   }

   /**
    * Set the name.
    * 
    * @param name
    * @return this {@link TestExecutionBuilder}
    */
   public TestExecutionBuilder name(String name) {
      testExecution.setName(name);
      return this;
   }

   /**
    * Set the started date.
    * 
    * @param name
    * @return this {@link TestExecutionBuilder}
    */
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

   /**
    * Add a new {@link TestExecutionParameter} to this {@link TestExecution} with given name and
    * value
    * 
    * @param name
    * @param value
    * @return this {@link TestExecutionBuilder}
    */
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

   /**
    * Add a new {@link Tag} to this {@link TestExecution} with given name
    * 
    * @param tag
    * @return
    */
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

   /**
    * Add a new {@link Value}
    * 
    * @return new {@link ValueBuilder}
    */
   public ValueBuilder value() {
      return new ValueBuilder(this, addValue(new Value()));
   }

   /**
    * Shortcut for value().metricName(metricName).resultValue(value).parameter(paramName,
    * paramValue).execution()
    * 
    * @param metricName
    * @param value
    * @param paramName
    * @param paramValue
    * @return this {@link TestExecutionBuilder}
    */
   public TestExecutionBuilder value(String metricName, Double value, String paramName, String paramValue) {
      return value().metricName(metricName).resultValue(value).parameter(paramName, paramValue).execution();
   }

   /**
    * Shortcut for value().metricName(metricName).resultValue(value).execution()
    * 
    * @param metricName
    * @param value
    * @return this {@link TestExecutionBuilder}
    */
   public TestExecutionBuilder value(String metricName, Double value) {
      return value().metricName(metricName).resultValue(value).execution();
   }

   /**
    * Sets the id of the {@link Test} referenced by this {@link TestExecution}. If the test is not
    * present, it's created.
    * 
    * @param testId
    * @return this {@link TestExecutionBuilder}
    */
   public TestExecutionBuilder testId(Long testId) {
      Test test = testExecution.getTest();
      if (test == null) {
         test = new Test();
         testExecution.setTest(test);
      }
      test.setId(testId);
      return this;
   }

   /**
    * Sets the uid of the {@link Test} referenced by this {@link TestExecution}. If the test is not
    * present, it's created.
    * 
    * @param testUid
    * @return this {@link TestExecutionBuilder}
    */
   public TestExecutionBuilder testUid(String testUid) {
      Test test = testExecution.getTest();
      if (test == null) {
         test = new Test();
         testExecution.setTest(test);
      }
      test.setUid(testUid);
      return this;
   }

   /**
    * Creates a new {@link TestExecution} based on defined values.
    * 
    * @return the {@link TestExecution}
    */
   public TestExecution build() {
      TestExecution r = testExecution;
      testExecution = new TestExecution();
      return r;
   }

}
