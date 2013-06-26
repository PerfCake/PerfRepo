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
package org.jboss.qa.perfrepo.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.jboss.qa.perfrepo.model.Metric;
import org.jboss.qa.perfrepo.model.TestExecution;
import org.jboss.qa.perfrepo.model.TestExecutionAttachment;
import org.jboss.qa.perfrepo.model.TestExecutionParameter;
import org.jboss.qa.perfrepo.model.TestExecutionTag;
import org.jboss.qa.perfrepo.model.Value;
import org.jboss.qa.perfrepo.rest.TestExecutionREST;
import org.jboss.qa.perfrepo.service.ServiceException;
import org.jboss.qa.perfrepo.service.TestService;
import org.jboss.qa.perfrepo.viewscope.ViewScoped;

@Named
@ViewScoped
public class TestExecutionController extends ControllerBase {

   private static final long serialVersionUID = 3012075520261954430L;

   @Inject
   private TestService testService;

   private TestExecution testExecution = null;

   private TestExecutionParameter testExecutionParameter = null;

   private TestExecutionTag testExecutionTag = null;

   private Value value = null;

   public TestExecution getTestExecution() {
      String id;
      if (testExecution == null) {
         if ((id = getRequestParam("testExecutionId")) != null) {
            testExecution = testService.getTestExecution(Long.valueOf(id));
         } else {
            //TODO: is it ok?
            testExecution = new TestExecution();
         }
      }
      return testExecution;
   }

   public TestExecutionParameter getTestExecutionParameter() {
      return testExecutionParameter;
   }

   public void setTestExecutionParameter(TestExecutionParameter tep) {
      this.testExecutionParameter = tep;
   }

   public void newTestExecutionParameter() {
      this.testExecutionParameter = new TestExecutionParameter();
   }

   public TestExecutionTag getTestExecutionTag() {
      return testExecutionTag;
   }

   public Value getValue() {
      return value;
   }

   public void setValue(Value value) {
      this.value = value;
   }

   public void setTestExecutionTag(TestExecutionTag testExecutionTag) {
      this.testExecutionTag = testExecutionTag;
   }

   public void newTestExecutionTag() {
      this.testExecutionTag = new TestExecutionTag();
   }

   public String update() {
      if (testExecution != null) {
         try {
            testService.updateTestExecution(testExecution);
         } catch (ServiceException e) {
            //TODO: how to handle web-layer exceptions ?
            throw new RuntimeException(e);
         }
      }
      return "/testExecution/detail.xhtml?testExecutionId=";
   }

   public List<TestExecutionParameter> getTestExecutionParameters() {

      return testExecution.getSortedParameters();

   }

   public List<TestExecutionTag> getTestExecutionTags() {
      List<TestExecutionTag> tegs = new ArrayList<TestExecutionTag>();
      if (testExecution != null && testExecution.getTestExecutionTags() != null) {
         tegs.addAll(testExecution.getTestExecutionTags());
      }
      return tegs;
   }

   public List<Value> getTestExecutionValues() {
      List<Value> values = new ArrayList<Value>();
      if (testExecution != null && testExecution.getValues() != null) {
         values.addAll(testExecution.getValues());
      }
      return values;
   }

   public Collection<TestExecutionAttachment> getAttachments() {
      return testExecution == null ? Collections.<TestExecutionAttachment> emptyList() : testExecution.getAttachments();
   }

   public String delete() {
      TestExecution objectToDelete = testExecution;
      if (testExecution == null) {
         objectToDelete = new TestExecution();
         objectToDelete.setId(new Long(getRequestParam("testExecutionId")));
      }
      try {
         testService.deleteTestExecution(objectToDelete);
      } catch (Exception e) {
         // TODO: how to handle web-layer exceptions ?
         throw new RuntimeException(e);
      }
      return "Home";
   }

   public void deleteTestExecutionParamenter(TestExecutionParameter param) {
      if (param != null) {
         try {
            testService.deleteTestExecutionParameter(param);
            testExecution.getParameters().remove(param);
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
      }
   }

   public void addTestExecutionParameter() {
      if (testExecutionParameter != null && testExecution != null) {
         try {
            TestExecutionParameter tep = testService.addTestExecutionParameter(testExecution, testExecutionParameter);
            testExecution.getParameters().add(tep);
            testExecutionParameter = null;
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
      } else {
         throw new RuntimeException("parameters are not set");
      }
   }

   public void updateTestExecutionParameter() {
      if (testExecutionParameter != null) {
         try {
            testExecutionParameter.setTestExecution(testExecution);
            testService.updateTestExecutionParameter(testExecutionParameter);
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
      }
   }

   public void addTestExecutionTag() {
      if (testExecutionTag != null && testExecution != null) {
         try {
            TestExecutionTag teg = testService.addTestExecutionTag(testExecution, testExecutionTag);
            testExecution.getTestExecutionTags().add(teg);
            testExecutionTag = null;
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
      } else {
         throw new RuntimeException("parameters are not set");
      }
   }

   public void deleteTestExecutionTag(TestExecutionTag teg) {
      if (teg != null) {
         testService.deleteTestExecutionTag(teg);
         testExecution.getTestExecutionTags().remove(teg);
      }
   }

   public void createValue() {
      value = new Value();
   }

   public void addValue() {
      if (value != null && testExecution != null) {
         Value v = testService.addValue(testExecution, value);
         testExecution.getValues().add(v);

      }
   }

   public void updateValue() {
      if (value != null) {
         testService.updateValue(value);
      }
   }

   public List<Metric> getTestMetric() {
      if (testExecution != null) {
         return testService.getTestMetrics(testExecution.getTest());
      }
      return null;
   }

   public void deleteValue(Value value) {
      if (value != null) {
         testService.deleteValue(value);
         testExecution.getValues().remove(value);
      }
   }

   /**
    * Produce download link for an attachment. It will be an URL for the
    * {@link TestExecutionREST#getAttachment(Long)} method.
    * 
    * @param attachment
    * @return The download link.
    */
   public String getDownloadLink(TestExecutionAttachment attachment) {
      HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
      return request.getContextPath() + "/rest/testExecution/attachment/" + attachment.getId();
   }

}