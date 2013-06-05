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
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Table(name = "metric")
@NamedQueries({ 
   @NamedQuery(name = Metric.FIND_ALL, query = "SELECT x from Metric x"), 
   @NamedQuery(name = Metric.FIND_ALL_SORTED_BY_ID_ASC, query = "SELECT x FROM Metric x ORDER BY x.id ASC"), 
   @NamedQuery(name = Metric.FIND_ALL_SORTED_BY_ID_DESC, query = "SELECT x FROM Metric x ORDER BY x.id DESC"), 
   @NamedQuery(name = Metric.FIND_ALL_SORTED_BY_COMPARATOR_ASC, query = "SELECT x FROM Metric x ORDER BY x.comparator ASC"), 
   @NamedQuery(name = Metric.FIND_ALL_SORTED_BY_COMPARATOR_DESC, query = "SELECT x FROM Metric x ORDER BY x.comparator DESC"), 
   @NamedQuery(name = Metric.FIND_ALL_SORTED_BY_NAME_ASC, query = "SELECT x FROM Metric x ORDER BY x.name ASC"), 
   @NamedQuery(name = Metric.FIND_ALL_SORTED_BY_NAME_DESC, query = "SELECT x FROM Metric x ORDER BY x.name DESC"),
@NamedQuery(name = Metric.FIND_BY_ID, query = "SELECT x from Metric x WHERE x.id = :" + Metric.NQ_ID) })
@XmlRootElement(name = "metric")
@Named("metric")
@RequestScoped
public class Metric implements Serializable {

   private static final long serialVersionUID = 1L;

   public static final String FIND_ALL = "Metric.findAll";

   public static final String FIND_ALL_SORTED_BY_ID_ASC = "Metric.findAllSortedByIdAsc";
   public static final String FIND_ALL_SORTED_BY_ID_DESC = "Metric.findAllSortedByIdDesc";
   public static final String FIND_ALL_SORTED_BY_COMPARATOR_ASC = "Metric.findAllSortedByComparatorAsc";
   public static final String FIND_ALL_SORTED_BY_COMPARATOR_DESC = "Metric.findAllSortedByComparatorDesc";
   public static final String FIND_ALL_SORTED_BY_NAME_ASC = "Metric.findAllSortedByNameAsc";
   public static final String FIND_ALL_SORTED_BY_NAME_DESC = "Metric.findAllSortedByNameDesc";

   public static final String FIND_BY_ID = "Metric.findById";
   public static final String NQ_ID = "metricId";

   @Id
   @SequenceGenerator(name = "METRIC_ID_GENERATOR", sequenceName = "METRIC_SEQUENCE", allocationSize = 1)
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "METRIC_ID_GENERATOR")
   private Long id;

   @Column(name = "comparator")
   private String comparator;
   
   @Column(name = "name")
   private String name;
   
   @OneToMany(mappedBy = "metric")
   private Collection<Value> values;
   
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

   public void setComparator(String comparator) {
      this.comparator = comparator;
   }

   @XmlAttribute(name = "comparator")
   public String getComparator() {
      return this.comparator;
   }

   public void setName(String name) {
      this.name = name;
   }

   @XmlAttribute(name = "name")
   public String getName() {
      return this.name;
   }

   public void setValues(Collection<Value> values) {
      this.values = values;
   }

   @XmlTransient
   public Collection<Value> getValues() {
      return this.values;
   }

   @XmlElement(name="description")
   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }
   
   

}