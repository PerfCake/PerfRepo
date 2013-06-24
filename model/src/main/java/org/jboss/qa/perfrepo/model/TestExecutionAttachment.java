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

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 
 * A binary file that can be attached to test execution.
 * 
 * @author Michal Linhard (mlinhard@redhat.com)
 * 
 */
@Entity
@Table(name = "test_execution_attachment")
public class TestExecutionAttachment implements Serializable {

   @Id
   @SequenceGenerator(name = "TEST_EXECUTION_ATTACHMENT_ID_GENERATOR", sequenceName = "TEST_EXECUTION_ATTACHMENT_SEQUENCE", allocationSize = 1)
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TEST_EXECUTION_ATTACHMENT_ID_GENERATOR")
   private Long id;

   @Column(name = "filename")
   @NotNull
   @Size(max = 2047)
   private String filename;

   @Column(name = "mimetype")
   @NotNull
   @Size(max = 255)
   private String mimetype;

   @ManyToOne(optional = false, cascade = CascadeType.PERSIST)
   @JoinColumn(name = "test_execution_id", referencedColumnName = "id")
   private TestExecution testExecution;

   @Lob
   @Column(name = "content")
   @NotNull
   @Size(max = 1048576)
   private byte[] content;

   public TestExecutionAttachment() {
      this.testExecution = new TestExecution();
   }

   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public String getStringId() {
      return id == null ? null : String.valueOf(id);
   }

   public void setStringId(String id) {
      this.id = Long.valueOf(id);
   }

   public void setTestExecution(TestExecution testExecution) {
      this.testExecution = testExecution;
   }

   public TestExecution getTestExecution() {
      return this.testExecution;
   }

   public String getFilename() {
      return filename;
   }

   public void setFilename(String filename) {
      this.filename = filename;
   }

   public String getMimetype() {
      return mimetype;
   }

   public void setMimetype(String mimetype) {
      this.mimetype = mimetype;
   }

   public byte[] getContent() {
      return content;
   }

   public void setContent(byte[] content) {
      this.content = content;
   }

}