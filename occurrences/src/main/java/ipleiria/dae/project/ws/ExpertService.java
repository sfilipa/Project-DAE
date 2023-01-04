package ipleiria.dae.project.ws;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ipleiria.dae.project.dtos.ExpertDTO;
import ipleiria.dae.project.ejbs.ExpertBean;
import ipleiria.dae.project.entities.Expert;

import javax.ejb.EJB;
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

    @PATCH
    @Path("/{username}/occurrences/{occurrence_code}/disapprove")
    public Response disapproveOccurrence(@Context HttpServletRequest request, @PathParam("username") String username, @PathParam("occurrence_code") long occurrence_code) {
        try {
            // Get the input stream from the request
            InputStream inputStream = request.getInputStream();

            // Read the message body from the input stream
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(inputStream);

            // Extract the values from the JSON object
            String description = rootNode.get("description").asText();

            expertBean.disapproveOccurrence(username, occurrence_code, description);
            return Response.ok().build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @PATCH
    @Path("/{username}/occurrences/{occurrence_code}/approve")
    public Response approveOccurrence(@Context HttpServletRequest request, @PathParam("username") String username, @PathParam("occurrence_code") long occurrence_code) {
        try {
            // Get the input stream from the request
            InputStream inputStream = request.getInputStream();

            // Read the message body from the input stream
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(inputStream);

            // Extract the values from the JSON object
            String description = rootNode.get("description").asText();

            expertBean.approveOccurrence(username, occurrence_code, description);
            return Response.ok().build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @PATCH
    @Path("/{username}/occurrences/{occurrence_code}/acceptRepairer")
    public Response acceptRepairer(@PathParam("username") String username, @PathParam("occurrence_code") long occurrence_code){
        expertBean.acceptRepairer(username, occurrence_code);
        return Response.status(Response.Status.OK).build();
    }

    @PATCH
    @Path("/{username}/occurrences/{occurrence_code}/rejectRepairer")
    public Response rejectRepairer(@PathParam("username") String username, @PathParam("occurrence_code") long occurrence_code){
        expertBean.rejectRepairer(username, occurrence_code);
        return Response.status(Response.Status.OK).build();
    }

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
    public Response create(ExpertDTO expertDTO) {
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

    @PATCH
    @Path("/{username}/occurrences/{code}/assign")
    public Response assignOccurrence(@PathParam("username") String username, @PathParam("code") String code) {
        int response = expertBean.addOccurrence(username, code);
        switch (response) {
            case 0:
                return Response.status(Response.Status.OK)
                        .build();
            case -1:
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("ERROR_FINDING_EXPERT")
                        .build();
            case -2:
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("ERROR_FINDING_OCCURRENCE")
                        .build();
            default:
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PATCH
    @Path("/{username}/occurrences/{code}/unassign")
    public Response unassignOccurrence(@PathParam("username") String username, @PathParam("code") String code) {
        int response = expertBean.removeOccurrence(username, code);
        switch (response) {
            case 0:
                return Response.status(Response.Status.OK)
                        .build();
            case -1:
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("ERROR_FINDING_EXPERT")
                        .build();
            case -2:
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("ERROR_FINDING_OCCURRENCE")
                        .build();
            default:
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DELETE
    @Path("/{username}")
    public Response delete(@PathParam("username") String username) {

        expertBean.delete(username);

        return Response.status(Response.Status.ACCEPTED).build();
    }

    //TODO: metodos de approve e disapprove do repairer escolhido pelo cliente

}
