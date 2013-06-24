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

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Table(name = "test_metric")
@NamedQueries({
      @NamedQuery(name = TestMetric.FIND_TEST_ID, query = "SELECT tm.test from TestMetric tm inner join tm.test where tm.id= :entity"),
      @NamedQuery(name = TestMetric.FIND_TEST_METRIC, query = "SELECT tm from TestMetric tm JOIN tm.test t JOIN tm.metric m WHERE t.id= :test and m.id= :metric") })
@XmlRootElement(name = "testMetric")
@Named("testMetric")
@RequestScoped
public class TestMetric implements Serializable {

   public static final String FIND_TEST_ID = "TestMetric.findTestId";
   public static final String FIND_TEST_METRIC = "TestMetric.findTestMetric";

   @Id
   @SequenceGenerator(name = "TEST_METRIC_ID_GENERATOR", sequenceName = "TEST_METRIC_SEQUENCE", allocationSize = 1)
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TEST_METRIC_ID_GENERATOR")
   private Long id;

   @ManyToOne(optional = false, cascade = CascadeType.PERSIST)
   @JoinColumn(name = "metric_id", referencedColumnName = "id")
   private Metric metric;

   @ManyToOne(optional = false, cascade = CascadeType.PERSIST)
   @JoinColumn(name = "test_id", referencedColumnName = "id")
   private Test test;

   public TestMetric() {
      this.metric = new Metric();
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

   public void setMetric(Metric metric) {
      this.metric = metric;
   }

   @XmlElement(name = "metric")
   public Metric getMetric() {
      return this.metric;
   }

   public void setTest(Test test) {
      this.test = test;
   }

   @XmlTransient
   public Test getTest() {
      return this.test;
   }

}