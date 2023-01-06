package ipleiria.dae.project.ws;

import ipleiria.dae.project.dtos.AdministratorDTO;
import ipleiria.dae.project.ejbs.AdministratorBean;
import ipleiria.dae.project.exceptions.APIBadResponseException;
import ipleiria.dae.project.exceptions.MyEntityNotFoundException;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@Path("/administrators")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public class AdministratorService {

    @EJB
    private AdministratorBean administratorBean;

    @Context
    private SecurityContext securityContext;

    @GET
    @Path("{username}")
    public Response get(@PathParam("username") String username) throws MyEntityNotFoundException, APIBadResponseException {
        return Response.ok(AdministratorDTO.from(administratorBean.findOrFail(username))).build();
    }
}
