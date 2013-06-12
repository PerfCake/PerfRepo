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

   private static final long serialVersionUID = 1L;

   @Id
   @SequenceGenerator(name = "TEST_EXECUTION_ATTACHMENT_ID_GENERATOR", sequenceName = "TEST_EXECUTION_ATTACHMENT_SEQUENCE", allocationSize = 1)
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TEST_EXECUTION_ATTACHMENT_ID_GENERATOR")
   private Long id;

   @Column(name = "filename")
   private String filename;

   @Column(name = "mimetype")
   private String mimetype;

   @ManyToOne(optional = false, cascade = CascadeType.PERSIST)
   @JoinColumn(name = "test_execution_id", referencedColumnName = "id")
   private TestExecution testExecution;

   @Lob
   @Column(name = "content")
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