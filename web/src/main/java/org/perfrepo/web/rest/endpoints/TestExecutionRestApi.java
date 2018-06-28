package org.perfrepo.web.rest.endpoints;

import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.perfrepo.dto.test_execution.AttachmentDto;
import org.perfrepo.dto.test_execution.ParameterDto;
import org.perfrepo.dto.test_execution.TestExecutionDto;
import org.perfrepo.dto.test_execution.TestExecutionSearchCriteria;
import org.perfrepo.dto.test_execution.ValuesGroupDto;
import org.perfrepo.dto.test_execution.mass_operation.ParameterMassOperationDto;
import org.perfrepo.dto.test_execution.mass_operation.TagMassOperationDto;
import org.perfrepo.dto.util.SearchResult;
import org.perfrepo.web.adapter.TestExecutionAdapter;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Web service endpoint for test executions.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
@Path("/test-executions")
@RequestScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TestExecutionRestApi {

   @Inject
   private TestExecutionAdapter testExecutionAdapter;

   @GET
   @Path("/{id}")
   public Response get(@PathParam("id") Long testId) {
      TestExecutionDto testExecution = testExecutionAdapter.getTestExecution(testId);

      return Response.ok(testExecution).build();
   }

   @POST
   @Path("/search")
   public Response search(TestExecutionSearchCriteria searchParams) {
      SearchResult<TestExecutionDto> result = testExecutionAdapter.searchTestExecutions(searchParams);

      return Response
              .status(Response.Status.OK)
              .header("X-Pagination-Total-Count", result.getTotalCount())
              .header("X-Pagination-Current-Page", result.getCurrentPage())
              .header("X-Pagination-Page-Count", result.getPageCount())
              .header("X-Pagination-Per-Page", result.getPerPage())
              .entity(result.getData()).build();
   }

   @GET
   @Path("/search-criteria")
   public Response getSearchCriteria() {
       TestExecutionSearchCriteria searchParams = testExecutionAdapter.getSearchCriteria();

       return Response.ok(searchParams).build();
   }

   @POST
   public Response create(TestExecutionDto testExecutionDto) {
      TestExecutionDto createdTestExecution = testExecutionAdapter.createTestExecution(testExecutionDto);
      URI uri = URI.create("/test-executions/" + createdTestExecution.getId());

      return Response.created(uri).build();
   }

   @PUT
   public Response update(TestExecutionDto testExecutionDto) {
      testExecutionAdapter.updateTestExecution(testExecutionDto);

      return Response.noContent().build();
   }

   @DELETE
   @Path("/{id}")
   public Response delete(@PathParam("id") Long testExecutionId) {
      testExecutionAdapter.removeTestExecution(testExecutionId);

      return Response.noContent().build();
   }

   @GET
   public Response getAll() {
      List<TestExecutionDto> allTestExecutions = testExecutionAdapter.getAllTestExecutions();

      return Response.ok().entity(allTestExecutions).build();
   }

   /**
    * Set test execution parameters. The old parameters are removed.
    * @param testExecutionId The test execution identifier.
    * @param testExecutionParameters Execution parameters.
    * @return No content if the request is successful.
    */
   @PUT
   @Path("/{id}/parameters")
   public Response setParameters(@PathParam("id") Long testExecutionId, Set<ParameterDto> testExecutionParameters) {
       testExecutionAdapter.setTestExecutionParameters(testExecutionId, testExecutionParameters);

       return Response.noContent().build();
   }
   /**
    * Add measured values to the test execution. The old values of the metric are preserved.
    *
    * @param testExecutionId The test execution identifier.
    * @param valuesGroup The sets of new execution measured values.
    * @return No content if the request is successful.
    */
   @POST
   @Path("/{id}/values")
   public Response addExecutionValues(@PathParam("id") Long testExecutionId, ValuesGroupDto valuesGroup) {
      testExecutionAdapter.addExecutionValues(testExecutionId, valuesGroup);

      return Response.noContent().build();
   }

   /**
    * Set measured values to the test execution. The old values of the metric are removed.
    *
    * @param testExecutionId The test execution identifier.
    * @param valuesGroup The sets of new execution measured values.
    * @return No content if the request is successful.
    */
   @PUT
   @Path("/{id}/values")
   public Response setExecutionValues(@PathParam("id") Long testExecutionId, ValuesGroupDto valuesGroup) {
      testExecutionAdapter.setExecutionValues(testExecutionId, valuesGroup);

      return Response.noContent().build();
   }

    /**
     * File attachment download.
     * @param attachmentId Attachment ID.
     * @param hash Access hash token.
     * @return File response.
     */
   @GET
   @Path("/attachments/download/{attachmentId}/{hash}")
   @Produces(MediaType.APPLICATION_OCTET_STREAM)
   public Response downloadAttachment(@PathParam("attachmentId") Long attachmentId, @PathParam("hash") String hash) {

       AttachmentDto attachment = testExecutionAdapter.getTestExecutionAttachment(attachmentId, hash);

       return Response
               .ok(attachment.getContent())
               .header("Content-Disposition", "attachment; filename=" + attachment.getFilename())
               .build();
   }

    /**
     * TODO this method will be changed
     */
    @POST
    @Path("/attachments/upload")
    @Consumes("*/*")
    public Response uploadAttachment(MultipartFormDataInput formDataInput) {
        Map<String, List<InputPart>> formDataParts = formDataInput.getFormDataMap();
        List<InputPart> inputParts = formDataParts.get("file");

        InputPart inputPart = inputParts.get(0);

        try {
            MultivaluedMap<String, String> headers = inputPart.getHeaders();
            InputStream inputStream = inputPart.getBody(InputStream.class, null);

            byte [] bytes = IOUtils.toByteArray(inputStream);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return Response.status(200)
                .entity("uploadFile is called, Uploaded file name : ").build();
    }

    @POST
    @Path("/mass-operation/tags-addition")
    public Response addTagsMassOperation(TagMassOperationDto massOperation) {
        testExecutionAdapter.addTags(massOperation);

        return Response.noContent().build();
    }

    @POST
    @Path("/mass-operation/tags-removal")
    public Response removeTagsMassOperation(TagMassOperationDto massOperation) {
        testExecutionAdapter.removeTags(massOperation);

        return Response.noContent().build();
    }

    @POST
    @Path("/mass-operation/parameter-addition")
    public Response addParameterMassOperation(ParameterMassOperationDto massOperation) {
        testExecutionAdapter.addParameter(massOperation);

        return Response.noContent().build();
    }

    @POST
    @Path("/mass-operation/parameter-removal")
    public Response removeParameterMassOperation(ParameterMassOperationDto massOperation) {
        testExecutionAdapter.removeParameter(massOperation);

        return Response.noContent().build();
    }

    @POST
    @Path("/mass-operation/test-execution-removal")
    public Response deleteTestExecutionsMassOperation(Set<Long> testExecutionIds) {
        testExecutionAdapter.removeTestExecutions(testExecutionIds);

        return Response.noContent().build();
    }
}