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

import org.jboss.qa.perfrepo.dao.ValueDAO;
import org.jboss.qa.perfrepo.model.Value;

@Path("/value")
@RequestScoped
public class ValueREST {

	@Inject
	private ValueDAO dao;

	@GET()
	@Produces(MediaType.TEXT_XML)
	@Path("/getAll")
	public List<Value> findAll() {
		return dao.findAll();
	}

	@GET()
	@Produces(MediaType.TEXT_XML)
	@Path("/get/{valueId}")
	public Value findById(@PathParam("valueId") Long valueId) {
		return dao.get(valueId);
	}
	
	@POST()
   @Path("/create")
   @Consumes(MediaType.TEXT_XML)
   public Long create(Value value) {
      dao.create(value);
      return value.getId();
   }
	
//	@GET
//@Produces("text/xml")
//@Path("/getByMetric/{metricId}")
//public List<Value> findByMetric(@PathParam("metricId") Integer metricId) {
//return dao.findByMetric(metricId);
//}
//@GET
//@Produces("text/xml")
//@Path("/getByTestExecution/{testExecutionId}")
//public List<Value> findByTestExecution(@PathParam("testExecutionId") Integer testExecutionId) {
//return dao.findByTestExecution(testExecutionId);
//}

}