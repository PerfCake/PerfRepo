/* 
 * Copyright 2013 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.qa.perfrepo.model;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@javax.persistence.Entity
@Table(name = "\"user\"")
public class User implements Entity<User>, Comparable<User> {

   @Id
   @SequenceGenerator(name = "USER_ID_GENERATOR", sequenceName = "USER_SEQUENCE", allocationSize = 1)
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_ID_GENERATOR")
   private Long id;

   @Column(name = "username")
   @NotNull
   @Size(max = 2047)
   private String username;

   @Column(name = "email")
   @NotNull
   @Size(max = 2047)
   private String email;

   @OneToMany(mappedBy = "user")
   private Collection<UserProperty> properties;

   @OneToMany(mappedBy = "user")
   private Collection<FavoriteParameter> favoriteParameters;

   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public String getUsername() {
      return username;
   }

   public void setUsername(String username) {
      this.username = username;
   }

   public String getEmail() {
      return email;
   }

   public void setEmail(String email) {
      this.email = email;
   }

   public Collection<UserProperty> getProperties() {
      return properties;
   }

   public void setProperties(Collection<UserProperty> properties) {
      this.properties = properties;
   }

   public Collection<FavoriteParameter> getFavoriteParameters() {
      return favoriteParameters;
   }

   public void setFavoriteParameters(Collection<FavoriteParameter> favoriteParameters) {
      this.favoriteParameters = favoriteParameters;
   }

   @Override
   public int compareTo(User o) {
      return this.username.compareTo(o.username);
   }

   @Override
   public User clone() {
      try {
         return (User) super.clone();
      } catch (CloneNotSupportedException e) {
         throw new RuntimeException(e);
      }
   }

   public UserProperty findProperty(String name) {
      if (properties == null) {
         return null;
      }
      for (UserProperty prop : properties) {
         if (name.equals(prop.getName())) {
            return prop;
         }
      }
      return null;
   }

}