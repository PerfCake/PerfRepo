package org.jboss.qa.perfrepo.rest;

import java.lang.reflect.Method;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.jboss.qa.perfrepo.model.TestExecution;
import org.jboss.qa.perfrepo.model.TestExecutionAttachment;
import org.jboss.qa.perfrepo.service.TestService;

/**
 * 
 * REST interface for test execution related operations.
 * 
 * @author Pavel Drozd (pdrozd@redhat.com)
 * @author Michal Linhard (mlinhard@redhat.com)
 */
@Path("/testExecution")
@RequestScoped
public class TestExecutionREST {

   private static Method GET_TEST_EXECUTION_METHOD;
   private static Method GET_ATTACHMENT_METHOD;
   static {
      try {
         GET_TEST_EXECUTION_METHOD = TestExecutionREST.class.getMethod("get", Long.class);
         GET_ATTACHMENT_METHOD = TestExecutionREST.class.getMethod("getAttachment", Long.class);
      } catch (Exception e) {
         e.printStackTrace(System.err);
      }
   }

   @Inject
   private TestService testService;

   @GET
   @Produces(MediaType.TEXT_XML)
   @Path("/{testExecutionId}")
   @Logged
   public Response get(@PathParam("testExecutionId") Long testExecutionId) {
      return Response.ok(testService.getTestExecution(testExecutionId)).build();
   }

   @GET
   @Produces(MediaType.TEXT_XML)
   @Path("/all")
   @Logged
   public Response all() {
      return Response.ok(testService.findAllTestExecutions()).build();
   }

   @POST()
   @Path("/create")
   @Consumes(MediaType.TEXT_XML)
   @Logged
   public Response create(TestExecution testExecution, @Context UriInfo uriInfo) throws Exception {
      Long id = testService.createTestExecution(testExecution).getId();
      return Response.created(uriInfo.getBaseUriBuilder().path(TestExecutionREST.class).path(GET_TEST_EXECUTION_METHOD).build(id)).entity(id).build();
   }

   @DELETE
   @Produces(MediaType.TEXT_XML)
   @Path("/{testExecutionId}")
   @Logged
   public Response delete(@PathParam("testExecutionId") Long testExecutionId) throws Exception {
      TestExecution idHolder = new TestExecution();
      idHolder.setId(testExecutionId);
      testService.deleteTestExecution(idHolder);
      return Response.noContent().build();
   }

   @POST()
   @Path("/{testExecutionId}/addAttachment")
   @Logged
   public Response addAttachment(@PathParam("testExecutionId") Long testExecutionId, @HeaderParam("Content-type") String mimeType,
         @HeaderParam("filename") String fileName, byte[] requestBody, @Context UriInfo uriInfo) throws Exception {
      TestExecutionAttachment attachment = new TestExecutionAttachment();
      attachment.setFilename(fileName);
      attachment.setMimetype(mimeType);
      attachment.setContent(requestBody);
      TestExecution testExec = new TestExecution();
      testExec.setId(testExecutionId);
      attachment.setTestExecution(testExec);
      Long id = testService.addAttachment(attachment);
      return Response.created(uriInfo.getBaseUriBuilder().path(TestExecutionREST.class).path(GET_ATTACHMENT_METHOD).build(id)).entity(id).build();
   }

   @GET
   @Path("/attachment/{attachmentId}")
   @Logged
   public Response getAttachment(@PathParam("attachmentId") Long attachmentId) {
      TestExecutionAttachment attachment = testService.getAttachment(attachmentId);
      if (attachment == null) {
         return Response.status(Status.NOT_FOUND).build();
      }
      ResponseBuilder response = Response.ok(attachment.getContent());
      response.header("Content-type", attachment.getMimetype());
      response.header("Content-Disposition", "attachment; filename=" + attachment.getFilename());
      return response.build();
   }
}