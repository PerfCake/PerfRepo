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
import java.util.Set;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * 
 * Represents one value measured in a {@link TestExecution}.
 * 
 * @author Pavel Drozd (pdrozd@redhat.com)
 * @author Michal Linhard (mlinhard@redhat.com)
 * 
 */
@Entity
@Table(name = "value")
@NamedQueries({ @NamedQuery(name = Value.FIND_TEST_ID, query = "SELECT test from Value v inner join v.testExecution te inner join te.test test where v.id= :entity") })
@XmlRootElement(name = "value")
@Named("value")
@RequestScoped
public class Value implements Serializable, CloneableEntity<Value> {

   public static final String FIND_TEST_ID = "Value.findTestId";

   @Id
   @SequenceGenerator(name = "VALUE_ID_GENERATOR", sequenceName = "VALUE_SEQUENCE", allocationSize = 1)
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "VALUE_ID_GENERATOR")
   private Long id;

   @ManyToOne(optional = false, cascade = CascadeType.PERSIST)
   @JoinColumn(name = "metric_id", referencedColumnName = "id")
   private Metric metric;

   @Column(name = "name")
   @Size(max = 255)
   private String name;

   @Column(name = "result_value")
   private Double resultValue;

   @ManyToOne(optional = false, cascade = CascadeType.PERSIST)
   @JoinColumn(name = "test_execution_id", referencedColumnName = "id")
   private TestExecution testExecution;

   @OneToMany(mappedBy = "value")
   private Set<ValueParameter> parameters;

   public Value() {
      this.metric = new Metric();
      this.testExecution = new TestExecution();
   }

   @XmlTransient
   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public void setMetric(Metric metric) {
      this.metric = metric;
   }

   @XmlTransient
   public Metric getMetric() {
      return this.metric;
   }

   public void setName(String name) {
      this.name = name;
   }

   @XmlAttribute(name = "name")
   public String getName() {
      return this.name;
   }

   public void setResultValue(Double resultValue) {
      this.resultValue = resultValue;
   }

   @XmlAttribute(name = "result")
   public Double getResultValue() {
      return this.resultValue;
   }

   public void setTestExecution(TestExecution testExecution) {
      this.testExecution = testExecution;
   }

   @XmlTransient
   public TestExecution getTestExecution() {
      return this.testExecution;
   }

   public void setParameters(Set<ValueParameter> valueParameters) {
      this.parameters = valueParameters;
   }

   @XmlElementWrapper(name = "parameters")
   @XmlElement(name = "parameter")
   public Set<ValueParameter> getParameters() {
      return this.parameters;
   }

   @XmlAttribute(name = "metricName")
   public String getMetricName() {
      return metric == null ? null : metric.getName();
   }

   public void setMetricName(String metricName) {
      if (metric == null) {
         metric = new Metric();
      }
      metric.setName(metricName);
   }

   @XmlAttribute(name = "metricComparator")
   public String getMetricComparator() {
      return metric == null ? null : metric.getComparator();
   }

   public void setMetricComparator(String metricComparator) {
      if (metric == null) {
         metric = new Metric();
      }
      metric.setComparator(metricComparator);
   }

   @Override
   public Value clone() {
      try {
         return (Value) super.clone();
      } catch (CloneNotSupportedException e) {
         throw new RuntimeException(e);
      }
   }

   public boolean hasParameters() {
      return parameters != null && !parameters.isEmpty();
   }

}