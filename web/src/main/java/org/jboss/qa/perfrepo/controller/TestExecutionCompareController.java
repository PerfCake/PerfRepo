package org.jboss.qa.perfrepo.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.qa.perfrepo.model.TestExecution;
import org.jboss.qa.perfrepo.service.TestExecutionService;
import org.jboss.qa.perfrepo.session.TEComparatorSession;
import org.richfaces.component.SortOrder;

/**
 * 
 * @author pdrozd
 * 
 */
@Named
@RequestScoped
public class TestExecutionCompareController implements Serializable {

   private static final long serialVersionUID = 1L;

   @Inject
   private TestExecutionService testExecutionService;
   
   @Inject
   private TEComparatorSession teComparator;   

   private Map<String, SortOrder> sortsOrders;
   private List<String> sortPriorities;

   private boolean multipleSorting = false;

   private static final String SORT_PROPERTY_PARAMETER = "sortProperty";

  @PostConstruct
   public void init() {
      sortsOrders = new HashMap<String, SortOrder>();
      sortPriorities = new ArrayList<String>();    
   }

   public void sort() {
      String property = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(SORT_PROPERTY_PARAMETER);
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
      if (teComparator.getTestExecutions() != null && teComparator.getTestExecutions().size() > 0) {
         return testExecutionService.getTestExecutions(teComparator.getTestExecutions());
      }
      return new ArrayList<TestExecution>();
      
   }

   public List<String> getSortPriorities() {
      return sortPriorities;
   }

   public Map<String, SortOrder> getSortsOrders() {
      return sortsOrders;
   }

}