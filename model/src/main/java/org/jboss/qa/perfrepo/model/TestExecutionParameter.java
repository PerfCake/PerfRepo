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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Table(name = "test_execution_parameter")
@NamedQueries({ 
   @NamedQuery(name = TestExecutionParameter.FIND_TEST_ID, query = "SELECT test from Test test inner join test.testExecutions te inner join te.testExecutionParameters tep where tep.id = :entity")})
@XmlRootElement(name = "testExecutionParameter")
@Named("testExecutionParameter")
@RequestScoped
public class TestExecutionParameter implements Serializable {

   private static final long serialVersionUID = 1L;

   public static final String FIND_ALL = "TestExecutionParameter.findAll";

   public static final String FIND_TEST_ID = "TestExecutionParameter.findTestId";

   @Id
   @SequenceGenerator(name = "TEST_EXECUTION_PARAMETER_ID_GENERATOR", sequenceName = "TEST_EXECUTION_PARAMETER_SEQUENCE", allocationSize = 1)
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TEST_EXECUTION_PARAMETER_ID_GENERATOR")
   private Long id;

   @Column(name = "name")
   private String name;
   
   @ManyToOne(optional = false, cascade = CascadeType.PERSIST)
   @JoinColumn(name = "test_execution_id", referencedColumnName = "id")
   private TestExecution testExecution;
   
   @Column(name = "value")
   private String value;

   public TestExecutionParameter() {
      this.testExecution = new TestExecution();
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
      return String.valueOf(id);
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

}