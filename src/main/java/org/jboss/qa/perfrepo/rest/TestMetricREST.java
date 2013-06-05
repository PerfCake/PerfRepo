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

import org.jboss.qa.perfrepo.dao.TestMetricDAO;
import org.jboss.qa.perfrepo.model.TestMetric;

@Path("/testMetric")
@RequestScoped
public class TestMetricREST {

	@Inject
	private TestMetricDAO dao;

	@GET()
	@Produces(MediaType.TEXT_XML)
	@Path("/getAll")
	public List<TestMetric> findAll() {
		return dao.findAll();
	}

	@GET()
	@Produces(MediaType.TEXT_XML)
	@Path("/get/{testMetricId}")
	public TestMetric findById(@PathParam("testMetricId") Long testMetricId) {
		return dao.get(testMetricId);
	}
	
	@POST()
   @Path("/create")
   @Consumes(MediaType.TEXT_XML)
   public Long create(TestMetric testMetric) {
      dao.create(testMetric);
      return testMetric.getId();
   }
	
//	@GET
//@Produces("text/xml")
//@Path("/getByMetric/{metricId}")
//public List<TestMetric> findByMetric(@PathParam("metricId") Integer metricId) {
//return dao.findByMetric(metricId);
//}
//@GET
//@Produces("text/xml")
//@Path("/getByTest/{testId}")
//public List<TestMetric> findByTest(@PathParam("testId") Integer testId) {
//return dao.findByTest(testId);
//}

}