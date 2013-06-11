package org.jboss.qa.perfrepo.rest;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

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

   /**
    * @param testExecution
    * @return Response with id of newly created test execution or error message.
    */
   @POST()
   @Path("/create")
   @Consumes(MediaType.TEXT_XML)
   @Logged
   public Response create(TestExecution testExecution) throws Exception {
      return Response.ok(testService.storeTestExecution(testExecution).getId()).build();
   }

   /**
    * Add test execution attachment.
    * 
    * @param testExecutionId
    * @param mimeType
    * @param fileName
    * @param requestBody
    * @return Id of newly created attachment
    */
   @POST()
   @Path("/{testExecutionId}/addAttachment")
   @Logged
   public Response addAttachment(@PathParam("testExecutionId") Long testExecutionId, @HeaderParam("Content-type") String mimeType,
         @HeaderParam("filename") String fileName, byte[] requestBody) throws Exception {
      TestExecutionAttachment attachment = new TestExecutionAttachment();
      attachment.setFilename(fileName);
      attachment.setMimetype(mimeType);
      attachment.setContent(requestBody);
      TestExecution testExec = new TestExecution();
      testExec.setId(testExecutionId);
      attachment.setTestExecution(testExec);
      return Response.status(200).entity(testService.addAttachment(attachment)).build();
   }

   /**
    * 
    * Fetch test execution attachment.
    * 
    * @param attachmentId
    * @return the attachment
    */
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