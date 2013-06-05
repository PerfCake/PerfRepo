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

import org.jboss.qa.perfrepo.dao.TestExecutionTagDAO;
import org.jboss.qa.perfrepo.model.TestExecutionTag;

@Path("/testExecutionTag")
@RequestScoped
public class TestExecutionTagREST {

	@Inject
	private TestExecutionTagDAO dao;

	@GET()
	@Produces(MediaType.TEXT_XML)
	@Path("/getAll")
	public List<TestExecutionTag> findAll() {
		return dao.findAll();
	}

	@GET()
	@Produces(MediaType.TEXT_XML)
	@Path("/get/{testExecutionTagId}")
	public TestExecutionTag findById(@PathParam("testExecutionTagId") Long testExecutionTagId) {
		return dao.get(testExecutionTagId);
	}
	
	@POST()
   @Path("/create")
   @Consumes(MediaType.TEXT_XML)
   public Long create(TestExecutionTag testExecutionTag) {
      dao.create(testExecutionTag);
      return testExecutionTag.getId();
   }
	
//	@GET
//@Produces("text/xml")
//@Path("/getByTag/{tagId}")
//public List<TestExecutionTag> findByTag(@PathParam("tagId") Integer tagId) {
//return dao.findByTag(tagId);
//}
//@GET
//@Produces("text/xml")
//@Path("/getByTestExecution/{testExecutionId}")
//public List<TestExecutionTag> findByTestExecution(@PathParam("testExecutionId") Integer testExecutionId) {
//return dao.findByTestExecution(testExecutionId);
//}

}