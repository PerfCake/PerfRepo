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

@Entity
@Table(name = "test")
   @NamedQueries({ @NamedQuery(name = Test.FIND_ALL, query = "SELECT x from Test x"), 
   @NamedQuery(name = Test.FIND_ALL_SORTED_BY_ID_ASC, query = "SELECT x FROM Test x ORDER BY x.id ASC"), 
   @NamedQuery(name = Test.FIND_ALL_SORTED_BY_ID_DESC, query = "SELECT x FROM Test x ORDER BY x.id DESC"), 
   @NamedQuery(name = Test.FIND_ALL_SORTED_BY_NAME_ASC, query = "SELECT x FROM Test x ORDER BY x.name ASC"), 
   @NamedQuery(name = Test.FIND_ALL_SORTED_BY_NAME_DESC, query = "SELECT x FROM Test x ORDER BY x.name DESC"), 
   @NamedQuery(name = Test.FIND_ALL_SORTED_BY_UID_ASC, query = "SELECT x FROM Test x ORDER BY x.uid ASC"), 
   @NamedQuery(name = Test.FIND_ALL_SORTED_BY_UID_DESC, query = "SELECT x FROM Test x ORDER BY x.uid DESC"),

@NamedQuery(name = Test.FIND_BY_ID, query = "SELECT x from Test x WHERE x.id = :" + Test.NQ_ID) })
@XmlRootElement(name = "test")
@Named("test")
@RequestScoped
public class Test implements Serializable {

   private static final long serialVersionUID = 1L;

   public static final String FIND_ALL = "Test.findAll";

   public static final String FIND_ALL_SORTED_BY_ID_ASC = "Test.findAllSortedByIdAsc";
   public static final String FIND_ALL_SORTED_BY_ID_DESC = "Test.findAllSortedByIdDesc";
   public static final String FIND_ALL_SORTED_BY_NAME_ASC = "Test.findAllSortedByNameAsc";
   public static final String FIND_ALL_SORTED_BY_NAME_DESC = "Test.findAllSortedByNameDesc";
   public static final String FIND_ALL_SORTED_BY_UID_ASC = "Test.findAllSortedByUidAsc";
   public static final String FIND_ALL_SORTED_BY_UID_DESC = "Test.findAllSortedByUidDesc";

   public static final String FIND_BY_ID = "Test.findById";
   public static final String NQ_ID = "testId";

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