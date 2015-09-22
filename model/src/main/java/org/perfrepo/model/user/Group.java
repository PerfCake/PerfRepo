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
package org.perfrepo.model.user;

import org.perfrepo.model.Entity;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.Collection;

@javax.persistence.Entity
@Table(name = "\"group\"")
public class Group implements Entity<Group>, Comparable<Group> {

   private static final long serialVersionUID = -9158731656089441951L;

   @Id
   @SequenceGenerator(name = "GROUP_ID_GENERATOR", sequenceName = "GROUP_SEQUENCE", allocationSize = 1)
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GROUP_ID_GENERATOR")
   private Long id;

   @Column(name = "name")
   private String name;

   @ManyToMany(fetch = FetchType.LAZY, mappedBy = "groups")
   private Collection<User> users;

   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public Collection<User> getUsers() {
      return users;
   }

   public void setUsers(Collection<User> users) {
      this.users = users;
   }

   @Override
   public int compareTo(Group group) {
      return this.name.compareTo(group.name);
   }

   @Override
   public Group clone() {
      try {
         return (Group) super.clone();
      } catch (CloneNotSupportedException e) {
         throw new RuntimeException(e);
      }
   }
}
