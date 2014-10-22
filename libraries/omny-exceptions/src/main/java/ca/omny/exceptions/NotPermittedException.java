package ca.omny.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class NotPermittedException extends WebApplicationException {

    public NotPermittedException() {
        this("not allowed");
    }

    public NotPermittedException(String message) {
        super(Response.status(Response.Status.FORBIDDEN)
                .entity(message).type(MediaType.TEXT_PLAIN).build());
    }
}
