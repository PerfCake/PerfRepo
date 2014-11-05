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
package org.perfrepo.model;

import org.perfrepo.model.auth.EntityType;
import org.perfrepo.model.auth.SecuredEntity;
import org.perfrepo.model.user.User;

import javax.persistence.CascadeType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@javax.persistence.Entity
@Table(name = "test_subscriber")
@SecuredEntity(type = EntityType.TEST, parent="test")
public class TestSubscriber implements Entity<TestSubscriber> {

   private static final long serialVersionUID = 1L;

   @Id
   @SequenceGenerator(name = "TEST_SUBSCRIBER_ID_GENERATOR", sequenceName = "TEST_SUBSCRIBER_SEQUENCE", allocationSize = 1)
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TEST_SUBSCRIBER_ID_GENERATOR")
   private Long id;

   @ManyToOne(optional = false, cascade = CascadeType.PERSIST)
   @JoinColumn(name = "test_id", referencedColumnName = "id")
   private Test test;

   @ManyToOne(optional = false, cascade = CascadeType.PERSIST)
   @JoinColumn(name = "user_id", referencedColumnName = "id")
   private User user;

   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public void setTest(Test test) {
      this.test = test;
   }

   public Test getTest() {
      return this.test;
   }

   public void setUser(User user) {
      this.user = user;
   }

   public User getUser() {
      return this.user;
   }

   @Override
   public TestSubscriber clone() {
      try {
         return (TestSubscriber) super.clone();
      } catch (CloneNotSupportedException e) {
         throw new RuntimeException(e);
      }
   }
}