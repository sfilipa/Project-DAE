package ipleiria.dae.project.ws;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ipleiria.dae.project.dtos.ExpertDTO;
import ipleiria.dae.project.dtos.OccurrenceDTO;
import ipleiria.dae.project.dtos.create.RepairerCreateDTO;
import ipleiria.dae.project.dtos.RepairerDTO;
import ipleiria.dae.project.dtos.create.UpdatePasswordDTO;
import ipleiria.dae.project.ejbs.EmailBean;
import ipleiria.dae.project.ejbs.RepairerBean;
import ipleiria.dae.project.entities.Expert;
import ipleiria.dae.project.entities.Repairer;
import ipleiria.dae.project.exceptions.MyEntityExistsException;
import ipleiria.dae.project.exceptions.MyEntityNotFoundException;
import ipleiria.dae.project.exceptions.NotAuthorizedException;
import ipleiria.dae.project.security.Authenticated;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Path("repairers") // relative url web path for this service
@Produces({MediaType.APPLICATION_JSON}) // injects header “Content-Type: application/json”
@Consumes({MediaType.APPLICATION_JSON})
public class RepairerService {
    @EJB
    private RepairerBean repairerBean;

    @EJB
    private EmailBean emailBean;

    @Context
    private SecurityContext securityContext;

    /**
     * Repairer
     * With Occurrence Started
     */

    @PATCH
    @Authenticated
    @RolesAllowed({"Repairer"})
    @Path("/{username}/occurrences/{occurrence_code}/start")
    public Response startOccurrence(@PathParam("username") String username, @PathParam("occurrence_code") long occurrence_code)
            throws MyEntityNotFoundException {
        try {
            repairerBean.startOccurrence(username, occurrence_code);
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

    @PATCH
    @Authenticated
    @RolesAllowed({"Repairer"})
    @Path("/{username}/occurrences/{occurrence_code}/fail")
    public Response failOccurrence(@Context HttpServletRequest request, @PathParam("username") String username, @PathParam("occurrence_code") long occurrence_code)
            throws MyEntityNotFoundException {
        try {
            // Get the input stream from the request
            InputStream inputStream = request.getInputStream();

            // Read the message body from the input stream
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(inputStream);

            // Extract the values from the JSON object
            String description = rootNode.get("description").asText();

            repairerBean.failOccurrence(username, occurrence_code, description);
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

    @PATCH
    @Authenticated
    @RolesAllowed({"Repairer"})
    @Path("/{username}/occurrences/{occurrence_code}/finish")
    public Response finishOccurrence(@Context HttpServletRequest request, @PathParam("username") String username, @PathParam("occurrence_code") long occurrence_code)
            throws MyEntityNotFoundException {
        try {
            // Get the input stream from the request
            InputStream inputStream = request.getInputStream();

            // Read the message body from the input stream
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(inputStream);

            // Extract the values from the JSON object
            String description = rootNode.get("description").asText();

            repairerBean.finishOccurrence(username, occurrence_code, description);
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

    @GET
    @Authenticated
    @RolesAllowed({"Repairer"})
    @Path("/{username}/occurrences/assigned")
    public Response getAssignedOccurrences(@PathParam("username") String username) throws MyEntityNotFoundException {
        if (!securityContext.getUserPrincipal().getName().equals(username)) {
            throw new ForbiddenException(username + ", You are not allowed to access this resource");
        }

        return Response.ok(OccurrenceDTO.from(repairerBean.occurrences(username))).build();
    }

    /**
     * Repairer
     * CRUD
     */

    @GET
    @Authenticated
    @RolesAllowed({"Administrator", "Client"})
    @Path("/")
    public List<RepairerDTO> getAllRepairers() {
        return RepairerDTO.from(repairerBean.getAllRepairers());
    }

    @GET
    @Authenticated
    @RolesAllowed({"Administrator", "Repairer"})
    @Path("/{username}")
    public Response getRepairer(@PathParam("username") String username) {
        try {
            if (!securityContext.getUserPrincipal().getName().equals(username)) {
                throw new ForbiddenException(username + ", You are not allowed to access this resource");
            }

            Repairer repairer = repairerBean.findOrFail(username);

            return Response.ok(RepairerDTO.from(repairer)).build();
        } catch (MyEntityNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (ForbiddenException e) {
            return Response.status(Response.Status.FORBIDDEN).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @POST
    @Authenticated
    @RolesAllowed({"Administrator"})
    @Path("/")
    public Response create(RepairerCreateDTO repairerDTO) throws MyEntityExistsException {
        try {
            Repairer repairer = repairerBean.create(
                    repairerDTO.getUsername(),
                    repairerDTO.getPassword(),
                    repairerDTO.getName(),
                    repairerDTO.getEmail(),
                    repairerDTO.getAddress()
            );

            return Response.status(Response.Status.CREATED)
                    .entity(RepairerDTO.from(repairer))
                    .build();
        } catch (MyEntityExistsException e) {
            return Response.status(Response.Status.CONFLICT).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @PUT
    @Authenticated
    @RolesAllowed({"Administrator", "Repairer"})
    @Path("/{username}")
    public Response update(@PathParam("username") String username, RepairerDTO repairerDTO) throws MyEntityNotFoundException {
        try {
            if (!securityContext.getUserPrincipal().getName().equals(username)) {
                throw new ForbiddenException(username + ", You are not allowed to access this resource");
            }

        Repairer repairer = repairerBean.update(
                username,
                repairerDTO.getName(),
                repairerDTO.getEmail(),
                repairerDTO.getAddress()
        );

        return Response.status(Response.Status.OK)
                .entity(RepairerDTO.from(repairer))
                .build();
        } catch (MyEntityNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (ForbiddenException e) {
            return Response.status(Response.Status.FORBIDDEN).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @PATCH
    @Authenticated
    @RolesAllowed({"Repairer"})
    @Path("/{username}/password")
    public Response updatePassword(@PathParam("username") String username, UpdatePasswordDTO repairerDTO) {
        if(!securityContext.getUserPrincipal().getName().equals(username)) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        repairerBean.updatePassword(username, repairerDTO.getPassword(), repairerDTO.getOldPassword());

        return Response.status(Response.Status.OK)
                .entity("Password updated")
                .build();
    }


    @DELETE
    @Authenticated
    @RolesAllowed({"Administrator"})
    @Path("/{username}")
    public Response delete(@PathParam("username") String username) throws MyEntityNotFoundException {
        repairerBean.delete(username);
        return Response.status(Response.Status.ACCEPTED).build();
    }
}
