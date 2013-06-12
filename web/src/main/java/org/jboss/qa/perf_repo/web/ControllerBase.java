package org.jboss.qa.perf_repo.web;

import java.io.Serializable;
import java.util.Map;

import javax.faces.context.FacesContext;

/**
 * 
 * Base class for controllers.
 * 
 * @author Michal Linhard (mlinhard@redhat.com)
 * 
 */
public class ControllerBase implements Serializable {

   private static final long serialVersionUID = -1616863465068425778L;

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
}
