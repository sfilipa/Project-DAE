package ipleiria.dae.project.ws;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ipleiria.dae.project.dtos.create.RepairerCreateDTO;
import ipleiria.dae.project.dtos.RepairerDTO;
import ipleiria.dae.project.ejbs.EmailBean;
import ipleiria.dae.project.ejbs.RepairerBean;
import ipleiria.dae.project.entities.Repairer;
import ipleiria.dae.project.exceptions.MyEntityExistsException;
import ipleiria.dae.project.exceptions.MyEntityNotFoundException;

import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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

    @GET // means: to call this endpoint, we need to use the HTTP GET method
    @Path("/") // means: the relative url path is “/api/repairers/”
    public List<RepairerDTO> getAllRepairers() {
        return RepairerDTO.from(repairerBean.getAllRepairers());
    }

    @GET
    @Path("/{username}")
    public Response getRepairer(@PathParam("username") String username) throws MyEntityNotFoundException {
        Repairer repairer = repairerBean.find(username);
        if(repairer == null){
            throw new MyEntityNotFoundException("Repairer not found");
        }
        return Response.ok(RepairerDTO.from(repairer)).build();
    }

    @POST
    @Path("/")
    public Response create(RepairerCreateDTO repairerDTO) throws MyEntityExistsException {

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
    }

    @PUT
    @Path("/{username}")
    public Response update(RepairerCreateDTO repairerDTO) throws MyEntityNotFoundException{
        Repairer repairer = repairerBean.update(
                repairerDTO.getUsername(),
                repairerDTO.getPassword(),
                repairerDTO.getName(),
                repairerDTO.getEmail(),
                repairerDTO.getAddress()
        );

        return Response.status(Response.Status.OK)
                .entity(RepairerDTO.from(repairer))
                .build();
    }

    @DELETE
    @Path("/{username}")
    public Response delete(@PathParam("username") String username) throws MyEntityNotFoundException {
        repairerBean.delete(username);
        return Response.status(Response.Status.ACCEPTED).build();
    }

    @PATCH
    @Path("/{username}/occurrences/{occurrence_code}/assign")
    public Response assignOccurrence(@PathParam("username") String username, @PathParam("occurrence_code") long occurrence_code)
    throws MyEntityNotFoundException{
        try {
            repairerBean.assignOccurrence(username, occurrence_code);
            return Response.ok().build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @PATCH
    @Path("/{username}/occurrences/{occurrence_code}/unassign")
    public Response unassignOccurrence(@Context HttpServletRequest request, @PathParam("username") String username, @PathParam("occurrence_code") long occurrence_code)
    throws MyEntityNotFoundException{
        try {
            repairerBean.unassignOccurrence(username, occurrence_code);
            return Response.ok().build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @PATCH
    @Path("/{username}/occurrences/{occurrence_code}/start")
    public Response startOccurrence(@PathParam("username") String username, @PathParam("occurrence_code") long occurrence_code)
    throws MyEntityNotFoundException{
        try {
            repairerBean.startOccurrence(username, occurrence_code);
            return Response.ok().build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @PATCH
    @Path("/{username}/occurrences/{occurrence_code}/fail")
    public Response failOccurrence(@Context HttpServletRequest request, @PathParam("username") String username, @PathParam("occurrence_code") long occurrence_code)
        throws MyEntityNotFoundException{
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
        } catch (IOException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @PATCH
    @Path("/{username}/occurrences/{occurrence_code}/finish")
    public Response finishOccurrence(@Context HttpServletRequest request, @PathParam("username") String username, @PathParam("occurrence_code") long occurrence_code)
        throws MyEntityNotFoundException{
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
        } catch (IOException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }
}
