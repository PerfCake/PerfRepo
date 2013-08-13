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

import org.apache.log4j.Logger;
import org.jboss.qa.perfrepo.controller.JFreechartBean.XYLineChartSpec;
import org.jboss.qa.perfrepo.model.Metric;
import org.jboss.qa.perfrepo.model.TestExecution;
import org.jboss.qa.perfrepo.model.TestExecutionAttachment;
import org.jboss.qa.perfrepo.model.TestExecutionParameter;
import org.jboss.qa.perfrepo.model.TestExecutionTag;
import org.jboss.qa.perfrepo.model.Value;
import org.jboss.qa.perfrepo.model.to.MultiValue;
import org.jboss.qa.perfrepo.model.to.MultiValue.ParamInfo;
import org.jboss.qa.perfrepo.model.to.MultiValue.ValueInfo;
import org.jboss.qa.perfrepo.rest.TestExecutionREST;
import org.jboss.qa.perfrepo.service.ServiceException;
import org.jboss.qa.perfrepo.service.TestService;
import org.jboss.qa.perfrepo.viewscope.ViewScoped;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

@Named
@ViewScoped
public class TestExecutionController extends ControllerBase {

   private static final long serialVersionUID = 3012075520261954430L;
   private static final Logger log = Logger.getLogger(TestExecutionController.class);

   @Inject
   private TestService testService;

   private TestExecution testExecution = null;

   private TestExecutionParameter parameter = null;

   private TestExecutionTag testExecutionTag = null;

   private Value value = null;

   private List<ValueInfo> values = null;

   private List<ParamInfo> selectedMultiValueList = null;
   private List<String> selectedMultiValueParamSelectionList = null;
   private String selectedMultiValueParamSelection = null;
   private ValueInfo selectedMultiValue = null;
   private XYLineChartSpec chartData = null;

   private boolean editMode;
   private boolean createMode;
   private Long testExecutionId;

   public Long getTestExecutionId() {
      return testExecutionId;
   }

   public void setTestExecutionId(Long testExecutionId) {
      this.testExecutionId = testExecutionId;
   }

   public boolean isEditMode() {
      return editMode;
   }

   public void setEditMode(boolean editMode) {
      this.editMode = editMode;
   }

   public boolean isCreateMode() {
      return createMode;
   }

   public void setCreateMode(boolean createMode) {
      this.createMode = createMode;
   }

   public void preRender() {
      reloadSessionMessages();
      if (testExecutionId == null) {
         if (!createMode) {
            log.error("No execution ID supplied");
            redirectWithMessage("/", ERROR, "page.exec.errorNoExecId");
         } else {
            if (testExecution == null) {
               testExecution = new TestExecution();
            }
         }
      } else {
         if (testExecution == null) {
            testExecution = testService.getFullTestExecution(testExecutionId);
            if (testExecution == null) {
               log.error("Can't find execution with id " + testExecutionId);
               redirectWithMessage("/", ERROR, "page.exec.errorExecNotFound", testExecutionId);
            } else {
               values = MultiValue.createFrom(testExecution);
            }
         }
      }
   }

   public TestExecution getTestExecution() {
      return testExecution;
   }

   public TestExecutionParameter getParameter() {
      return parameter;
   }

   public void setParameter(TestExecutionParameter param) {
      this.parameter = param;
   }

   public void unsetParameter() {
      this.parameter = null;
   }

   public void newTestExecutionParameter() {
      this.parameter = new TestExecutionParameter();
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
      if (parameter != null && testExecution != null) {
         try {
            TestExecutionParameter tep = testService.addTestExecutionParameter(testExecution, parameter);
            testExecution.getParameters().add(tep);
            parameter = null;
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
      } else {
         throw new RuntimeException("parameters are not set");
      }
   }

   public void updateTestExecutionParameter() {
      if (parameter != null) {
         TestExecution idHolder = new TestExecution();
         idHolder.setId(testExecutionId);
         parameter.setTestExecution(idHolder);
         try {
            TestExecutionParameter freshParam = testService.updateTestExecutionParameter(parameter);
            for (TestExecutionParameter param : testExecution.getParameters()) {
               if (param.getId().equals(freshParam.getId())) {
                  testExecution.getParameters().remove(param);
                  break;
               }
            }
            testExecution.getParameters().add(freshParam);
            parameter = null;
         } catch (ServiceException e) {
            addMessageFor(e);
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

   public void showMultiValue(ValueInfo value) {
      if (value == null || !value.isMultiValue()) {
         addMessage(ERROR, "page.exec.notMultiValue");
         return;
      }
      selectedMultiValueParamSelectionList = value.getComplexValueParams();
      if (selectedMultiValueParamSelectionList.isEmpty()) {
         addMessage(ERROR, "page.exec.notMultiValue");
         selectedMultiValueParamSelectionList = null;
         return;
      }
      Collections.sort(selectedMultiValueParamSelectionList);
      selectedMultiValueParamSelection = selectedMultiValueParamSelectionList.get(0);
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

   //
   //   private void createChart(List<ParamInfo> values, ValueInfo mainValue) {
   //      try {
   //         double minValue = Double.MAX_VALUE;
   //         double maxValue = Double.MIN_VALUE;
   //         XYDataList series = new XYDataList();
   //         series.setLabel(mainValue.getMetricName());
   //         for (ParamInfo pinfo : values) {
   //            Double paramValue = Double.valueOf(pinfo.getParamValue());
   //            if (paramValue != null) {
   //               XYDataPoint dp = new XYDataPoint(paramValue, pinfo.getValue());
   //               series.addDataPoint(dp);
   //               if (paramValue > maxValue) {
   //                  maxValue = paramValue;
   //               }
   //               if (paramValue < minValue) {
   //                  minValue = paramValue;
   //               }
   //            }
   //         }
   //         chartData.addDataList(series);
   //         double range = maxValue - minValue;
   //         chart.setYaxisMaxValue(maxValue + 0.1d * range);
   //         double yaxisMinValue = minValue - 0.1d * range;
   //         if (minValue >= 0d && yaxisMinValue < 0) {
   //            yaxisMinValue = 0d; // don't get below zero if min value isn't negative
   //         }
   //         chart.setYaxisMinValue(yaxisMinValue);
   //      } catch (Exception e) {
   //         log.error("Error while creating chart", e);
   //         chart = null;
   //         chartData = null;
   //      }
   //   }

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

}