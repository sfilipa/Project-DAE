package ipleiria.dae.project.exceptions.mappers;

import ipleiria.dae.project.exceptions.EntityManagerPersistDBException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.logging.Logger;

@Provider
public class EntityManagerPersistDBMapper implements ExceptionMapper<EntityManagerPersistDBException> {
    private static final Logger logger =
            Logger.getLogger(EntityManagerPersistDBException.class.getCanonicalName());

    @Override
    public Response toResponse(EntityManagerPersistDBException e) {
        String errorMsg = e.getMessage();
        logger.warning("ERROR: " + errorMsg);
        return Response.status(Response.Status.CONFLICT)
                .entity(errorMsg)
                .build();
    }
}
