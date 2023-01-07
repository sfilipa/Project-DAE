package ipleiria.dae.project.exceptions.mappers;

import ipleiria.dae.project.exceptions.APIBadResponseException;
import ipleiria.dae.project.exceptions.MyIllegalArgumentException;
import org.hibernate.validator.constraints.ParameterScriptAssert;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.logging.Logger;

@Provider
public class APIBadResponseExceptionMapper implements ExceptionMapper<APIBadResponseException> {
    private static final Logger logger =
            Logger.getLogger(APIBadResponseException.class.getCanonicalName());

    @Override
    public Response toResponse(APIBadResponseException e) {
        String errorMsg = e.getMessage();
        logger.warning("ERROR: " + errorMsg);
        return Response.status(Response.Status.NOT_ACCEPTABLE)
                .entity(errorMsg)
                .build();
    }
}
