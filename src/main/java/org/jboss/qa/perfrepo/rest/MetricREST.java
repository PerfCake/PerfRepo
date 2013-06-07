package org.jboss.qa.perfrepo.rest;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.qa.perfrepo.model.Metric;
import org.jboss.qa.perfrepo.service.TestService;

@Path("/metric")
@RequestScoped
public class MetricREST {

	@Inject
	private TestService testService;

	@GET()
	@Produces(MediaType.TEXT_XML)
	@Path("/getAll")
	public List<Metric> findAll() {
		return testService.getAllMetrics();
	}

	@GET()
	@Produces(MediaType.TEXT_XML)
	@Path("/get/{metricId}")
	public Metric findById(@PathParam("metricId") Long metricId) {
		return testService.getMetric(metricId);
	}
	
	@POST()
   @Path("/create")
   @Consumes(MediaType.TEXT_XML)
   public Long create(Metric metric) {
      testService.storeMetric(metric);
      return metric.getId();
   }
	
	
}