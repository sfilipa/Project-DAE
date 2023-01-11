package ipleiria.dae.project.exceptions.mappers;

import ipleiria.dae.project.exceptions.ArgumentNotValidException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.logging.Logger;

@Provider
public class ArgumentNotValidExceptionMapper implements ExceptionMapper<ArgumentNotValidException> {
    private static final Logger logger =
            Logger.getLogger(ArgumentNotValidException.class.getCanonicalName());

    @Override
    public Response toResponse(ArgumentNotValidException e) {
        String errorMsg = e.getMessage();
        logger.warning("ERROR: " + errorMsg);
        return Response.status(Response.Status.NOT_ACCEPTABLE)
                .entity(errorMsg)
                .build();
    }
}