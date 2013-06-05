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
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Table(name = "tag")
@NamedQueries({ @NamedQuery(name = Tag.FIND_ALL, query = "SELECT x from Tag x"), @NamedQuery(name = Tag.FIND_ALL_SORTED_BY_ID_ASC, query = "SELECT x FROM Tag x ORDER BY x.id ASC"), @NamedQuery(name = Tag.FIND_ALL_SORTED_BY_ID_DESC, query = "SELECT x FROM Tag x ORDER BY x.id DESC"), @NamedQuery(name = Tag.FIND_ALL_SORTED_BY_NAME_ASC, query = "SELECT x FROM Tag x ORDER BY x.name ASC"), @NamedQuery(name = Tag.FIND_ALL_SORTED_BY_NAME_DESC, query = "SELECT x FROM Tag x ORDER BY x.name DESC"),

@NamedQuery(name = Tag.FIND_BY_ID, query = "SELECT x from Tag x WHERE x.id = :" + Tag.NQ_ID) })
@XmlRootElement(name = "tag")
@Named("tag")
@RequestScoped
public class Tag implements Serializable {

   private static final long serialVersionUID = 1L;

   public static final String FIND_ALL = "Tag.findAll";

   public static final String FIND_ALL_SORTED_BY_ID_ASC = "Tag.findAllSortedByIdAsc";
   public static final String FIND_ALL_SORTED_BY_ID_DESC = "Tag.findAllSortedByIdDesc";
   public static final String FIND_ALL_SORTED_BY_NAME_ASC = "Tag.findAllSortedByNameAsc";
   public static final String FIND_ALL_SORTED_BY_NAME_DESC = "Tag.findAllSortedByNameDesc";

   public static final String FIND_BY_ID = "Tag.findById";
   public static final String NQ_ID = "tagId";

   @Id
   @SequenceGenerator(name = "TAG_ID_GENERATOR", sequenceName = "TAG_SEQUENCE", allocationSize = 1)
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TAG_ID_GENERATOR")
   private Long id;

   @Column(name = "name")
   private String name;
   
   @OneToMany(mappedBy = "tag")
   private Collection<TestExecutionTag> testExecutionTags;

 
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

   public void setTestExecutionTags(Collection<TestExecutionTag> testExecutionTags) {
      this.testExecutionTags = testExecutionTags;
   }

   @XmlTransient
   public Collection<TestExecutionTag> getTestExecutionTags() {
      return this.testExecutionTags;
   }

}