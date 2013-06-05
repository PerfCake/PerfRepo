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
@Table(name = "value_parameter")
@NamedQueries({ @NamedQuery(name = ValueParameter.FIND_ALL, query = "SELECT x from ValueParameter x"), @NamedQuery(name = ValueParameter.FIND_ALL_SORTED_BY_ID_ASC, query = "SELECT x FROM ValueParameter x ORDER BY x.id ASC"), @NamedQuery(name = ValueParameter.FIND_ALL_SORTED_BY_ID_DESC, query = "SELECT x FROM ValueParameter x ORDER BY x.id DESC"), @NamedQuery(name = ValueParameter.FIND_ALL_SORTED_BY_NAME_ASC, query = "SELECT x FROM ValueParameter x ORDER BY x.name ASC"), @NamedQuery(name = ValueParameter.FIND_ALL_SORTED_BY_NAME_DESC, query = "SELECT x FROM ValueParameter x ORDER BY x.name DESC"), @NamedQuery(name = ValueParameter.FIND_ALL_SORTED_BY_VALUE_ASC, query = "SELECT x FROM ValueParameter x ORDER BY x.paramValue ASC"), @NamedQuery(name = ValueParameter.FIND_ALL_SORTED_BY_VALUE_DESC, query = "SELECT x FROM ValueParameter x ORDER BY x.paramValue DESC"),

@NamedQuery(name = ValueParameter.FIND_BY_ID, query = "SELECT x from ValueParameter x WHERE x.id = :" + ValueParameter.NQ_ID) })
@XmlRootElement(name = "valueParameter")
@Named("valueParameter")
@RequestScoped
public class ValueParameter implements Serializable {

   private static final long serialVersionUID = 1L;

   public static final String FIND_ALL = "ValueParameter.findAll";

   public static final String FIND_ALL_SORTED_BY_ID_ASC = "ValueParameter.findAllSortedByIdAsc";
   public static final String FIND_ALL_SORTED_BY_ID_DESC = "ValueParameter.findAllSortedByIdDesc";
   public static final String FIND_ALL_SORTED_BY_NAME_ASC = "ValueParameter.findAllSortedByNameAsc";
   public static final String FIND_ALL_SORTED_BY_NAME_DESC = "ValueParameter.findAllSortedByNameDesc";
   public static final String FIND_ALL_SORTED_BY_VALUE_ASC = "ValueParameter.findAllSortedByValueAsc";
   public static final String FIND_ALL_SORTED_BY_VALUE_DESC = "ValueParameter.findAllSortedByValueDesc";

   public static final String FIND_BY_ID = "ValueParameter.findById";
   public static final String NQ_ID = "valueParameterId";

   @Id
   @SequenceGenerator(name = "VALUE_PARAMETER_ID_GENERATOR", sequenceName = "VALUE_PARAMETER_SEQUENCE", allocationSize = 1)
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "VALUE_PARAMETER_ID_GENERATOR")
   private Long id;

   @Column(name = "name")
   private String name;
   
   @Column(name = "value")
   private String paramValue;
   
   @ManyToOne(optional = false, cascade = CascadeType.PERSIST)
   @JoinColumn(name = "value_id", referencedColumnName = "id")
   private Value value;

   @XmlTransient
   public Value getValue() {
      return value;
   }

   public void setValue(Value value) {
      this.value = value;
   }

   public ValueParameter() {
      super();

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

   public void setParamValue(String value) {
      this.paramValue = value;
   }

   @XmlAttribute(name = "paramValue")   
   public String getParamValue() {
      return this.paramValue;
   }
}