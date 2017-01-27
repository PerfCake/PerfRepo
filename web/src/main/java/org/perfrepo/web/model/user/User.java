/**
 * PerfRepo
 * <p>
 * Copyright (C) 2015 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.perfrepo.web.model.user;

import org.hibernate.validator.constraints.Email;
import org.perfrepo.web.model.Entity;
import org.perfrepo.web.model.Test;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@javax.persistence.Entity
@Table(name = "\"user\"")
@NamedQueries({
    @NamedQuery(name = User.GET_SUBSCRIBERS_FOR_TEST, query = "SELECT distinct user from User user join user.subscribedTests test where test.id = :testId")
})
public class User implements Entity<User>, Comparable<User> {

   private static final long serialVersionUID = 4616015836066622075L;

   public static final String GET_SUBSCRIBERS_FOR_TEST = "User.getSubscribersForTest";

   @Id
   @SequenceGenerator(name = "USER_ID_GENERATOR", sequenceName = "USER_SEQUENCE", allocationSize = 1)
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_ID_GENERATOR")
   private Long id;

   @Column(name = "username", unique = true, updatable = false)
   @NotNull(message = "{user.usernameRequired}")
   @Size(max = 2047)
   private String username;

   @Column(name = "password")
   @NotNull(message = "{user.passwordRequired}")
   @Size(max = 300)
   private String password;

   @Column(name = "first_name")
   @NotNull(message = "{user.firstNameRequired}")
   @Size(max = 2047)
   private String firstName;

   @Column(name = "last_name")
   @NotNull(message = "{user.lastNameRequired}")
   @Size(max = 2047)
   private String lastName;

   @Column(name = "email")
   @NotNull(message = "{user.emailRequired}")
   @Email
   @Size(max = 2047)
   private String email;

   @Column(name = "type")
   @Enumerated(EnumType.STRING)
   private UserType type = UserType.REGULAR_USER;

   @ManyToMany(mappedBy = "subscribers")
   private Set<Test> subscribedTests = new HashSet<>();

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

   public String getPassword() {
      return password;
   }

   public void setPassword(String password) {
      this.password = password;
   }

   public String getFirstName() {
      return firstName;
   }

   public void setFirstName(String firstName) {
      this.firstName = firstName;
   }

   public String getLastName() {
      return lastName;
   }

   public void setLastName(String lastName) {
      this.lastName = lastName;
   }

   public String getEmail() {
      return email;
   }

   public void setEmail(String email) {
      this.email = email;
   }

   public UserType getType() {
      return type;
   }

   public void setType(UserType type) {
      this.type = type;
   }

   public Set<Test> getSubscribedTests() {
      return subscribedTests;
   }

   public void setSubscribedTests(Set<Test> subscribedTests) {
      this.subscribedTests = subscribedTests;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof User)) return false;

      User user = (User) o;

      return getUsername() != null ? getUsername().equals(user.getUsername()) : user.getUsername() == null;
   }

   @Override
   public int hashCode() {
      return getUsername() != null ? getUsername().hashCode() : 0;
   }

   @Override
   public int compareTo(User o) {
      return this.username.compareTo(o.username);
   }

   @Override
   public String toString() {
      return "User{" +
              "email='" + email + '\'' +
              ", firstName='" + firstName + '\'' +
              ", id=" + id +
              ", lastName='" + lastName + '\'' +
              ", password='" + password + '\'' +
              ", username='" + username + '\'' +
              '}';
   }

   public boolean isSuperAdmin() {
      return type == UserType.SUPER_ADMIN;
   }

   public enum UserType {
      ROBOT, REGULAR_USER, SUPER_ADMIN;
   }
}