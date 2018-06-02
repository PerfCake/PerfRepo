package org.perfrepo.dto.exception;

/**
 * This unchecked exception is thrown if service adapter could not perform action for any reason.
 * A fatal error occurred, for example NPE, lost database connection, constraint violation  ...
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class AdapterException extends RuntimeException {

    public AdapterException() {

    }

    /**
     * Constructs a {@link AdapterException} with the specified detail message.
     *
     * @param message Detailed message.
     */
    public AdapterException(String message) {
        super(message);
    }


}
