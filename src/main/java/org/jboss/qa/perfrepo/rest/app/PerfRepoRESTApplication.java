package org.jboss.qa.perfrepo.rest.app;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.jboss.qa.perfrepo.rest.*;

@ApplicationPath("/rest")
public class PerfRepoRESTApplication extends Application {

   private Set<Class<?>> classes = new HashSet<Class<?>>();

   public PerfRepoRESTApplication() {
      classes.add(TestExecutionTagREST.class);
      classes.add(TestMetricREST.class);
      classes.add(TagREST.class);
      classes.add(ValueREST.class);
      classes.add(ValueParameterREST.class);
      classes.add(MetricREST.class);
      classes.add(TestExecutionREST.class);
      classes.add(TestREST.class);
      classes.add(TestExecutionParameterREST.class);

   }

   @Override
   public Set<Class<?>> getClasses() {
      return classes;
   }

}