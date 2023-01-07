package ipleiria.dae.project.exceptions.mappers;

import ipleiria.dae.project.exceptions.MyEntityCreationViolationException;
import ipleiria.dae.project.exceptions.MyEntityExistsException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.logging.Logger;

@Provider
public class MyEntityCreationViolationMapper implements ExceptionMapper<MyEntityCreationViolationException> {
    private static final Logger logger =
            Logger.getLogger(MyEntityCreationViolationException.class.getCanonicalName());

    @Override
    public Response toResponse(MyEntityCreationViolationException e) {
        String errorMsg = e.getMessage();
        logger.warning("ERROR: " + errorMsg);
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(errorMsg)
                .build();
    }
}
