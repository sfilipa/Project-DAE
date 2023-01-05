package ipleiria.dae.project.ws;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ipleiria.dae.project.dtos.ExpertCreateDTO;
import ipleiria.dae.project.dtos.EmailDTO;
import ipleiria.dae.project.dtos.ExpertDTO;
import ipleiria.dae.project.dtos.InsuranceDTO;
import ipleiria.dae.project.dtos.OccurrenceDTO;
import ipleiria.dae.project.ejbs.EmailBean;
import ipleiria.dae.project.ejbs.ExpertBean;
import ipleiria.dae.project.entities.Expert;
import ipleiria.dae.project.exceptions.MyEntityExistsException;
import ipleiria.dae.project.exceptions.MyEntityNotFoundException;
import ipleiria.dae.project.security.Authenticated;

import javax.ejb.EJB;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

@Path("experts") // relative url web path for this service
@Produces({MediaType.APPLICATION_JSON}) // injects header “Content-Type: application/json”
@Consumes({MediaType.APPLICATION_JSON})
public class ExpertService {
    @EJB
    private ExpertBean expertBean;

    @EJB
    private EmailBean emailBean;

    /**
     * Expert
     *  Repairers
     */

    @PATCH
    @Path("/{username}/occurrences/{occurrence_id}/acceptRepairer")
    public Response acceptRepairer(@PathParam("username") String username, @PathParam("occurrence_id") long occurrence_id){
        try{
            expertBean.acceptRepairer(username, occurrence_id);
            return Response.status(Response.Status.OK).build();
        }catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @PATCH
    @Path("/{username}/occurrences/{occurrence_id}/rejectRepairer")
    public Response rejectRepairer(@Context HttpServletRequest request, @PathParam("username") String username, @PathParam("occurrence_id") long occurrence_id){
        try{
            // Get the input stream from the request
            InputStream inputStream = request.getInputStream();

            // Read the message body from the input stream
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(inputStream);

            // Extract the values from the JSON object
            String description = rootNode.get("description").asText();

            expertBean.rejectRepairer(username, occurrence_id, description);
            return Response.status(Response.Status.OK).build();
        }catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }


    /**
     * Expert
     *  Occurrences
     */

    @PATCH
    @Path("/{username}/occurrences/{occurrence_id}/assign")
    public Response assignOccurrence(@PathParam("username") String username, @PathParam("occurrence_id") long occurrence_id) {
        try{
            expertBean.addOccurrence(username, occurrence_id);
            return Response.status(Response.Status.OK).build();
        }catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @PATCH
    @Path("/{username}/occurrences/{occurrence_id}/unassign")
    public Response unassignOccurrence(@PathParam("username") String username, @PathParam("occurrence_id") long occurrence_id) {
        try{
            expertBean.removeOccurrence(username, occurrence_id);
            return Response.status(Response.Status.OK).build();
        }catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/{username}/occurrences/assigned")
    public Response getAssignedOccurrences(@PathParam("username") String username) {
        return Response.ok(OccurrenceDTO.from(expertBean.occurrences(username))).build();
    }

    @PATCH
    @Path("/{username}/occurrences/{occurrence_id}/disapprove")
    public Response disapproveOccurrence(@Context HttpServletRequest request, @PathParam("username") String username, @PathParam("occurrence_id") long occurrence_id) {
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
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @PATCH
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
     *  CRUD
     */

    @GET // means: to call this endpoint, we need to use the HTTP GET method
    @Path("/") // means: the relative url path is “/api/students/”
    public List<ExpertDTO> getAllExperts() {
        return ExpertDTO.from(expertBean.getAllExperts());
    }

    @GET
    @Path("/{username}")
    public Response getExpert(@PathParam("username") String username) {
        Expert expert = expertBean.find(username);
        if(expert == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(ExpertDTO.from(expert)).build();
    }

    @POST
    @Path("/")
    public Response create(ExpertCreateDTO expertDTO) throws MyEntityExistsException {
        Expert expert = expertBean.create(
                expertDTO.getUsername(),
                expertDTO.getPassword(),
                expertDTO.getName(),
                expertDTO.getEmail(),
                expertDTO.getCompany_username()
        );

        if(expert == null){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        return Response.status(Response.Status.CREATED)
                .entity(ExpertDTO.from(expert))
                .build();
    }

    @PUT
    @Path("/{username}")
    public Response update(@PathParam("username") String username, ExpertCreateDTO expertDTO) throws MyEntityNotFoundException {
        Expert expert = expertBean.update(
                username,
                expertDTO.getPassword(),
                expertDTO.getName(),
                expertDTO.getEmail(),
                expertDTO.getCompany_username()
        );

        if(expert == null){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        return Response.status(Response.Status.OK)
                .entity(ExpertDTO.from(expert))
                .build();
    }

    @DELETE
    @Path("/{username}")
    public Response delete(@PathParam("username") String username) {

        expertBean.delete(username);

        return Response.status(Response.Status.ACCEPTED).build();
    }

    @POST
    @Authenticated
    @Path("/{username}/email/send")
    public Response sendEmail(@PathParam("username") String username, EmailDTO email) throws MessagingException {
        Expert expert = expertBean.find(username);

        if(expert == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        emailBean.send(expert.getEmail(), email.getSubject(), email.getMessage());
        return Response.noContent().build();
    }
}
