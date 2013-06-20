package org.jboss.qa.perfrepo.model.to;

import java.util.Date;
import java.util.List;

public class TestExecutionSearchTO {
   
   private Date startedFrom;
   private Date startedTo;
   private String tags;
   
   private List<String> tagList;
   
   private String testUID;
   
   private String testName;

   public Date getStartedFrom() {
      return startedFrom;
   }

   public void setStartedFrom(Date startedFrom) {
      this.startedFrom = startedFrom;
   }

   public Date getStartedTo() {
      return startedTo;
   }

   public void setStartedTo(Date startedTo) {
      this.startedTo = startedTo;
   }

   public List<String> getTagList() {
      return tagList;
   }

   public void setTagList(List<String> tagList) {
      this.tagList = tagList;
   }

   public String getTestUID() {
      return testUID;
   }

   public void setTestUID(String testUID) {
      this.testUID = testUID;
   }

   public String getTestName() {
      return testName;
   }

   public void setTestName(String testName) {
      this.testName = testName;
   }

   public String getTags() {
      return tags;
   }

   public void setTags(String tags) {
      this.tags = tags;
   }
   
}
