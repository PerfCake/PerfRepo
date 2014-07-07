package org.jboss.qa.perfrepo.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.jboss.qa.perfrepo.model.user.User;

import java.io.Serializable;

/**
 * Represents user's favorite parameter for a test.
 * 
 * @author Jiri Holusa (jholusa@redhat.com)
 * 
 */
@javax.persistence.Entity
@Table(name = "favorite_parameter")
@NamedQueries({
      @NamedQuery(name = FavoriteParameter.FIND_BY_TEST_AND_PARAM_NAME, query = "SELECT fp FROM FavoriteParameter fp JOIN fp.test test JOIN fp.user user WHERE test.id = :testId AND user.id = :userId AND fp.parameterName = :paramName")
              })
public class FavoriteParameter implements Entity<FavoriteParameter> {

   public static final String FIND_BY_TEST_AND_PARAM_NAME = "findByTestAndParamName";

   @Id
   @SequenceGenerator(name = "FAVORITE_PARAMETER_ID_GENERATOR", sequenceName = "FAVORITE_PARAMETER_SEQUENCE", allocationSize = 1)
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FAVORITE_PARAMETER_ID_GENERATOR")
   private Long id;

   @Column(name = "label")
   @NotNull
   @Size(max = 2047)
   private String label;

   @Column(name = "parameter_name")
   @NotNull
   @Size(max = 2047)
   private String parameterName;

   @ManyToOne(optional = false, cascade = CascadeType.PERSIST)
   @JoinColumn(name = "user_id", referencedColumnName = "id")
   @NotNull
   private User user;

   @ManyToOne(optional = false, cascade = CascadeType.PERSIST)
   @JoinColumn(name = "test_id", referencedColumnName = "id")
   @NotNull
   private Test test;

   public String getLabel() {
      return label;
   }

   public void setLabel(String label) {
      this.label = label;
   }

   public String getParameterName() {
      return parameterName;
   }

   public void setParameterName(String parameterName) {
      this.parameterName = parameterName;
   }

   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public User getUser() {
      return user;
   }

   public void setUser(User user) {
      this.user = user;
   }

   public Test getTest() {
      return test;
   }

   public void setTest(Test test) {
      this.test = test;
   }

   @Override
   public FavoriteParameter clone() {
      try {
         return (FavoriteParameter) super.clone();
      } catch (CloneNotSupportedException e) {
         throw new RuntimeException(e);
      }
   }

   @Override
   public String toString() {
      return "FavoriteParameter{" +
            "id=" + id +
            ", label='" + label + '\'' +
            ", parameterName='" + parameterName + '\'' +
            ", user=" + user +
            '}';
   }
}
