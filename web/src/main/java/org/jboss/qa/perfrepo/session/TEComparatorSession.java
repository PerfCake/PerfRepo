package org.jboss.qa.perfrepo.session;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.qa.perfrepo.service.TestService;

@Named(value="teComparatorSession")
@SessionScoped
public class TEComparatorSession implements Serializable {

   private static final long serialVersionUID = 1L;

   @Inject
   private TestService testExecutionService;

   private Set<Long> testExecutions = new HashSet<Long>();
   
   public void add(Long te) {
     testExecutions.add(te);
   }   
   
   public void remove(Long id) {
      testExecutions.remove(id);
   }
   
   public Collection<Long> getTestExecutions() {
      return testExecutions;
   }
   
   public boolean isAnyToCompare()  {
      return testExecutions != null && testExecutions.size() > 0;
   }
   
}