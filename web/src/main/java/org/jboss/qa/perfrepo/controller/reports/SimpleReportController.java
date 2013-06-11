package org.jboss.qa.perfrepo.controller.reports;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.qa.perfrepo.model.TestExecution;
import org.jboss.qa.perfrepo.model.TestExecutionParameter;
import org.jboss.qa.perfrepo.model.TestExecutionTag;
import org.jboss.qa.perfrepo.model.Value;
import org.jboss.qa.perfrepo.model.ValueParameter;
import org.jboss.qa.perfrepo.service.TestService;
import org.jboss.qa.perfrepo.session.TEComparatorSession;
import org.richfaces.component.SortOrder;

@Named
@RequestScoped
public class SimpleReportController implements Serializable {

   private static final long serialVersionUID = 1L;

   @Inject
   private TestService testExecutionService;

   @Inject
   private TEComparatorSession teComparator; 

   private List<Map<String, Object>> valueRows;
   private List<String> valueColumns;

   private List<TestExecution> testExecutions = null;

   private Set<String> valueParameterNames = null;
   
   
   private List<Map<String, Object>> teParamRows;
   private List<String> teParamColumns;
   
   private Map<String, SortOrder> sortsOrders;
   private List<String> sortPriorities;

   private Map<String, Value> valueParameters = null;
  
   private boolean multipleSorting = false;
   
   private static final String SORT_PROPERTY_PARAMETER = "sortProperty";

   public Map<String, String> getRequestParams() {
      Map<String, String> map = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
      return map;
   }

   public String getRequestParam(String name) {
      return getRequestParams().get(name);
   }

   @PostConstruct
   public void initSecond() {
      sortsOrders = new HashMap<String, SortOrder>();
      sortPriorities = new ArrayList<String>();
      
      testExecutions = testExecutionService.getTestExecutions(teComparator.getTestExecutions());      
      
      valueParameters = new HashMap<String, Value>();
      valueParameterNames = new HashSet<String>();
      valueRows = new ArrayList<Map<String, Object>>();
      valueColumns = new ArrayList<String>();    
      
      teParamColumns = new ArrayList<String>();
      teParamRows = new ArrayList<Map<String, Object>>();
      
      //columns.add("ValueParameters");
      // preprocess
      
      teParamColumns.add("TestExecution");
      for (TestExecution te : testExecutions) {
         Map<String, Object> teParamRow = new HashMap<String, Object>();
         teParamRow.put("TestExecution", te.getName());               
         if (te.getTestExecutionParameters() != null && te.getTestExecutionParameters().size() > 0) {
            for (TestExecutionParameter tep : te.getTestExecutionParameters()) {
               if (!teParamColumns.contains(tep.getName())) {
                  teParamColumns.add(tep.getName());
               }
               teParamRow.put(tep.getName(), tep.getValue());
            }
         }
         if (te.getValues() != null) {
            for (Value value : te.getValues()) {
               StringBuffer valueParams = new StringBuffer();
               if (value.getValueParameters() != null && value.getValueParameters().size() > 0) {
                  for (ValueParameter vp : value.getValueParameters()) {
                     valueParams.append(vp.getName()).append("=").append(vp.getParamValue()).append("\n");
                     if (!valueColumns.contains(vp.getName())) {
                        valueColumns.add(vp.getName());
                     }
                  }
                  valueParams.deleteCharAt(valueParams.length() - 1);
                  valueParameterNames.add(valueParams.toString());
               }
               valueParameters.put(te.getId() + valueParams.toString(), value);
            }
         }
         if (!valueColumns.contains("Metric"))
            valueColumns.add("Metric");
         valueColumns.add(te.getName());   
         teParamRows.add(teParamRow);
      }

      // values:
      for (String vpn : valueParameterNames) {
         Map<String, Object> row = new HashMap<String, Object>();
         for (TestExecution te : testExecutions) {     
            Value value = valueParameters.get(te.getId() + vpn);
            //TODO:parsing??
            String[] params = vpn.split(";");
            for (String param : params) {
               String[] nv = param.split("=");
               if (nv.length == 2) {
                  row.put(nv[0], Double.valueOf(nv[1]));
               }
            }
            if (value != null && value.getMetric() != null) {
               row.put("Metric", value.getMetric().getName());
            }
            row.put(te.getName(), value != null ? value.getResultValue() : null);            
         }
         valueRows.add(row);
      }
      System.out.println();
   }
   
   
   public void sort() {
      String property = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
          .get(SORT_PROPERTY_PARAMETER);
      if (property != null) {
          SortOrder currentPropertySortOrder = sortsOrders.get(property);
          if (multipleSorting) {
              if (!sortPriorities.contains(property)) {
                  sortPriorities.add(property);
              }
          } else {
              sortsOrders.clear();
          }
          if (currentPropertySortOrder == null || currentPropertySortOrder.equals(SortOrder.descending)) {
              sortsOrders.put(property, SortOrder.ascending);
          } else {
              sortsOrders.put(property, SortOrder.descending);
          }
      }
  }
   public List<TestExecution> getTestExecutions() {
      return testExecutions;
   }

   public String getTags(TestExecution te) {
      StringBuilder tag = new StringBuilder();
      for (TestExecutionTag teg : te.getTestExecutionTags()) {
         tag.append(teg.getTag().getName()).append("\n");
      }
      return tag.toString().substring(0, tag.length() - 1);
   }


   public Set<String> getValueParameterNames() {
      return valueParameterNames;
   }

   
   public List<Map<String, Object>> getValueRows() {
      return valueRows;
   }

   public void setValueRows(List<Map<String, Object>> rows) {
      this.valueRows = rows;
   }

   public List<String> getValueColumns() {
      return valueColumns;
   }
   
   public List<String> getSortPriorities() {
      return sortPriorities;
  }

  public Map<String, SortOrder> getSortsOrders() {
      return sortsOrders;
  }

   public void setValueColumns(List<String> columns) {
      this.valueColumns = columns;
   }
   
   public List<Map<String, Object>> getTeParamRows() {
      return teParamRows;
   }

   public void setTeParamRows(List<Map<String, Object>> teParamRows) {
      this.teParamRows = teParamRows;
   }

   public List<String> getTeParamColumns() {
      return teParamColumns;
   }

   public void setTeParamColumns(List<String> teParamColumns) {
      this.teParamColumns = teParamColumns;
   }

   public String getRequestParam(String name, String _default) {
      String ret = getRequestParam(name);
      if (ret == null) {
         return _default;
      } else {
         return ret;
      }
   }
}