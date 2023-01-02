package ipleiria.dae.project.ws;

import ipleiria.dae.project.dtos.ClientDTO;
import ipleiria.dae.project.dtos.OccurrenceDTO;
import ipleiria.dae.project.ejbs.ClientBean;
import ipleiria.dae.project.entities.Client;
import ipleiria.dae.project.entities.Occurrence;
import ipleiria.dae.project.exceptions.MyEntityExistsException;
import ipleiria.dae.project.exceptions.MyEntityNotFoundException;
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

    @POST
    @Path("/")
    public Response create(Client clientDTO) throws MyEntityExistsException {
        Client client = null;
        client = clientBean.create(
                clientDTO.getUsername(),
                clientDTO.getPassword(),
                clientDTO.getName(),
                clientDTO.getEmail(),
                clientDTO.getAddress(),
                clientDTO.getPhoneNumber()
        );

        if (client == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.status(Response.Status.CREATED)
                .entity(ClientDTO.from(client))
                .build();
    }

    @PUT
   /* @Authenticated
    @RolesAllowed({"Client"})*/
    @Path("/{username}")
    public Response updateClient(@PathParam("username") String username, ClientDTO clientDTO) throws MyEntityNotFoundException {
       /* if(!securityContext.getUserPrincipal().getName().equals(username)) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }*/
        Client client = null;
        client = clientBean.update(
                username,
                clientDTO.getPassword(),
                clientDTO.getName(),
                clientDTO.getEmail(),
                clientDTO.getAddress(),
                clientDTO.getPhoneNumber()
        );


        if (client == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.status(Response.Status.OK)
                .entity(ClientDTO.from(client))
                .build();
    }

    @DELETE
   /* @Authenticated
    @RolesAllowed({"Client"})*/
    @Path("/{username}")
    public Response deleteOccurrence(@PathParam("username") String username) throws MyEntityNotFoundException {
        clientBean.delete(username);

        return Response.noContent().build();
    }

    @GET
    @Authenticated
    @RolesAllowed({"Client"})
    @Path("{username}/occurrences")
    public Response getClientOccurrences(@PathParam("username") String username) {
        return Response.ok(OccurrenceDTO.from(clientBean.clientOccurrences(username))).build();
    }
}