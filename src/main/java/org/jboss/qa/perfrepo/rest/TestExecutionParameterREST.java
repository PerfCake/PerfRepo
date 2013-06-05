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

import org.jboss.qa.perfrepo.dao.TestExecutionParameterDAO;
import org.jboss.qa.perfrepo.model.TestExecutionParameter;

@Path("/testExecutionParameter")
@RequestScoped
public class TestExecutionParameterREST {

	@Inject
	private TestExecutionParameterDAO dao;

	@GET()
	@Produces(MediaType.TEXT_XML)
	@Path("/getAll")
	public List<TestExecutionParameter> findAll() {
		return dao.findAll();
	}

	@GET()
	@Produces(MediaType.TEXT_XML)
	@Path("/get/{testExecutionParameterId}")
	public TestExecutionParameter findById(@PathParam("testExecutionParameterId") Long testExecutionParameterId) {
		return dao.get(testExecutionParameterId);
	}
	
	@POST()
   @Path("/create")
   @Consumes(MediaType.TEXT_XML)
   public Long create(TestExecutionParameter testExecutionParameter) {
      dao.create(testExecutionParameter);
      return testExecutionParameter.getId();
   }
	
//	@GET
//@Produces("text/xml")
//@Path("/getByTestExecution/{testExecutionId}")
//public List<TestExecutionParameter> findByTestExecution(@PathParam("testExecutionId") Integer testExecutionId) {
//return dao.findByTestExecution(testExecutionId);
//}

}