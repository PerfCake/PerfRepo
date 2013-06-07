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

import org.jboss.qa.perfrepo.model.Test;
import org.jboss.qa.perfrepo.service.TestService;

@Path("/test")
@RequestScoped
public class TestREST {

	@Inject
   private TestService testService; 

	@GET()
	@Produces(MediaType.TEXT_XML)
	@Path("/getAll")
	public List<Test> findAll() {
		return testService.getAllTests();
	}

	@GET()
	@Produces(MediaType.TEXT_XML)
	@Path("/get/{testId}")
	public Test findById(@PathParam("testId") Long testId) {
		return testService.getTest(testId);
	}
	
	@POST()
   @Path("/createTest")
   @Consumes(MediaType.TEXT_XML)
   public Test createTest(Test test) {
	   Test stored = testService.storeTest(test);
      return stored;
   }
	
}