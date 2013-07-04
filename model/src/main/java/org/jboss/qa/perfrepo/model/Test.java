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

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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

import org.jboss.qa.perfrepo.model.builder.TestBuilder;

/**
 * Represents one test.
 * 
 * @author Pavel Drozd (pdrozd@redhat.com)
 * @author Michal Linhard (mlinhard@redhat.com)
 */
@Entity
@Table(name = "test")
@NamedQueries({
      @NamedQuery(name = Test.FIND_TEST_ID, query = "SELECT test from Test test where test = :entity"),
      @NamedQuery(name = Test.FIND_TESTS_USING_METRIC, query = "SELECT test from Test test, TestMetric tm, Metric m where test = tm.test and tm.metric = m and m.id = :metric") })
@XmlRootElement(name = "test")
@Named("test")
@RequestScoped
public class Test implements Serializable, CloneableEntity<Test> {

   private static final long serialVersionUID = 2936849220074718535L;

   public static final String FIND_ALL = "Test.findAll";

   public static final String FIND_TEST_ID = "Test.findTestId";
   public static final String FIND_TESTS_USING_METRIC = "Test.findTestsUsingMetric";

   @Id
   @SequenceGenerator(name = "TEST_ID_GENERATOR", sequenceName = "TEST_SEQUENCE", allocationSize = 1)
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TEST_ID_GENERATOR")
   private Long id;

   @Column(name = "name")
   @NotNull
   @Size(max = 2047)
   private String name;

   @OneToMany(mappedBy = "test")
   private Collection<TestExecution> testExecutions;

   @OneToMany(mappedBy = "test")
   private Collection<TestMetric> testMetrics;

   @Column(name = "uid")
   @NotNull
   @Size(max = 2047)
   private String uid;

   @Column(name = "groupId")
   @NotNull
   @Size(max = 255)
   private String groupId;

   @Column(name = "description")
   @NotNull
   @Size(max = 10239)
   private String description;

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

   public void setTestExecutions(Collection<TestExecution> testExecutions) {
      this.testExecutions = testExecutions;
   }

   @XmlTransient
   public Collection<TestExecution> getTestExecutions() {
      return this.testExecutions;
   }

   public void setTestMetrics(Collection<TestMetric> testMetrics) {
      this.testMetrics = testMetrics;
   }

   @XmlTransient
   public Collection<TestMetric> getTestMetrics() {
      return this.testMetrics;
   }

   @XmlElementWrapper(name = "metrics")
   @XmlElement(name = "metric")
   public Collection<Metric> getMetrics() {
      return testMetrics == null ? null : new MetricCollection();
   }

   public void setMetrics(Collection<Metric> metrics) {
      testMetrics = new ArrayList<TestMetric>();
      getMetrics().addAll(metrics);
   }

   /**
    * Hack to evade listing intermediate {@link TestMetric} objects in XML.
    */
   private class MetricCollection extends AbstractCollection<Metric> {

      private class MetricIterator implements Iterator<Metric> {

         private Iterator<TestMetric> iterator;

         @Override
         public boolean hasNext() {
            return iterator.hasNext();
         }

         @Override
         public Metric next() {
            return iterator.next().getMetric();
         }

         @Override
         public void remove() {
            throw new UnsupportedOperationException();
         }

      }

      @Override
      public Iterator<Metric> iterator() {
         MetricIterator i = new MetricIterator();
         i.iterator = testMetrics.iterator();
         return i;
      }

      @Override
      public int size() {
         return testMetrics.size();
      }

      @Override
      public boolean add(Metric e) {
         TestMetric tm = new TestMetric();
         tm.setMetric(e);
         return testMetrics.add(tm);
      }

   }

   public void setUid(String uid) {
      this.uid = uid;
   }

   @XmlAttribute(name = "uid")
   public String getUid() {
      return this.uid;
   }

   @XmlAttribute(name = "groupId")
   public String getGroupId() {
      return groupId;
   }

   public void setGroupId(String groupId) {
      this.groupId = groupId;
   }

   public String getDescription() {
      return description;
   }

   @XmlElement(name = "description")
   public void setDescription(String description) {
      this.description = description;
   }

   @XmlTransient
   public List<Metric> getSortedMetrics() {
      if (testMetrics == null || testMetrics.isEmpty()) {
         return new ArrayList<Metric>(0);
      } else {
         List<Metric> result = new ArrayList<Metric>();
         for (TestMetric tm : testMetrics) {
            result.add(tm.getMetric());
         }
         Collections.sort(result);
         return result;
      }
   }

   public static TestBuilder builder() {
      return new TestBuilder();
   }

   @Override
   public Test clone() {
      try {
         return (Test) super.clone();
      } catch (CloneNotSupportedException e) {
         throw new RuntimeException(e);
      }
   }

}