package org.jboss.qa.perfrepo.rest;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.qa.perfrepo.model.Metric;
import org.jboss.qa.perfrepo.model.Test;
import org.jboss.qa.perfrepo.service.TestService;

/**
 * REST interface for Test objects.
 * 
 * @author Pavel Drozd (pdrozd@redhat.com)
 * @author Michal Linhard (mlinhard@redhat.com)
 */
@Path("/test")
@RequestScoped
public class TestREST {

   @Inject
   private TestService testService;

   @GET
   @Produces(MediaType.TEXT_XML)
   @Path("/{testId}")
   @Logged
   public Response get(@PathParam("testId") Long testId) {
      return Response.ok(testService.getTest(testId)).build();
   }

   @GET
   @Produces(MediaType.TEXT_XML)
   @Path("/all")
   @Logged
   public Response all() {
      return Response.ok(testService.findAllTests()).build();
   }

   @POST
   @Path("/create")
   @Consumes(MediaType.TEXT_XML)
   @Logged
   public Response create(Test test) {
      return Response.ok(testService.createTest(test).getId()).build();
   }

   @POST
   @Path("/{testId}/addMetric")
   @Consumes(MediaType.TEXT_XML)
   @Logged
   public Response addMetric(@PathParam("testId") Long testId, Metric metric) {
      Test test = new Test();
      test.setId(testId);
      return Response.ok(testService.addMetric(test, metric).getId()).build();
   }

   @GET
   @Produces("text/xml")
   @Path("/{testId}/executions")
   @Logged
   public Response executions(@PathParam("testId") Long testId) {
      return Response.ok(testService.findExecutionsByTest(testId)).build();
   }

}