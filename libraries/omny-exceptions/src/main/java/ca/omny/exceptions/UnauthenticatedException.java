package ca.omny.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class UnauthenticatedException extends WebApplicationException {

    public UnauthenticatedException() {
        this("Not authenticated");
    }
    
    public UnauthenticatedException(String message) {
        super(Response.status(Response.Status.UNAUTHORIZED)
                .entity(message).type(MediaType.TEXT_PLAIN).build());
    }
}
