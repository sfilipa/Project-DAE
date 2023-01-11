package ipleiria.dae.project.ws;

import ipleiria.dae.project.dtos.*;
import ipleiria.dae.project.dtos.create.ClientCreateDTO;
import ipleiria.dae.project.dtos.create.UpdatePasswordDTO;
import ipleiria.dae.project.ejbs.ClientBean;
import ipleiria.dae.project.ejbs.EmailBean;
import ipleiria.dae.project.ejbs.RepairerBean;
import ipleiria.dae.project.entities.Client;
import ipleiria.dae.project.exceptions.APIBadResponseException;
import ipleiria.dae.project.exceptions.MyEntityExistsException;
import ipleiria.dae.project.exceptions.MyEntityNotFoundException;
import ipleiria.dae.project.exceptions.NifNotValidException;
import ipleiria.dae.project.exceptions.NotAuthorizedException;
import ipleiria.dae.project.security.Authenticated;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.List;

@Path("/clients")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
//@RolesAllowed({"Teacher", "Administrator"})
public class ClientService {
    @EJB
    private ClientBean clientBean;

    @EJB
    private EmailBean emailBean;

    @EJB
    private RepairerBean repairerBean;

    @Context
    private SecurityContext securityContext;

    @GET
    @Authenticated
    @RolesAllowed({"Administrator"})
    @Path("/")
    public List<ClientDTO> getAllClients() {
        return ClientDTO.from(clientBean.getAllClients());
    }

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
    public Response create(ClientCreateDTO clientDTO) {
        Client client = clientBean.create(
                clientDTO.getUsername(),
                clientDTO.getPassword(),
                clientDTO.getName(),
                clientDTO.getEmail(),
                clientDTO.getAddress(),
                clientDTO.getPhoneNumber(),
                clientDTO.getNif_nipc()
        );

        if (client == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.status(Response.Status.CREATED)
                .entity(ClientDTO.from(client))
                .build();
    }

    @PUT
   /* @Authenticated*/
   // @RolesAllowed({"Client"})
    @Path("/{username}")
    public Response updateClient(@PathParam("username") String username, ClientDTO clientDTO) {
        Client client = clientBean.update(
                username,
                clientDTO.getName(),
                clientDTO.getEmail(),
                clientDTO.getAddress(),
                clientDTO.getPhoneNumber(),
                clientDTO.getNif_nipc()
        );

        if (client == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.status(Response.Status.OK)
                .entity(ClientDTO.from(client))
                .build();
    }

    @PATCH
    @Authenticated
    @RolesAllowed({"Client"})
    @Path("/{username}/password")
    public Response updatePassword(@PathParam("username") String username, UpdatePasswordDTO clientDTO) {
        if(!securityContext.getUserPrincipal().getName().equals(username)) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        clientBean.updatePassword(username, clientDTO.getPassword(), clientDTO.getOldPassword());

        return Response.status(Response.Status.OK)
                .entity("Password updated")
                .build();
    }

    @DELETE
    @Authenticated
    @RolesAllowed({"Client", "Administrator"})
    @Path("/{username}")
    public Response delete(@PathParam("username") String username) {
        clientBean.delete(username);

        return Response.noContent().build();
    }

    @GET
    @Authenticated
    @RolesAllowed({"Client"})
    @Path("{username}/occurrences")
    public Response getClientOccurrences(@PathParam("username") String username){
        return Response.ok(OccurrenceDTO.from(clientBean.clientOccurrences(username))).build();
    }

    // GET of insurances
    @GET
    @Authenticated
    @RolesAllowed({"Client"})
    @Path("{username}/insurances")
    public Response getClientInsurances(@PathParam("username") String username) {
        return Response.ok(InsuranceDTO.from(clientBean.insurances(username))).build();
    }

    @POST
    @Authenticated
    @Path("/{username}/email/send")
    public Response sendEmail(@PathParam("username") String username, EmailDTO email) {
        Client client = clientBean.find(username);

        if(client == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        emailBean.send(client.getEmail(), email.getSubject(), email.getMessage());
        return Response.noContent().build();
    }

    @PATCH
    @Authenticated
    @RolesAllowed({"Client"})
    @Path("/{clientUsername}/occurrences/{occurrence_code}/{repairerUsername}/assign")
    public Response assignOccurrence(@PathParam("clientUsername") String clientUsername, @PathParam("repairerUsername") String repairerUsername, @PathParam("occurrence_code") long occurrence_code) throws MyEntityNotFoundException, ipleiria.dae.project.exceptions.NotAuthorizedException {
        try {
            if(!securityContext.getUserPrincipal().getName().equals(clientUsername)) {
                return Response.status(Response.Status.FORBIDDEN).build();
            }

            clientBean.verifyOccurrenceBelongsToClient(clientUsername, occurrence_code);

            repairerBean.assignOccurrence(repairerUsername, occurrence_code);
            return Response.ok().build();
        } catch (MyEntityNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .build();
        } catch (ipleiria.dae.project.exceptions.NotAuthorizedException e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(e.getMessage())
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @PATCH
    @Authenticated
    @RolesAllowed({"Client"})
    @Path("/{clientUsername}/occurrences/{occurrence_code}/{repairerUsername}/unassign")
    public Response unassignOccurrence(@Context HttpServletRequest request, @PathParam("clientUsername") String clientUsername, @PathParam("repairerUsername") String repairerUsername, @PathParam("occurrence_code") long occurrence_code)
            throws MyEntityNotFoundException, NotAuthorizedException {
        try {
            if(!securityContext.getUserPrincipal().getName().equals(clientUsername)) {
                return Response.status(Response.Status.FORBIDDEN).build();
            }

            clientBean.verifyOccurrenceBelongsToClient(clientUsername, occurrence_code);

            repairerBean.unassignOccurrence(repairerUsername, occurrence_code);
            return Response.ok().build();
        } catch (MyEntityNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .build();
        } catch (NotAuthorizedException e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(e.getMessage())
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

}
