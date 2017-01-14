package org.perfrepo.web.rest.endpoints;

import org.perfrepo.dto.test_execution.TestExecutionDto;
import org.perfrepo.dto.test_execution.TestExecutionSearchCriteria;
import org.perfrepo.dto.util.SearchResult;
import org.perfrepo.web.adapter.TestExecutionAdapter;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

/**
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
}