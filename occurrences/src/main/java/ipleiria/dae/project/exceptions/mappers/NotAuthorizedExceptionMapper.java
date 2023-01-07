package ipleiria.dae.project.exceptions.mappers;

import ipleiria.dae.project.exceptions.MyIllegalArgumentException;
import ipleiria.dae.project.exceptions.NotAuthorizedException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.logging.Logger;

@Provider
public class NotAuthorizedExceptionMapper implements ExceptionMapper<NotAuthorizedException>  {
    private static final Logger logger =
            Logger.getLogger(NotAuthorizedException.class.getCanonicalName());

    @Override
    public Response toResponse(NotAuthorizedException e) {
        String errorMsg = e.getMessage();
        logger.warning("ERROR: " + errorMsg);
        return Response.status(Response.Status.UNAUTHORIZED)
                .entity(errorMsg)
                .build();
    }
}
