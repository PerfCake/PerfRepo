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

import org.jboss.qa.perfrepo.dao.TestExecutionDAO;
import org.jboss.qa.perfrepo.model.TestExecution;
import org.jboss.qa.perfrepo.service.TestExecutionService;

@Path("/testExecution")
@RequestScoped
public class TestExecutionREST {

   @Inject
   private TestExecutionDAO dao;
   
   @Inject
   private TestExecutionService testExecutionService;

   @GET()
   @Produces(MediaType.TEXT_XML)
   @Path("/getAll")
   public List<TestExecution> findAll() {
      return dao.findAll();
   }

   @GET()
   @Produces(MediaType.TEXT_XML)
   @Path("/get/{testExecutionId}")
   public TestExecution findById(@PathParam("testExecutionId") Long testExecutionId) {
      return dao.getFullTestExecution(testExecutionId);
   }

//   @POST()
//   @Path("/create")
//   @Consumes(MediaType.TEXT_XML)
//   public Long create(TestExecution testExecution) {
//      dao.create(testExecution);
//      return testExecution.getId();
//   }
   
   @POST()
   @Path("/createTestExecution")
   @Consumes(MediaType.TEXT_XML)
   public Long createFull(TestExecution testExecution) {
      TestExecution stored = testExecutionService.storeTestExecution(testExecution);
      return stored.getId();
   }   

   @GET
   @Produces("text/xml")
   @Path("/getByTest/{testId}")
   public List<TestExecution> findByTest(@PathParam("testId") Long testId) {
      return dao.findByTest(testId);
   }

}