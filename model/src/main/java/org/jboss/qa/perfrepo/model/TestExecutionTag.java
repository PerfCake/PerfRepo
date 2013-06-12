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
@Table(name = "test_execution_tag")
@NamedQueries({ @NamedQuery(name = TestExecutionTag.FIND_TEST_ID, query = "SELECT test from TestExecutionTag teg inner join teg.testExecution te inner join te.test test WHERE teg = :entity") })
@XmlRootElement(name = "testExecutionTag")
@Named("testExecutionTag")
@RequestScoped
public class TestExecutionTag implements Serializable {

   public static final String FIND_TEST_ID = "TestExecutionTag.findTestId";

   @Id
   @SequenceGenerator(name = "TEST_EXECUTION_TAG_ID_GENERATOR", sequenceName = "TEST_EXECUTION_TAG_SEQUENCE", allocationSize = 1)
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TEST_EXECUTION_TAG_ID_GENERATOR")
   private Long id;

   @ManyToOne(optional = false, cascade = CascadeType.PERSIST)
   @JoinColumn(name = "tag_id", referencedColumnName = "id")
   private Tag tag;

   @ManyToOne(optional = false, cascade = CascadeType.PERSIST)
   @JoinColumn(name = "test_execution_id", referencedColumnName = "id")
   private TestExecution testExecution;

   public TestExecutionTag() {
      super();
      this.tag = new Tag();
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
      return id == null ? null : String.valueOf(id);
   }

   public void setStringId(String id) {
      this.id = Long.valueOf(id);
   }

   public void setTag(Tag tag) {
      this.tag = tag;
   }

   @XmlElement(name = "tag")
   public Tag getTag() {
      return this.tag;
   }

   public void setTestExecution(TestExecution testExecution) {
      this.testExecution = testExecution;
   }

   @XmlTransient
   public TestExecution getTestExecution() {
      return this.testExecution;
   }

}