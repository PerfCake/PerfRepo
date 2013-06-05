package org.jboss.qa.perfrepo.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Table(name = "test_execution")
@NamedQueries({ @NamedQuery(name = TestExecution.FIND_ALL, query = "SELECT x from TestExecution x"), @NamedQuery(name = TestExecution.FIND_ALL_SORTED_BY_ID_ASC, query = "SELECT x FROM TestExecution x ORDER BY x.id ASC"), @NamedQuery(name = TestExecution.FIND_ALL_SORTED_BY_ID_DESC, query = "SELECT x FROM TestExecution x ORDER BY x.id DESC"), @NamedQuery(name = TestExecution.FIND_ALL_SORTED_BY_NAME_ASC, query = "SELECT x FROM TestExecution x ORDER BY x.name ASC"), @NamedQuery(name = TestExecution.FIND_ALL_SORTED_BY_NAME_DESC, query = "SELECT x FROM TestExecution x ORDER BY x.name DESC"), @NamedQuery(name = TestExecution.FIND_BY_TEST, query = "SELECT x FROM TestExecution x WHERE x.test.id = :" + Test.NQ_ID),

@NamedQuery(name = TestExecution.FIND_BY_ID, query = "SELECT x from TestExecution x WHERE x.id = :" + TestExecution.NQ_ID) })
@XmlRootElement(name = "testExecution")
@Named("testExecution")
@RequestScoped
public class TestExecution implements Serializable {

   private static final long serialVersionUID = 1L;

   public static final String FIND_ALL = "TestExecution.findAll";

   public static final String FIND_ALL_SORTED_BY_ID_ASC = "TestExecution.findAllSortedByIdAsc";
   public static final String FIND_ALL_SORTED_BY_ID_DESC = "TestExecution.findAllSortedByIdDesc";
   public static final String FIND_ALL_SORTED_BY_NAME_ASC = "TestExecution.findAllSortedByNameAsc";
   public static final String FIND_ALL_SORTED_BY_NAME_DESC = "TestExecution.findAllSortedByNameDesc";
   public static final String FIND_BY_TEST = "TestExecution.findByTest";

   public static final String FIND_BY_ID = "TestExecution.findById";
   public static final String NQ_ID = "testExecutionId";

   @Id
   @SequenceGenerator(name = "TEST_EXECUTION_ID_GENERATOR", sequenceName = "TEST_EXECUTION_SEQUENCE", allocationSize = 1)
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TEST_EXECUTION_ID_GENERATOR")
   private Long id;

   @Column(name = "name")
   private String name;
   
   @ManyToOne(optional = false, cascade = CascadeType.PERSIST)
   @JoinColumn(name = "test_id", referencedColumnName = "id")
   private Test test;
   
   @OneToMany(mappedBy = "testExecution")
   private Set<TestExecutionParameter> testExecutionParameters = new HashSet<TestExecutionParameter>();
   
   @OneToMany(mappedBy = "testExecution")
   private Set<TestExecutionTag> testExecutionTags = new HashSet<TestExecutionTag>();
   
   @OneToMany(mappedBy = "testExecution")
   private Set<Value> values = new HashSet<Value>();
   
   @Column(name = "started")
   private Date started;

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

   public void setTest(Test test) {
      this.test = test;
   }

   @XmlElement(name = "test")
   public Test getTest() {
      return this.test;
   }

   public void setTestExecutionParameters(Set<TestExecutionParameter> testExecutionParameters) {
      this.testExecutionParameters = testExecutionParameters;
   }

   @XmlElementWrapper(name = "testExecutionParameters")
   @XmlElement(name = "testExecutionParameter")
   public Collection<TestExecutionParameter> getTestExecutionParameters() {
      return this.testExecutionParameters;
   }

   public void setTestExecutionTags(Set<TestExecutionTag> testExecutionTags) {
      this.testExecutionTags = testExecutionTags;
   }

   @XmlElementWrapper(name = "testExecutionTags")
   @XmlElement(name = "testExecutionTag")
   public Collection<TestExecutionTag> getTestExecutionTags() {
      return this.testExecutionTags;
   }

   public void setValues(Set<Value> values) {
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

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((started == null) ? 0 : started.hashCode());
      result = prime * result + ((test == null) ? 0 : test.hashCode());
      result = prime * result + ((testExecutionParameters == null) ? 0 : testExecutionParameters.hashCode());
      result = prime * result + ((testExecutionTags == null) ? 0 : testExecutionTags.hashCode());
      result = prime * result + ((values == null) ? 0 : values.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      TestExecution other = (TestExecution) obj;
      if (id == null) {
         if (other.id != null)
            return false;
      } else if (!id.equals(other.id))
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (started == null) {
         if (other.started != null)
            return false;
      } else if (!started.equals(other.started))
         return false;
      if (test == null) {
         if (other.test != null)
            return false;
      } else if (!test.equals(other.test))
         return false;
      if (testExecutionParameters == null) {
         if (other.testExecutionParameters != null)
            return false;
      } else if (!testExecutionParameters.equals(other.testExecutionParameters))
         return false;
      if (testExecutionTags == null) {
         if (other.testExecutionTags != null)
            return false;
      } else if (!testExecutionTags.equals(other.testExecutionTags))
         return false;
      if (values == null) {
         if (other.values != null)
            return false;
      } else if (!values.equals(other.values))
         return false;
      return true;
   }
   
   
   
   

}