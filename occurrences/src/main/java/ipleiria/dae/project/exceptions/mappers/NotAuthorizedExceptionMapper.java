package ipleiria.dae.project.exceptions.mappers;

import ipleiria.dae.project.exceptions.MyIllegalArgumentException;
import ipleiria.dae.project.exceptions.NotAuthorizedException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import java.util.logging.Logger;

public class NotAuthorizedExceptionMapper implements ExceptionMapper<NotAuthorizedException>  {
    private static final Logger logger =
            Logger.getLogger(MyIllegalArgumentException.class.getCanonicalName());

    @Override
    public Response toResponse(NotAuthorizedException e) {
        String errorMsg = e.getMessage();
        logger.warning("ERROR: " + errorMsg);
        return Response.status(Response.Status.UNAUTHORIZED)
                .entity(errorMsg)
                .build();
    }
}
