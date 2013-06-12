package org.jboss.qa.perfrepo.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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

/**
 * 
 * Represents one execution of a test.
 * 
 * @author Pavel Drozd (pdrozd@redhat.com)
 * @author Michal Linhard (mlinhard@redhat.com)
 * 
 */
@Entity
@Table(name = "test_execution")
@NamedQueries({ @NamedQuery(name = TestExecution.FIND_TEST_ID, query = "SELECT te.test from TestExecution te inner join te.test where te= :entity") })
@XmlRootElement(name = "testExecution")
@Named("testExecution")
@RequestScoped
public class TestExecution implements Serializable {

   private static final long serialVersionUID = 1L;
   public static final String FIND_TEST_ID = "TestExecution.findTestId";

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
   private Set<TestExecutionParameter> parameters = new HashSet<TestExecutionParameter>();

   @OneToMany(mappedBy = "testExecution")
   private Set<TestExecutionTag> testExecutionTags = new HashSet<TestExecutionTag>();

   @OneToMany(mappedBy = "testExecution")
   private Set<Value> values = new HashSet<Value>();

   @OneToMany(mappedBy = "testExecution")
   private Set<TestExecutionAttachment> attachments = new HashSet<TestExecutionAttachment>();

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

   public void setParameters(Set<TestExecutionParameter> testExecutionParameters) {
      this.parameters = testExecutionParameters;
   }

   @XmlElementWrapper(name = "testExecutionParameters")
   @XmlElement(name = "testExecutionParameter")
   public Collection<TestExecutionParameter> getParameters() {
      return this.parameters;
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

   @XmlTransient
   public Collection<TestExecutionAttachment> getAttachments() {
      return attachments;
   }

   public void setAttachments(Set<TestExecutionAttachment> attachments) {
      this.attachments = attachments;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((started == null) ? 0 : started.hashCode());
      result = prime * result + ((test == null) ? 0 : test.hashCode());
      result = prime * result + ((parameters == null) ? 0 : parameters.hashCode());
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
      if (parameters == null) {
         if (other.parameters != null)
            return false;
      } else if (!parameters.equals(other.parameters))
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

   /**
    * 
    * @return test uid
    */
   public String getTestUid() {
      return test == null ? null : test.getUid();
   }

   public TestExecutionParameter addParameter(TestExecutionParameter param) {
      if (parameters == null) {
         parameters = new HashSet<TestExecutionParameter>();
      }
      parameters.add(param);
      return param;
   }

   public TestExecutionParameter addParameter(String name, String value) {
      return addParameter(new TestExecutionParameter(name, value));
   }

   public Tag addTag(String tag) {
      if (testExecutionTags == null) {
         testExecutionTags = new HashSet<TestExecutionTag>();
      }
      TestExecutionTag intermediate = new TestExecutionTag();
      Tag tagObj = new Tag();
      tagObj.setName(tag);
      intermediate.setTag(tagObj);
      testExecutionTags.add(intermediate);
      return tagObj;
   }

   public Value addValue(Value value) {
      if (values == null) {
         values = new HashSet<Value>();
      }
      values.add(value);
      return value;
   }

   public Value addValue(String metricName, Double value) {
      return addValue(new Value(metricName, value));
   }

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

   public List<TestExecutionParameter> getSortedParameters() {
      if (parameters == null || parameters.isEmpty()) {
         return new ArrayList<TestExecutionParameter>(0);
      } else {
         List<TestExecutionParameter> result = new ArrayList<TestExecutionParameter>(parameters);
         Collections.sort(result);
         return result;
      }
   }

   public Map<String, String> getParametersAsMap() {
      if (parameters == null || parameters.isEmpty()) {
         return new HashMap<>(0);
      } else {
         Map<String, String> r = new HashMap<>();
         for (TestExecutionParameter p : parameters) {
            r.put(p.getName(), p.getValue());
         }
         return r;
      }
   }

   public Map<String, Double> getResultValuesAsMap() {
      if (values == null || values.isEmpty()) {
         return new HashMap<>(0);
      } else {
         Map<String, Double> r = new HashMap<>();
         for (Value v : values) {
            r.put(v.getMetricName(), v.getResultValue());
         }
         return r;
      }
   }

   public Map<String, Value> getValuesAsMap() {
      if (values == null || values.isEmpty()) {
         return new HashMap<>(0);
      } else {
         Map<String, Value> r = new HashMap<>();
         for (Value v : values) {
            r.put(v.getMetricName(), v);
         }
         return r;
      }
   }
}