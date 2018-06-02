package org.perfrepo.web.rest.exception_mapper;

import org.apache.http.HttpStatus;
import org.perfrepo.dto.util.validation.ValidationErrors;
import org.perfrepo.dto.exception.ValidationException;

import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Maps {@link ValidationException} exception to HTTP response. Sets 422 status code.
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
@Provider
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        ValidationErrors validationErrors = new ValidationErrors();
        ValidationException validationException = new ValidationException(exception.getMessage(), validationErrors);

        return Response.status(HttpStatus.SC_UNPROCESSABLE_ENTITY).entity(validationException).build();
    }
}