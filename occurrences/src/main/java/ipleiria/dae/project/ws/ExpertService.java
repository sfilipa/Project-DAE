package ipleiria.dae.project.ws;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ipleiria.dae.project.dtos.ClientDTO;
import ipleiria.dae.project.dtos.EmailDTO;
import ipleiria.dae.project.dtos.ExpertDTO;
import ipleiria.dae.project.dtos.OccurrenceDTO;
import ipleiria.dae.project.dtos.create.ExpertCreateDTO;
import ipleiria.dae.project.ejbs.EmailBean;
import ipleiria.dae.project.ejbs.ExpertBean;
import ipleiria.dae.project.entities.Client;
import ipleiria.dae.project.entities.Expert;
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
import java.io.InputStream;
import java.util.List;

@Path("experts") // relative url web path for this service
@Produces({MediaType.APPLICATION_JSON}) // injects header “Content-Type: application/json”
@Consumes({MediaType.APPLICATION_JSON})
public class ExpertService {
    @EJB
    private ExpertBean expertBean;

    @EJB
    private EmailBean emailBean;

    @Context
    private SecurityContext securityContext;

    /**
     * Expert
     * Repairers
     */

    @PATCH
    @Authenticated
    @RolesAllowed({"Expert"})
    @Path("/{username}/occurrences/{occurrence_id}/acceptRepairer")
    public Response acceptRepairer(@PathParam("username") String username, @PathParam("occurrence_id") long occurrence_id) throws MyEntityNotFoundException, NotAuthorizedException {
        try {
            expertBean.acceptRepairer(username, occurrence_id);
            return Response.status(Response.Status.OK).build();
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
    @RolesAllowed({"Expert"})
    @Path("/{username}/occurrences/{occurrence_id}/rejectRepairer")
    public Response rejectRepairer(@Context HttpServletRequest request, @PathParam("username") String username, @PathParam("occurrence_id") long occurrence_id) throws MyEntityNotFoundException, NotAuthorizedException {
        try {
            // Get the input stream from the request
            InputStream inputStream = request.getInputStream();

            // Read the message body from the input stream
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(inputStream);

            // Extract the values from the JSON object
            String description = rootNode.get("description").asText();

            expertBean.rejectRepairer(username, occurrence_id, description);
            return Response.status(Response.Status.OK).build();
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


    /**
     * Expert
     * Occurrences
     */

    @PATCH
    @Authenticated
    @RolesAllowed({"Expert"})
    @Path("/{username}/occurrences/{occurrence_id}/assign")
    public Response assignOccurrence(@PathParam("username") String username, @PathParam("occurrence_id") long occurrence_id) {
        try {
            expertBean.addOccurrence(username, occurrence_id);
            return Response.status(Response.Status.OK).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @PATCH
    @Authenticated
    @RolesAllowed({"Expert"})
    @Path("/{username}/occurrences/{occurrence_id}/unassign")
    public Response unassignOccurrence(@PathParam("username") String username, @PathParam("occurrence_id") long occurrence_id) {
        try {
            expertBean.removeOccurrence(username, occurrence_id);
            return Response.status(Response.Status.OK).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @GET
    @Authenticated
    @RolesAllowed({"Expert"})
    @Path("/{username}/occurrences/assigned")
    public Response getAssignedOccurrences(@PathParam("username") String username) throws MyEntityNotFoundException {
        if (!securityContext.getUserPrincipal().getName().equals(username)) {
            throw new ForbiddenException(username + ", You are not allowed to access this resource");
        }

        return Response.ok(OccurrenceDTO.from(expertBean.occurrences(username))).build();
    }

    @PATCH
    @Authenticated
    @RolesAllowed({"Expert"})
    @Path("/{username}/occurrences/{occurrence_id}/disapprove")
    public Response disapproveOccurrence(@Context HttpServletRequest request,
                                         @PathParam("username") String username,
                                         @PathParam("occurrence_id") long occurrence_id) {
        try {
            // Get the input stream from the request
            InputStream inputStream = request.getInputStream();

            // Read the message body from the input stream
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(inputStream);

            // Extract the values from the JSON object
            String description = rootNode.get("description").asText();

            expertBean.disapproveOccurrence(username, occurrence_id, description);
            return Response.ok().build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @PATCH
    @Authenticated
    @RolesAllowed({"Expert"})
    @Path("/{username}/occurrences/{occurrence_id}/approve")
    public Response approveOccurrence(@Context HttpServletRequest request, @PathParam("username") String username, @PathParam("occurrence_id") long occurrence_id) {
        try {
            // Get the input stream from the request
            InputStream inputStream = request.getInputStream();

            // Read the message body from the input stream
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(inputStream);

            // Extract the values from the JSON object
            String description = rootNode.get("description").asText();

            expertBean.approveOccurrence(username, occurrence_id, description);
            return Response.ok().build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    /**
     * Expert
     * CRUD
     */

    @GET
    @Authenticated
    @RolesAllowed({"Administrator"})
    @Path("/")
    public List<ExpertDTO> getAllExperts() {
        return ExpertDTO.from(expertBean.getAllExperts());
    }

    @GET
    @Authenticated
    @RolesAllowed({"Administrator", "Expert"})
    @Path("/{username}")
    public Response getExpert(@PathParam("username") String username) throws MyEntityNotFoundException {
        try {
            if (!securityContext.getUserPrincipal().getName().equals(username)) {
                throw new ForbiddenException(username + ", You are not allowed to access this resource");
            }

            Expert expert = expertBean.findOrFail(username);

            return Response.ok(ExpertDTO.from(expert)).build();
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
    public Response create(ExpertCreateDTO expertDTO) throws MyEntityExistsException {
        try {
            Expert expert = expertBean.create(
                    expertDTO.getUsername(),
                    expertDTO.getPassword(),
                    expertDTO.getName(),
                    expertDTO.getEmail(),
                    expertDTO.getCompany_username()
            );

            return Response.status(Response.Status.CREATED)
                    .entity(ExpertDTO.from(expert))
                    .build();
        } catch (MyEntityExistsException e) {
            return Response.status(Response.Status.CONFLICT).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @PUT
    @Authenticated
    @RolesAllowed({"Administrator", "Expert"})
    @Path("/{username}")
    public Response update(@PathParam("username") String username, ExpertDTO expertDTO) throws MyEntityNotFoundException {
        try {
            if (!securityContext.getUserPrincipal().getName().equals(username)) {
                throw new ForbiddenException(username + ", You are not allowed to access this resource");
            }

            Expert expert = expertBean.update(
                    username,
                    expertDTO.getName(),
                    expertDTO.getEmail()
            );

            return Response.status(Response.Status.OK)
                    .entity(ExpertDTO.from(expert))
                    .build();
        } catch (MyEntityNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (ForbiddenException e) {
            return Response.status(Response.Status.FORBIDDEN).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @PATCH
    @Authenticated
    @RolesAllowed({"Expert"})
    @Path("/{username}/password")
    public Response updatePassword(@PathParam("username") String username, ExpertCreateDTO expertDTO) {
        if(!securityContext.getUserPrincipal().getName().equals(username)) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        Expert expert = expertBean.updatePassword(username, expertDTO.getPassword());

        if (expert == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.status(Response.Status.OK)
                .entity(ExpertDTO.from(expert))
                .build();
    }

    @PATCH
    @Authenticated
    @RolesAllowed({"Administrator"})
    @Path("/{username}/insuranceCompany/{company_username}")
    public Response updateInsuranceCompany(@PathParam("username") String username, @PathParam("company_username") String insuranceCompany) {
        expertBean.updateInsuranceCompany(username, insuranceCompany);
        return Response.status(Response.Status.OK)
                .entity("Insurance company updated for expert " + username)
                .build();
    }

    @DELETE
    @Authenticated
    @RolesAllowed({"Administrator"})
    @Path("/{username}")
    public Response delete(@PathParam("username") String username) throws MyEntityNotFoundException {
        expertBean.delete(username);
        return Response.status(Response.Status.ACCEPTED).build();
    }

    @POST
    @Authenticated
    @RolesAllowed({"Expert"})
    @Path("/{username}/email/send")
    public Response sendEmail(@PathParam("username") String username, EmailDTO email) throws MyEntityNotFoundException {
        try {
            if (!securityContext.getUserPrincipal().getName().equals(username)) {
                throw new ForbiddenException(username + ", You are not allowed to access this resource");
            }

            Expert expert = expertBean.findOrFail(username);

            emailBean.send(expert.getEmail(), email.getSubject(), email.getMessage());
            return Response.noContent().build();
        } catch (MyEntityNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (ForbiddenException e) {
            return Response.status(Response.Status.FORBIDDEN).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }
}
