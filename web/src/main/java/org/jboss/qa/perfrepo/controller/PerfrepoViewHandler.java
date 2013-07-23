package org.jboss.qa.perfrepo.controller;

import javax.faces.application.ViewHandler;
import javax.faces.application.ViewHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

/**
 * {@link ViewHandler} that makes forms to post to original url.
 * 
 * @author Michal Linhard (mlinhard@redhat.com)
 * 
 */
public class PerfrepoViewHandler extends ViewHandlerWrapper {

   private ViewHandler wrapped;

   public PerfrepoViewHandler(ViewHandler wrapped) {
      this.wrapped = wrapped;
   }

   /**
    * We always post back to the original Request URL, not the viewID since we sometimes encode
    * state in the Request URL such as object id, page number, etc.
    */
   @Override
   public String getActionURL(FacesContext faces, String viewID) {
      HttpServletRequest request = (HttpServletRequest) faces.getExternalContext().getRequest();
      String formActionURL = (String) request.getAttribute("formActionURL");
      if (formActionURL == null) {
         return super.getActionURL(faces, viewID);
      } else {
         return request.getContextPath() + formActionURL;
      }
   }

   @Override
   public ViewHandler getWrapped() {
      return wrapped;
   }
}
