package ipleiria.dae.project.ws;

import ipleiria.dae.project.dtos.OccurrenceDTO;
import ipleiria.dae.project.ejbs.ExpertBean;
import ipleiria.dae.project.ejbs.OccurrenceBean;
import ipleiria.dae.project.ejbs.RepairerBean;
import ipleiria.dae.project.entities.Expert;
import ipleiria.dae.project.entities.Occurrence;
import ipleiria.dae.project.entities.Repairer;
import ipleiria.dae.project.exceptions.MyEntityNotFoundException;
import ipleiria.dae.project.security.Authenticated;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ejb.EJB;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

@Path("occurrences")
@Produces({MediaType.APPLICATION_JSON}) // injects header “Content-Type: application/json”
@Consumes({MediaType.APPLICATION_JSON})
public class OccurrenceService {
    @EJB
    private OccurrenceBean occurrenceBean;
    @EJB
    private ExpertBean expertBean;

    @EJB
    private RepairerBean repairerBean;

    @Context
    private SecurityContext securityContext;

    @GET
    @Path("/")
    public List<OccurrenceDTO> getAllOccurrences() {
        return OccurrenceDTO.from(occurrenceBean.getAllOccurrences());
    }

    @GET
    @Path("/{id}")
    public Response getOccurrenceDetails(@PathParam("id") long id) {
        Occurrence occurrence = occurrenceBean.find(id);
        if (occurrence != null) {
            return Response.ok().entity(OccurrenceDTO.from(occurrence)).build();
        }
        return Response.status(Response.Status.NOT_FOUND)
                .entity("ERROR_FINDING_OCCURRENCE")
                .build();
    }

    @POST
    @Authenticated
    @RolesAllowed({"Client"})
    @Path("/")
    public Response create(OccurrenceDTO occurrenceDTO) throws MyEntityNotFoundException {
        if (!securityContext.getUserPrincipal().getName().equals(occurrenceDTO.getUsernameClient())) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        Occurrence occurrence = occurrenceBean.create(
                occurrenceDTO.getUsernameClient(),
                occurrenceDTO.getDate(),
                occurrenceDTO.getState(),
                occurrenceDTO.getInsuranceCode(),
                occurrenceDTO.getDescription()
        );

        if (occurrence == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.status(Response.Status.CREATED)
                .entity(OccurrenceDTO.from(occurrence))
                .build();
    }

    @PUT
    @Authenticated
    @RolesAllowed({"Client"})
    @Path("/{id}")
    public Response updateOccurrence(@PathParam("id") long id, OccurrenceDTO occurrenceDTO) throws MyEntityNotFoundException {
        if (!securityContext.getUserPrincipal().getName().equals(occurrenceDTO.getUsernameClient())) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        Occurrence occurrence;
        occurrence = occurrenceBean.update(
                id,
                occurrenceDTO.getUsernameClient(),
                occurrenceDTO.getDate(),
                occurrenceDTO.getState(),
                occurrenceDTO.getInsuranceCode(),
                occurrenceDTO.getDescription()
        );

        if (occurrence == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.status(Response.Status.OK)
                .entity(OccurrenceDTO.from(occurrence))
                .build();
    }

    @PATCH
    @Path("/{id}/expert/{username}/add")
    //a seguradora atribui um perito à ocorrencia - verificar que o perito é da mesma seguradora
    public Response addExpert(@PathParam("id") long id, @PathParam("username") String username) {
        Occurrence occurrence = occurrenceBean.find(id);
        if (occurrence == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("ERROR_FINDING_OCCURRENCE")
                    .build();
        }
        Expert expert = expertBean.find(username);
        if (expert == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("ERROR_FINDING_EXPERT")
                    .build();
        }

        int response = occurrenceBean.addExpert(id, username);
        switch (response) {
            case -1:
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("ERROR_FINDING_OCCURRENCE")
                        .build();
            case -2:
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("OCCURRENCE_IS_NOT_PENDING_FOR_APPROVAL")
                        .build();
            case -3:
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("ERROR_FINDING_EXPERT")
                        .build();
            case -4:
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("EXPERT_IS_ALREADY_ASSOCIATED_TO_THAT_OCCURRENCE")
                        .build();
            case -5:
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("EXPERT_NOT_FROM_SAME_INSURANCE")
                        .build();
            default:
                return Response.status(Response.Status.ACCEPTED)
                        .entity(OccurrenceDTO.from(occurrence))
                        .build();
        }
    }

    @PATCH
    @Path("/{id}/expert/{username}/unassign")
    public Response unassignExpert(@PathParam("id") long id, @PathParam("username") String username) {
        Occurrence occurrence = occurrenceBean.find(id);
        if (occurrence == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("ERROR_FINDING_OCCURRENCE")
                    .build();
        }
        int response = occurrenceBean.removeExpert(id, username);
        switch (response) {
            case -1:
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("ERROR_FINDING_OCCURRENCE")
                        .build();
            case -2:
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("ERROR_FINDING_EXPERT")
                        .build();
            case -3:
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("EXPERT_IS_NOT_ASSIGNED_TO_THAT_OCCURRENCE")
                        .build();
            default:
                return Response.status(Response.Status.ACCEPTED)
                        .entity(OccurrenceDTO.from(occurrence))
                        .build();
        }
    }

    @PATCH
    @Path("/{id}/approve")
    public Response approveOccurrence(@PathParam("id") long id) {
        Occurrence occurrence = occurrenceBean.find(id);
        if (occurrence == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("ERROR_FINDING_OCCURRENCE")
                    .build();
        }

        occurrenceBean.approveOccurrence(id);

        return Response.status(Response.Status.OK).build();
    }

    @PATCH
    @Path("/{id}/disapprove")
    public Response disapproveOccurrence(@PathParam("id") long id) {
        Occurrence occurrence = occurrenceBean.find(id);
        if (occurrence == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("ERROR_FINDING_OCCURRENCE")
                    .build();
        }

        occurrenceBean.disapproveOccurrence(id);

        return Response.status(Response.Status.OK).build();
    }

    @PATCH
    @Path("/{id}/repairer/{username}/assign") //o perito, caso aprove a cobertura, atribui um reparador à ocorrência
    public Response assignRepairer(@PathParam("id") long id, @PathParam("username") String username) {
        Occurrence occurrence = occurrenceBean.find(id);
        if (occurrence == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("ERROR_FINDING_OCCURRENCE")
                    .build();
        }
        Repairer repairer = repairerBean.find(username);
        if (repairer == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("ERROR_FINDING_REPAIRER")
                    .build();
        }

        int response = occurrenceBean.assignRepairer(id, username);
        switch (response) {
            case -1:
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("ERROR_FINDING_OCCURRENCE")
                        .build();
            case -2:
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("OCCURRENCE_IS_NOT_APPROVED_YET")
                        .build();
            case -3:
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("ERROR_FINDING_REPAIRER")
                        .build();
            default:
                return Response.status(Response.Status.ACCEPTED)
                        .entity(OccurrenceDTO.from(occurrence))
                        .build();
        }
    }

    @PATCH
    @Path("/{id}/repairer/unassign")
    public Response unassignRepairer(@PathParam("id") long id) {
        Occurrence occurrence = occurrenceBean.find(id);
        if (occurrence == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("ERROR_FINDING_OCCURRENCE")
                    .build();
        }

        int response = occurrenceBean.unassignRepairer(id);
        switch (response) {
            case -1:
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("ERROR_FINDING_OCCURRENCE")
                        .build();
            case -2:
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("ERROR_FINDING_REPAIRER")
                        .build();
            default:
                return Response.status(Response.Status.ACCEPTED)
                        .entity(OccurrenceDTO.from(occurrence))
                        .build();
        }
    }

//    @GET
//    @Path("/{id}/document")
//    public Response getOccurrenceDocuments(@PathParam("id") long id){
//        return Response.status(Response.Status.BAD_REQUEST).build();
//    }
//
//    @PUT
//    @Path("/{id}/document")
//    public Response addDocumentsToOccurrence(@PathParam("id") long id){
//        return Response.status(Response.Status.BAD_REQUEST).build();
//    }

    @DELETE
    @Path("/{id}")
    public Response deleteOccurrence(@PathParam("id") long id) throws MyEntityNotFoundException {
        occurrenceBean.delete(id);

        return Response.noContent().build();
    }

    /*private List<OccurrenceDTO> toDTOS(List<Occurrence> allOccurrences) {
        return allOccurrences.stream().map(this::toDTO).collect(Collectors.toList());
    }

    private OccurrenceDTO toDTO(Occurrence occurrence){

        return new OccurrenceDTO(
                occurrence.getId(),
                occurrence.getClient().getUsername(),
                occurrence.getDate(),
                occurrence.getState(),
                occurrence.getInsuredAssetType(),
                occurrence.getInsurance().getCode(),
                occurrence.getInsurance().getName(),
                occurrence.getDescription(),
                occurrence.getObject()
        );
    }*/
}
