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
import javax.persistence.Column;
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Table(name = "test_execution_parameter")
@NamedQueries({
      @NamedQuery(name = TestExecutionParameter.FIND_TEST_ID, query = "SELECT test from Test test inner join test.testExecutions te inner join te.parameters tep where tep.id = :entity"),
      @NamedQuery(name = TestExecutionParameter.FIND_BY_TEST_ID, query = "SELECT DISTINCT p.name FROM TestExecutionParameter p, TestExecution e WHERE p.testExecution.id = e.id AND e.test.id = :testId") })
@XmlRootElement(name = "testExecutionParameter")
@Named("testExecutionParameter")
@RequestScoped
public class TestExecutionParameter implements Serializable, Comparable<TestExecutionParameter>, CloneableEntity<TestExecutionParameter> {

   public static final String FIND_ALL = "TestExecutionParameter.findAll";

   public static final String FIND_TEST_ID = "TestExecutionParameter.findTestId";
   public static final String FIND_BY_TEST_ID = "TestExecutionParameter.findByTestId";

   @Id
   @SequenceGenerator(name = "TEST_EXECUTION_PARAMETER_ID_GENERATOR", sequenceName = "TEST_EXECUTION_PARAMETER_SEQUENCE", allocationSize = 1)
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TEST_EXECUTION_PARAMETER_ID_GENERATOR")
   private Long id;

   @Column(name = "name")
   @NotNull
   @Size(max = 2047)
   private String name;

   @ManyToOne(optional = false, cascade = CascadeType.PERSIST)
   @JoinColumn(name = "test_execution_id", referencedColumnName = "id")
   private TestExecution testExecution;

   @Column(name = "value")
   @NotNull
   @Size(max = 2047)
   private String value;

   public TestExecutionParameter() {
      this.testExecution = new TestExecution();
   }

   public TestExecutionParameter(String name, String value) {
      this.testExecution = new TestExecution();
      this.name = name;
      this.value = value;
   }

   @XmlTransient
   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public void setName(String name) {
      this.name = name;
   }

   @XmlAttribute(name = "name")
   public String getName() {
      return this.name;
   }

   public void setTestExecution(TestExecution testExecution) {
      this.testExecution = testExecution;
   }

   @XmlTransient
   public TestExecution getTestExecution() {
      return this.testExecution;
   }

   public void setValue(String value) {
      this.value = value;
   }

   @XmlAttribute(name = "value")
   public String getValue() {
      return this.value;
   }

   @Override
   public int compareTo(TestExecutionParameter o) {
      return this.getName().compareTo(o.getName());
   }

   @Override
   public TestExecutionParameter clone() {
      try {
         return (TestExecutionParameter) super.clone();
      } catch (CloneNotSupportedException e) {
         throw new RuntimeException(e);
      }
   }

}