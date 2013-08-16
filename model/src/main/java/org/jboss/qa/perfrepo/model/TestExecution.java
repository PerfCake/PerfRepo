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
package org.jboss.qa.perfrepo.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.jboss.qa.perfrepo.model.builder.TestExecutionBuilder;

/**
 * 
 * Represents one execution of a test.
 * 
 * @author Pavel Drozd (pdrozd@redhat.com)
 * @author Michal Linhard (mlinhard@redhat.com)
 * 
 */
@javax.persistence.Entity
@Table(name = "test_execution")
@NamedQueries({ @NamedQuery(name = TestExecution.FIND_TEST_ID, query = "SELECT te.test from TestExecution te inner join te.test where te= :entity") })
@XmlRootElement(name = "testExecution")
@Named("testExecution")
@RequestScoped
public class TestExecution implements Entity<TestExecution> {

   private static final long serialVersionUID = -2956845045583534606L;

   public static final String FIND_TEST_ID = "TestExecution.findTestId";

   @Id
   @SequenceGenerator(name = "TEST_EXECUTION_ID_GENERATOR", sequenceName = "TEST_EXECUTION_SEQUENCE", allocationSize = 1)
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TEST_EXECUTION_ID_GENERATOR")
   private Long id;

   @Column(name = "name")
   @NotNull
   @Size(max = 2047)
   private String name;

   @ManyToOne(optional = false, cascade = CascadeType.PERSIST)
   @JoinColumn(name = "test_id", referencedColumnName = "id")
   private Test test;

   @OneToMany(mappedBy = "testExecution")
   private Collection<TestExecutionParameter> parameters;

   @OneToMany(mappedBy = "testExecution")
   private Collection<TestExecutionTag> testExecutionTags;

   @OneToMany(mappedBy = "testExecution")
   private Collection<Value> values;

   @OneToMany(mappedBy = "testExecution")
   private Collection<TestExecutionAttachment> attachments;

   @Column(name = "started")
   private Date started;

   @Column(name = "locked")
   private Boolean locked;

   public TestExecution() {
      this.test = new Test();
   }

   @XmlTransient
   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   @XmlID
   @XmlAttribute(name = "id")
   public String getStringId() {
      return id == null ? null : String.valueOf(id);
   }

   public void setStringId(String id) {
      this.id = Long.valueOf(id);
   }

   public void setName(String name) {
      this.name = name;
   }

   @XmlAttribute(name = "name")
   public String getName() {
      return this.name;
   }

   public void setTest(Test test) {
      this.test = test;
   }

   /**
    * this atttribute is {@link XmlTransient} because we don't want to return {@link Test}, which
    * would need to include {@link Metric} objects. The test is determined from xml attribute testId
    * which is supplied by {@link TestExecution#getTestId()} method.
    */
   @XmlTransient
   public Test getTest() {
      return this.test;
   }

   @XmlAttribute(name = "testId")
   public String getTestId() {
      return test == null ? null : (test.getId() == null ? null : test.getId().toString());
   }

   public void setTestId(String id) {
      if (test == null) {
         test = new Test();
      }
      test.setId(Long.valueOf(id));
   }

   @XmlAttribute(name = "testUid")
   public String getTestUid() {
      return test == null ? null : test.getUid();
   }

   public void setTestUid(String uid) {
      if (test == null) {
         test = new Test();
      }
      test.setUid(uid);
   }

   public void setParameters(Collection<TestExecutionParameter> testExecutionParameters) {
      this.parameters = testExecutionParameters;
   }

   @XmlElementWrapper(name = "parameters")
   @XmlElement(name = "parameter")
   public Collection<TestExecutionParameter> getParameters() {
      return this.parameters;
   }

   public void setTestExecutionTags(Collection<TestExecutionTag> testExecutionTags) {
      this.testExecutionTags = testExecutionTags;
   }

   @XmlElementWrapper(name = "tags")
   @XmlElement(name = "tag")
   public Collection<TestExecutionTag> getTestExecutionTags() {
      return this.testExecutionTags;
   }

   public void setValues(Collection<Value> values) {
      this.values = values;
   }

   @XmlElementWrapper(name = "values")
   @XmlElement(name = "value")
   public Collection<Value> getValues() {
      return this.values;
   }

   @XmlAttribute(name = "started")
   public Date getStarted() {
      return started;
   }

   public void setStarted(Date started) {
      this.started = started;
   }

   @XmlElementWrapper(name = "attachments")
   @XmlElement(name = "attachment")
   public Collection<TestExecutionAttachment> getAttachments() {
      return attachments;
   }

   public void setAttachments(Collection<TestExecutionAttachment> attachments) {
      this.attachments = attachments;
   }

   @XmlTransient
   public List<String> getSortedTags() {
      if (testExecutionTags == null || testExecutionTags.isEmpty()) {
         return new ArrayList<String>(0);
      } else {
         List<String> result = new ArrayList<String>();
         for (TestExecutionTag tet : testExecutionTags) {
            result.add(tet.getTag().getName());
         }
         Collections.sort(result);
         return result;
      }
   }

   @XmlTransient
   public List<TestExecutionParameter> getSortedParameters() {
      if (parameters == null || parameters.isEmpty()) {
         return new ArrayList<TestExecutionParameter>(0);
      } else {
         List<TestExecutionParameter> result = new ArrayList<TestExecutionParameter>(parameters);
         Collections.sort(result);
         return result;
      }
   }

   @XmlTransient
   public Map<String, String> getParametersAsMap() {
      if (parameters == null || parameters.isEmpty()) {
         return new HashMap<String, String>(0);
      } else {
         Map<String, String> r = new HashMap<String, String>();
         for (TestExecutionParameter p : parameters) {
            r.put(p.getName(), p.getValue());
         }
         return r;
      }
   }

   public TestExecutionParameter findParameter(String name) {
      if (parameters == null || parameters.isEmpty()) {
         return null;
      }
      for (TestExecutionParameter param : parameters) {
         if (name.equals(param.getName())) {
            return param;
         }
      }
      return null;
   }

   public static TestExecutionBuilder builder() {
      return new TestExecutionBuilder();
   }

   @Override
   public TestExecution clone() {
      try {
         return (TestExecution) super.clone();
      } catch (CloneNotSupportedException e) {
         throw new RuntimeException(e);
      }
   }

   public boolean isLocked() {
      return locked == null || locked.booleanValue();
   }

   public Boolean getLocked() {
      return locked;
   }

   public void setLocked(Boolean locked) {
      this.locked = locked;
   }
}