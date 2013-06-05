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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Table(name = "value")
@NamedQueries({ @NamedQuery(name = Value.FIND_ALL, query = "SELECT x from Value x"), @NamedQuery(name = Value.FIND_ALL_SORTED_BY_ID_ASC, query = "SELECT x FROM Value x ORDER BY x.id ASC"), @NamedQuery(name = Value.FIND_ALL_SORTED_BY_ID_DESC, query = "SELECT x FROM Value x ORDER BY x.id DESC"), @NamedQuery(name = Value.FIND_BY_METRIC, query = "SELECT x FROM Value x WHERE x.metric.id = :" + Metric.NQ_ID), @NamedQuery(name = Value.FIND_ALL_SORTED_BY_NAME_ASC, query = "SELECT x FROM Value x ORDER BY x.name ASC"), @NamedQuery(name = Value.FIND_ALL_SORTED_BY_NAME_DESC, query = "SELECT x FROM Value x ORDER BY x.name DESC"), @NamedQuery(name = Value.FIND_ALL_SORTED_BY_RESULT_VALUE_ASC, query = "SELECT x FROM Value x ORDER BY x.resultValue ASC"), @NamedQuery(name = Value.FIND_ALL_SORTED_BY_RESULT_VALUE_DESC, query = "SELECT x FROM Value x ORDER BY x.resultValue DESC"), @NamedQuery(name = Value.FIND_BY_TEST_EXECUTION, query = "SELECT x FROM Value x WHERE x.testExecution.id = :" + TestExecution.NQ_ID),

@NamedQuery(name = Value.FIND_BY_ID, query = "SELECT x from Value x WHERE x.id = :" + Value.NQ_ID) })
@XmlRootElement(name = "value")
@Named("value")
@RequestScoped
public class Value implements Serializable {

   private static final long serialVersionUID = 1L;

   public static final String FIND_ALL = "Value.findAll";

   public static final String FIND_ALL_SORTED_BY_ID_ASC = "Value.findAllSortedByIdAsc";
   public static final String FIND_ALL_SORTED_BY_ID_DESC = "Value.findAllSortedByIdDesc";
   public static final String FIND_BY_METRIC = "Value.findByMetric";
   public static final String FIND_ALL_SORTED_BY_NAME_ASC = "Value.findAllSortedByNameAsc";
   public static final String FIND_ALL_SORTED_BY_NAME_DESC = "Value.findAllSortedByNameDesc";
   public static final String FIND_ALL_SORTED_BY_RESULT_VALUE_ASC = "Value.findAllSortedByResultValueAsc";
   public static final String FIND_ALL_SORTED_BY_RESULT_VALUE_DESC = "Value.findAllSortedByResultValueDesc";
   public static final String FIND_BY_TEST_EXECUTION = "Value.findByTestExecution";

   public static final String FIND_BY_ID = "Value.findById";
   public static final String NQ_ID = "valueId";

   @Id
   @SequenceGenerator(name = "VALUE_ID_GENERATOR", sequenceName = "VALUE_SEQUENCE", allocationSize = 1)
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "VALUE_ID_GENERATOR")
   private Long id;

   @ManyToOne(optional = false, cascade = CascadeType.PERSIST)
   @JoinColumn(name = "metric_id", referencedColumnName = "id")
   private Metric metric;
   
   @Column(name = "name")
   private String name;
   
   @Column(name = "result_value")
   private Double resultValue;
   
   @ManyToOne(optional = false, cascade = CascadeType.PERSIST)
   @JoinColumn(name = "test_execution_id", referencedColumnName = "id")
   private TestExecution testExecution;
   
   @OneToMany(mappedBy = "value")
   private Set<ValueParameter> valueParameters;

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

   @XmlID
   @XmlAttribute(name = "id")
   public String getStringId() {
      return String.valueOf(id);
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

   public void setValueParameters(Set<ValueParameter> valueParameters) {
      this.valueParameters = valueParameters;
   }

   @XmlElementWrapper(name = "valueParameters")
   @XmlElement(name = "valueParameter")
   public Set<ValueParameter> getValueParameters() {
      return this.valueParameters;
   }

}