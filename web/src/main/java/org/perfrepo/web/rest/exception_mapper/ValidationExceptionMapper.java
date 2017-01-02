package org.perfrepo.web.rest.exception_mapper;

import org.apache.http.HttpStatus;
import org.perfrepo.dto.util.validation.FieldError;
import org.perfrepo.web.adapter.exceptions.ValidationException;


import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.List;

/**
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ValidationException> {

    @Override
    public Response toResponse(ValidationException exception) {
        List<FieldError> fieldErrors = null;
        if (exception.getValidationErrors() != null) {
            fieldErrors = exception.getValidationErrors().getFieldErrors();
        }
        ValidationExceptionResponse responseEntity =
                new ValidationExceptionResponse(exception.getMessage(), fieldErrors);

        return Response.status(HttpStatus.SC_UNPROCESSABLE_ENTITY).entity(responseEntity).build();
    }

    private class ValidationExceptionResponse {

        private String message;

        private List<FieldError> fieldErrors;

        public ValidationExceptionResponse(String message, List<FieldError> fieldErrors) {
            this.message = message;
            this.fieldErrors = fieldErrors;
        }

        public String getMessage() {
            return message;
        }

        public List<FieldError> getFieldErrors() {
            return fieldErrors;
        }
    }
}