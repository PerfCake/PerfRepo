package org.jboss.qa.perfrepo.model;

import java.io.Serializable;
import java.util.Collection;

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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

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
public class Test implements Serializable {

   private static final long serialVersionUID = 1L;

   public static final String FIND_ALL = "Test.findAll";

   public static final String FIND_TEST_ID = "Test.findTestId";
   public static final String FIND_TESTS_USING_METRIC = "Test.findTestsUsingMetric";

   @Id
   @SequenceGenerator(name = "TEST_ID_GENERATOR", sequenceName = "TEST_SEQUENCE", allocationSize = 1)
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TEST_ID_GENERATOR")
   private Long id;

   @Column(name = "name")
   private String name;

   @OneToMany(mappedBy = "test")
   private Collection<TestExecution> testExecutions;

   @OneToMany(mappedBy = "test")
   private Collection<TestMetric> testMetrics;

   @Column(name = "uid")
   private String uid;

   @Column(name = "groupId")
   private String groupId;

   @Column(name = "description")
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

   @XmlElementWrapper(name = "testMetrics")
   @XmlElement(name = "testMetric")
   public Collection<TestMetric> getTestMetrics() {
      return this.testMetrics;
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

}