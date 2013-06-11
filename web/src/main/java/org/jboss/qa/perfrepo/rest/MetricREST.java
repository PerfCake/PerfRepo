package org.jboss.qa.perfrepo.rest;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.qa.perfrepo.service.TestService;

@Path("/metric")
@RequestScoped
public class MetricREST {

   @Inject
   private TestService testService;

   @GET
   @Produces(MediaType.TEXT_XML)
   @Path("/{metricId}")
   @Logged
   public Response get(@PathParam("metricId") Long metricId) {
      return Response.ok(testService.getMetric(metricId)).build();
   }

   @GET
   @Produces(MediaType.TEXT_XML)
   @Path("/all")
   @Logged
   public Response all() {
      return Response.ok(testService.getAllMetrics()).build();
   }

}