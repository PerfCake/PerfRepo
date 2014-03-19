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
import java.util.Date;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.jboss.qa.perfrepo.controller.JFreechartBean.XYLineChartSpec;
import org.jboss.qa.perfrepo.model.Metric;
import org.jboss.qa.perfrepo.model.Test;
import org.jboss.qa.perfrepo.model.TestExecution;
import org.jboss.qa.perfrepo.model.TestExecutionAttachment;
import org.jboss.qa.perfrepo.model.TestExecutionParameter;
import org.jboss.qa.perfrepo.model.TestExecutionTag;
import org.jboss.qa.perfrepo.model.Value;
import org.jboss.qa.perfrepo.model.ValueParameter;
import org.jboss.qa.perfrepo.model.builder.TestExecutionBuilder;
import org.jboss.qa.perfrepo.model.util.EntityUtil;
import org.jboss.qa.perfrepo.rest.TestExecutionREST;
import org.jboss.qa.perfrepo.service.ServiceException;
import org.jboss.qa.perfrepo.service.TestService;
import org.jboss.qa.perfrepo.session.UserSession;
import org.jboss.qa.perfrepo.util.FavoriteParameter;
import org.jboss.qa.perfrepo.util.MultiValue;
import org.jboss.qa.perfrepo.util.MultiValue.ParamInfo;
import org.jboss.qa.perfrepo.util.MultiValue.ValueInfo;
import org.jboss.qa.perfrepo.util.Util;
import org.jboss.qa.perfrepo.viewscope.ViewScoped;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.richfaces.event.FileUploadEvent;
import org.richfaces.model.UploadedFile;

/**
 * Details of {@link TestExecution}
 * 
 * @author Michal Linhard (mlinhard@redhat.com)
 * 
 */
@Named
@ViewScoped
public class TestExecutionController extends ControllerBase {

   private static final long serialVersionUID = 3012075520261954430L;
   private static final Logger log = Logger.getLogger(TestExecutionController.class);

   @Inject
   private TestService testService;

   @Inject
   private UserSession userSession;

   private TestExecution testExecution = null;
   private Test test = null;

   private TestExecutionParameter editedParameter = null;
   private Value editedValue = null;
   private Long editedValueMetricSelectionId = null;
   private TestExecution editedTestExecution = null;
   private FavoriteParameter editedFavoriteParameter = null;

   private List<ValueInfo> values = null;
   private List<FavoriteParameter> favoriteParameters = null;

   private List<ParamInfo> selectedMultiValueList = null;
   private List<String> selectedMultiValueParamSelectionList = null;
   private String selectedMultiValueParamSelection = null;
   private ValueInfo selectedMultiValue = null;
   private XYLineChartSpec chartData = null;

   private Long createForTest;
   private Long testExecutionId;

   public List<FavoriteParameter> getFavoriteParameters() {
      return favoriteParameters;
   }

   public FavoriteParameter getEditedFavoriteParameter() {
      return editedFavoriteParameter;
   }

   public void setEditedFavoriteParameter(String paramName) {
      editedFavoriteParameter = new FavoriteParameter();
      editedFavoriteParameter.setTestId(test.getId());
      editedFavoriteParameter.setParameterName(paramName);
      FavoriteParameter fp = findFavoriteParameter(paramName);
      if (fp != null) {
         editedFavoriteParameter.setLabel(fp.getLabel());
      } else {
         editedFavoriteParameter.setLabel("New label " + (favoriteParameters == null ? 0 : favoriteParameters.size()));
      }
   }

   public void unsetEditedFavoriteParameter() {
      editedFavoriteParameter = null;
   }

   public void saveEditedFavoriteParameter() {
      userSession.addFavoriteParameter(editedFavoriteParameter.getTestId(), editedFavoriteParameter.getParameterName(), editedFavoriteParameter.getLabel());
      favoriteParameters = userSession.getFavoriteParametersFor(test.getId());
   }

   public void removeFromFavorites(String paramName) {
      if (paramName == null || !isFavorite(paramName)) {
         log.error("incorrect request for removeFromFavorites");
         return;
      }
      userSession.removeFavoriteParameter(test.getId(), paramName);
      favoriteParameters = userSession.getFavoriteParametersFor(test.getId());
   }

   public Long getCreateForTest() {
      return createForTest;
   }

   public void setCreateForTest(Long createForTest) {
      this.createForTest = createForTest;
   }

   public String getRawTags() {
      return Util.rawTags(editedTestExecution == null ? null : editedTestExecution.getSortedTags());
   }

   public void setRawTags(String rawTags) {
      if (editedTestExecution == null) {
         return;
      }
      List<String> tags = Util.parseTags(rawTags);
      TestExecutionBuilder b = TestExecution.builder();
      for (String tag : tags) {
         b.tag(tag);
      }
      editedTestExecution.setTestExecutionTags(b.build().getTestExecutionTags());
   }

   public boolean isDisplayComment() {
      return testExecution != null && testExecution.getComment() != null && !testExecution.getComment().equals("");
   }

   public String getDisplayedComment() {
      if (isDisplayComment()) {
         return testExecution.getComment();
      } else {
         return "&nbsp;";
      }
   }

   public void setEditedTestExecution() {
      this.editedTestExecution = testExecution.clone();
   }

   public void unsetEditedTestExecution() {
      if (createForTest != null) {
         redirect("/test/" + createForTest);
      } else {
         this.editedTestExecution = null;
         redirect("/exec/" + testExecution.getId());
      }
   }

   public TestExecution getEditedTestExecution() {
      return editedTestExecution;
   }

   public void updateEditedTestExecution() {
      if (editedTestExecution != null) {
         try {
            if (editedTestExecution.getId() == null) {
               testExecution = testService.createTestExecution(editedTestExecution);
               redirectWithMessage("/exec/" + testExecution.getId(), INFO, "page.exec.successfullyCreated", testExecution.getName());
            } else {
               testExecution = testService.updateTestExecution(editedTestExecution);
               showMultiValue(null);
               redirectWithMessage("/exec/" + testExecution.getId(), INFO, "page.exec.successfullyUpdated", testExecution.getName());
            }
         } catch (ServiceException e) {
            addMessageFor(e);
         }
      }
   }

   public void setExecutionLocked(boolean locked) {
      try {
         testExecution = testService.setExecutionLocked(testExecution, locked);
         showMultiValue(null);
      } catch (ServiceException e) {
         addMessageFor(e);
      }
   }

   public Long getTestExecutionId() {
      return testExecutionId;
   }

   public void setTestExecutionId(Long testExecutionId) {
      this.testExecutionId = testExecutionId;
   }

   public void preRender() {
      reloadSessionMessages();
      if (testExecutionId == null) {
         if (createForTest == null) {
            log.error("No execution ID supplied");
            redirectWithMessage("/", ERROR, "page.exec.errorNoExecId");
         } else {
            if (testExecution == null) {
               test = testService.getFullTest(createForTest);
               if (test == null) {
                  log.error("Can't find test with id " + testExecution.getTest().getId());
                  redirectWithMessage("/", ERROR, "page.test.errorTestNotFound", testExecution.getTest().getId());
               }
               testExecution = new TestExecution();
               testExecution.setTest(test);
               testExecution.setLocked(Boolean.FALSE);
               testExecution.setStarted(new Date());
               testExecution.setName("New execution");
               setEditedTestExecution();
            }
         }
      } else {
         if (testExecution == null) {
            testExecution = testService.getFullTestExecution(testExecutionId);
            if (testExecution == null) {
               log.error("Can't find execution with id " + testExecutionId);
               redirectWithMessage("/", ERROR, "page.exec.errorExecNotFound", testExecutionId);
            } else {
               test = testService.getFullTest(testExecution.getTest().getId());
               if (test == null) {
                  log.error("Can't find test with id " + testExecution.getTest().getId());
                  redirectWithMessage("/", ERROR, "page.test.errorTestNotFound", testExecution.getTest().getId());
               } else {
                  values = MultiValue.createFrom(testExecution);
                  favoriteParameters = userSession.getFavoriteParametersFor(test.getId());
               }
               setEditedTestExecution();
            }
         }
      }
   }

   public TestExecution getTestExecution() {
      return testExecution;
   }

   public Test getTest() {
      return test;
   }

   public TestExecutionParameter getEditedParameter() {
      return editedParameter;
   }

   public void setEditedParameter(TestExecutionParameter param) {
      this.editedParameter = param == null ? null : param.clone();
   }

   public void unsetEditedParameter() {
      this.editedParameter = null;
   }

   public void unsetEditedParameterAndRemove() {
      removeFromFavorites(editedFavoriteParameter.getParameterName());
      editedFavoriteParameter = null;
   }

   public void createEditedParameter() {
      this.editedParameter = new TestExecutionParameter();
   }

   public Value getEditedValue() {
      return editedValue;
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
      return "Search";
   }

   public void deleteParameter(TestExecutionParameter param) {
      if (param != null) {
         try {
            testService.deleteParameter(param);
            EntityUtil.removeById(testExecution.getParameters(), param.getId());
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
      }
   }

   public void updateEditedParameter() {
      if (editedParameter != null) {
         TestExecution idHolder = new TestExecution();
         idHolder.setId(testExecutionId);
         editedParameter.setTestExecution(idHolder);
         try {
            TestExecutionParameter freshParam = testService.updateParameter(editedParameter);
            EntityUtil.removeById(testExecution.getParameters(), freshParam.getId());
            testExecution.getParameters().add(freshParam);
            editedParameter = null;
         } catch (ServiceException e) {
            addMessageFor(e);
            editedParameter = null;
         }
      }
   }

   public void setEditedValue(Value value) {
      this.editedValue = value == null ? null : value.cloneWithParameters();
      if (editedValue != null || editedValue.getParameters() != null) {
         if (editedValue.getParameters() instanceof List) {
            Collections.sort((List<ValueParameter>) editedValue.getParameters());
         }
      }
   }

   public Long getEditedValueMetricSelectionId() {
      return editedValueMetricSelectionId;
   }

   public void setEditedValueMetricSelectionId(Long editedValueMetricSelectionId) {
      this.editedValueMetricSelectionId = editedValueMetricSelectionId;
   }

   public void createEditedValue() {
      editedValue = new Value();
   }

   public void unsetEditedValue() {
      editedValue = null;
   }

   public void addEditedValueParameter() {
      if (editedValue == null) {
         log.error("can't add parameter, editedValue not set");
         return;
      }
      ValueParameter vp = new ValueParameter();
      if (editedValue.getParameters() == null) {
         editedValue.setParameters(new ArrayList<ValueParameter>(1));
      }
      vp.setName("param" + (editedValue.getParameters().size() + 1));
      editedValue.getParameters().add(vp);
   }

   public void removeEditedValueParameter(ValueParameter vp) {
      if (editedValue == null) {
         log.error("can't remove parameter, editedValue not set");
         return;
      }
      if (editedValue.getParameters() == null) {
         return;
      }
      editedValue.getParameters().remove(vp);
   }

   // this is also create method for value
   public void updateEditedValue() {
      if (editedValue != null) {
         TestExecution idHolder = new TestExecution();
         idHolder.setId(testExecutionId);
         editedValue.setTestExecution(idHolder);
         try {
            Value freshValue = null;
            if (editedValue.getId() == null) {
               Metric selectedMetric = EntityUtil.findById(test.getMetrics(), editedValueMetricSelectionId);
               if (selectedMetric == null) {
                  addMessage(ERROR, "page.exec.errorMetricMandatory");
                  return;
               }
               editedValue.setMetric(selectedMetric.clone());
               freshValue = testService.addValue(editedValue);
            } else {
               freshValue = testService.updateValue(editedValue);
               EntityUtil.removeById(testExecution.getValues(), freshValue.getId());
            }
            testExecution.getValues().add(freshValue);
            editedValue = null;
            ValueInfo prevValueInfo = MultiValue.find(values, freshValue);
            values = MultiValue.createFrom(testExecution);
            showMultiValue(prevValueInfo == null ? null : prevValueInfo.getMetricName());
         } catch (ServiceException e) {
            addMessageFor(e);
         }
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
         TestExecution idHolder = new TestExecution();
         idHolder.setId(testExecutionId);
         value.setTestExecution(idHolder);
         try {
            testService.deleteValue(value);
            EntityUtil.removeById(testExecution.getValues(), value.getId());
            ValueInfo prevValueInfo = MultiValue.find(values, value);
            values = MultiValue.createFrom(testExecution);
            showMultiValue(prevValueInfo.getMetricName());
         } catch (ServiceException e) {
            addMessageFor(e);
         }
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

   public List<ValueInfo> getValues() {
      return values;
   }

   public void updateParamSelection() {
      if (selectedMultiValue == null || !selectedMultiValue.isMultiValue()) {
         addMessage(ERROR, "page.exec.notMultiValue");
         return;
      }
      selectedMultiValueList = selectedMultiValue.getComplexValueByParamName(selectedMultiValueParamSelection);
      chartData = createChart(selectedMultiValueList, selectedMultiValue);
   }

   private void clearSelectedMultiValue() {
      selectedMultiValueParamSelectionList = null;
      selectedMultiValueParamSelection = null;
      selectedMultiValue = null;
      selectedMultiValueList = null;
      chartData = null;
   }

   public void showMultiValue(String metricName) {
      if (metricName == null) {
         clearSelectedMultiValue();
         return;
      }
      ValueInfo value = MultiValue.find(values, metricName);
      if (value == null || !value.isMultiValue()) {
         clearSelectedMultiValue();
         return;
      }
      selectedMultiValueParamSelectionList = value.getComplexValueParams();
      if (selectedMultiValueParamSelectionList.isEmpty()) {
         clearSelectedMultiValue();
         return;
      }
      Collections.sort(selectedMultiValueParamSelectionList);
      if (selectedMultiValueParamSelection == null) {
         selectedMultiValueParamSelection = selectedMultiValueParamSelectionList.get(0);
      }
      selectedMultiValueList = value.getComplexValueByParamName(selectedMultiValueParamSelection);
      selectedMultiValue = value;
      chartData = createChart(selectedMultiValueList, selectedMultiValue);
   }

   public XYLineChartSpec getChartData() {
      return chartData;
   }

   private XYLineChartSpec createChart(List<ParamInfo> values, ValueInfo mainValue) {
      if (selectedMultiValueList == null) {
         return null;
      }
      XYSeriesCollection dataset = new XYSeriesCollection();
      XYSeries series = new XYSeries(selectedMultiValueParamSelection);
      dataset.addSeries(series);
      try {
         for (ParamInfo pinfo : selectedMultiValueList) {
            Double paramValue = Double.valueOf(pinfo.getParamValue());
            if (paramValue != null) {
               series.add(paramValue, pinfo.getValue());
            }
         }
         XYLineChartSpec chartSpec = new XYLineChartSpec();
         chartSpec.title = "Multi-value for " + mainValue.getMetricName();
         chartSpec.xAxisLabel = selectedMultiValueParamSelection;
         chartSpec.yAxisLabel = "Metric value";
         chartSpec.dataset = dataset;
         return chartSpec;
      } catch (NumberFormatException e) {
         log.error("Can't chart non-numeric values");
         return null;
      } catch (Exception e) {
         log.error("Error while creating chart", e);
         return null;
      }
   }

   public List<ParamInfo> getSelectedMultiValueList() {
      return selectedMultiValueList;
   }

   public String getSelectedMultiValueParamSelection() {
      return selectedMultiValueParamSelection;
   }

   public void setSelectedMultiValueParamSelection(String selectedMultiValueParamSelection) {
      this.selectedMultiValueParamSelection = selectedMultiValueParamSelection;
   }

   public List<String> getSelectedMultiValueParamSelectionList() {
      return selectedMultiValueParamSelectionList;
   }

   public String displayValueFavParam(String param) {
      return displayValueTable(testExecution.findParameter(param));
   }

   public String displayValueTable(TestExecutionParameter param) {
      return Util.displayValue(param);
   }

   private FavoriteParameter findFavoriteParameter(String paramName) {
      if (paramName == null || favoriteParameters == null) {
         return null;
      }
      for (FavoriteParameter fp : favoriteParameters) {
         if (fp.getParameterName().equals(paramName)) {
            return fp;
         }
      }
      return null;
   }

   public boolean isFavorite(String paramName) {
      return findFavoriteParameter(paramName) != null;
   }

   public void uploadAttachment(FileUploadEvent event) throws Exception {
      UploadedFile item = event.getUploadedFile();
      TestExecutionAttachment attachment = new TestExecutionAttachment();
      attachment.setFilename(item.getName());
      attachment.setMimetype(item.getContentType());
      attachment.setContent(item.getData());
      attachment.setTestExecution(new TestExecution());
      attachment.getTestExecution().setId(testExecutionId);
      try {
         testService.addAttachment(attachment);
         testExecution = testService.getFullTestExecution(testExecutionId);
         showMultiValue(null);
      } catch (ServiceException e) {
         addMessageFor(e);
      }
   }

   public void deleteAttachment(TestExecutionAttachment attachment) {
      try {
         attachment.setTestExecution(new TestExecution());
         attachment.getTestExecution().setId(testExecutionId);
         testService.deleteAttachment(attachment);
         testExecution = testService.getFullTestExecution(testExecutionId);
         showMultiValue(null);
      } catch (ServiceException e) {
         addMessageFor(e);
      }
   }

}