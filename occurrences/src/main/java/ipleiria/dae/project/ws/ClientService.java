package ipleiria.dae.project.ws;

import ipleiria.dae.project.dtos.ClientDTO;
import ipleiria.dae.project.dtos.OccurrenceDTO;
import ipleiria.dae.project.ejbs.ClientBean;
import ipleiria.dae.project.security.Authenticated;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@Path("/clients")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@Authenticated
//@RolesAllowed({"Teacher", "Administrator"})
public class ClientService {
    @EJB
    private ClientBean clientBean;

    @Context
    private SecurityContext securityContext;

    @GET
    @Authenticated
    @RolesAllowed({"Client"})
    @Path("{username}")
    public Response get(@PathParam("username") String username) {
        if(!securityContext.getUserPrincipal().getName().equals(username)) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        return Response.ok(ClientDTO.from(clientBean.findOrFail(username))).build();
    }

    @GET
    @Authenticated
    @RolesAllowed({"Client"})
    @Path("{username}/occurrences")
    public Response enrolled(@PathParam("username") String username) {
        return Response.ok(OccurrenceDTO.from(clientBean.clientOccurrences(username))).build();
    }
}
