package ipleiria.dae.project.exceptions.mappers;

import ipleiria.dae.project.exceptions.DateOutsideRangeException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import java.util.logging.Logger;

public class DateOutsideRangeExceptionMapper implements ExceptionMapper<DateOutsideRangeException> {

    private static final Logger logger =
            Logger.getLogger(DateOutsideRangeException.class.getCanonicalName());

    @Override
    public Response toResponse(DateOutsideRangeException e) {
        String errorMsg = e.getMessage();
        logger.warning("ERROR: " + errorMsg);
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(errorMsg)
                .build();
    }
}
