package org.jboss.qa.perfrepo.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.richfaces.component.SortOrder;

/**
 * 
 * Base class for controllers.
 * 
 * @author Michal Linhard (mlinhard@redhat.com)
 * 
 */
public class ControllerBase implements Serializable {

   private static final long serialVersionUID = -1616863465068425778L;
   
   private Map<String, SortOrder> sortsOrders = new HashMap<String, SortOrder>();
   
   
   private static final String SORT_PROPERTY_PARAMETER = "sortProperty";
   
   private boolean multipleSorting = false;
   
   private List<String> sortPriorities = new ArrayList<String>();

   public Map<String, String> getRequestParams() {
      return FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
   }

   public String getRequestParam(String name) {
      return getRequestParams().get(name);
   }

   public String getRequestParam(String name, String _default) {
      String ret = getRequestParam(name);
      if (ret == null) {
         return _default;
      } else {
         return ret;
      }
   }
   
   public Map<String, SortOrder> getSortsOrders() {
      return sortsOrders;
   }

   public void setSortsOrders(Map<String, SortOrder> sortsOrders) {
      this.sortsOrders = sortsOrders;
   }

   public boolean isMultipleSorting() {
      return multipleSorting;
   }

   public void setMultipleSorting(boolean multipleSorting) {
      this.multipleSorting = multipleSorting;
   }

   public List<String> getSortPriorities() {
      return sortPriorities;
   }

   public void setSortPriorities(List<String> sortPriorities) {
      this.sortPriorities = sortPriorities;
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
}
