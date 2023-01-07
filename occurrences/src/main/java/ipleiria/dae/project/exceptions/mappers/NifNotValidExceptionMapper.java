package ipleiria.dae.project.exceptions.mappers;

import ipleiria.dae.project.exceptions.MyIllegalArgumentException;
import ipleiria.dae.project.exceptions.NifNotValidException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.logging.Logger;

@Provider
public class NifNotValidExceptionMapper implements ExceptionMapper<NifNotValidException> {
    private static final Logger logger =
            Logger.getLogger(NifNotValidException.class.getCanonicalName());

    @Override
    public Response toResponse(NifNotValidException e) {
        String errorMsg = e.getMessage();
        logger.warning("ERROR: " + errorMsg);
        return Response.status(Response.Status.NOT_ACCEPTABLE)
                .entity(errorMsg)
                .build();
    }
}