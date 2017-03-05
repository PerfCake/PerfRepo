package org.perfrepo.web.rest.exception_mapper;

import org.perfrepo.web.adapter.exceptions.NotFoundException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Maps {@link NotFoundException} exception to HTTP response. Sets 404 status code.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
@Provider
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {

    @Override
    public Response toResponse(NotFoundException exception) {
        return Response.status(Response.Status.NOT_FOUND).entity(exception.getMessage()).build();
    }
}