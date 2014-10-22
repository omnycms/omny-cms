package ca.omny.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Shouldn't actually be considered as an error, but it is easier to cascade
 * this way
 *
 * @author al
 */
public class NotModifiedException extends WebApplicationException {

    public NotModifiedException(String message) {
        super(Response.status(Response.Status.NOT_MODIFIED)
                .entity(message).type(MediaType.TEXT_PLAIN).build());
    }
}
