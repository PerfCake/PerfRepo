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
@Table(name = "test_metric")
   @NamedQueries({ @NamedQuery(name = TestMetric.FIND_ALL, query = "SELECT x from TestMetric x"), 
   @NamedQuery(name = TestMetric.FIND_ALL_SORTED_BY_ID_ASC, query = "SELECT x FROM TestMetric x ORDER BY x.id ASC"), 
   @NamedQuery(name = TestMetric.FIND_ALL_SORTED_BY_ID_DESC, query = "SELECT x FROM TestMetric x ORDER BY x.id DESC"), 
   @NamedQuery(name = TestMetric.FIND_BY_METRIC, query = "SELECT x FROM TestMetric x WHERE x.metric.id = :" + Metric.NQ_ID), 
   @NamedQuery(name = TestMetric.FIND_BY_TEST, query = "SELECT x FROM TestMetric x WHERE x.test.id = :" + Test.NQ_ID),

@NamedQuery(name = TestMetric.FIND_BY_ID, query = "SELECT x from TestMetric x WHERE x.id = :" + TestMetric.NQ_ID) })
@XmlRootElement(name = "testMetric")
@Named("testMetric")
@RequestScoped
public class TestMetric implements Serializable {

   private static final long serialVersionUID = 1L;

   public static final String FIND_ALL = "TestMetric.findAll";

   public static final String FIND_ALL_SORTED_BY_ID_ASC = "TestMetric.findAllSortedByIdAsc";
   public static final String FIND_ALL_SORTED_BY_ID_DESC = "TestMetric.findAllSortedByIdDesc";
   public static final String FIND_BY_METRIC = "TestMetric.findByMetric";
   public static final String FIND_BY_TEST = "TestMetric.findByTest";

   public static final String FIND_BY_ID = "TestMetric.findById";
   public static final String NQ_ID = "testMetricId";

   @Id
   @SequenceGenerator(name = "TEST_METRIC_ID_GENERATOR", sequenceName = "TEST_METRIC_SEQUENCE", allocationSize = 1)
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TEST_METRIC_ID_GENERATOR")
   private Long id;

   @ManyToOne(optional = false, cascade = CascadeType.PERSIST)
   @JoinColumn(name = "metric_id", referencedColumnName = "id")
   private Metric metric;
   
   @ManyToOne(optional = false, cascade = CascadeType.PERSIST)
   @JoinColumn(name = "test_id", referencedColumnName = "id")
   private Test test;

   public TestMetric() {
      this.metric = new Metric();
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

   public void setMetric(Metric metric) {
      this.metric = metric;
   }

   @XmlElement(name = "metric")
   public Metric getMetric() {
      return this.metric;
   }
   
   
   public void setTest(Test test) {
      this.test = test;
   }

   @XmlTransient
   public Test getTest() {
      return this.test;
   }

}