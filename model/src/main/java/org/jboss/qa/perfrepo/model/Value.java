package org.jboss.qa.perfrepo.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlID;
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
public class Value implements Serializable {

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

   public Value(String metricName, Double value) {
      super();
      this.metric = new Metric();
      this.metric.setName(metricName);
      this.resultValue = value;
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

   public void setMetric(Metric metric) {
      this.metric = metric;
   }

   @XmlElement(name = "metric")
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

   @XmlAttribute(name = "resultValue")
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

   @XmlElementWrapper(name = "valueParameters")
   @XmlElement(name = "valueParameter")
   public Set<ValueParameter> getParameters() {
      return this.parameters;
   }

   public String getMetricName() {
      return metric == null ? null : metric.getName();
   }

   public ValueParameter addParameter(ValueParameter param) {
      if (parameters == null) {
         parameters = new HashSet<ValueParameter>();
      }
      parameters.add(param);
      return param;
   }

   public ValueParameter addParameter(String name, String value) {
      return addParameter(new ValueParameter(name, value));
   }

   public Map<String, String> getParametersAsMap() {
      if (parameters == null || parameters.isEmpty()) {
         return new HashMap<>(0);
      } else {
         Map<String, String> r = new HashMap<>();
         for (ValueParameter p : parameters) {
            r.put(p.getName(), p.getParamValue());
         }
         return r;
      }
   }

}