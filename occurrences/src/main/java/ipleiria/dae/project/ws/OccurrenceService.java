package ipleiria.dae.project.ws;

import ipleiria.dae.project.dtos.OccurrenceDTO;
import ipleiria.dae.project.ejbs.ExpertBean;
import ipleiria.dae.project.ejbs.OccurrenceBean;
import ipleiria.dae.project.ejbs.RepairerBean;
import ipleiria.dae.project.entities.Expert;
import ipleiria.dae.project.entities.Occurrence;
import ipleiria.dae.project.entities.Repairer;

import javax.ws.rs.*;
import javax.ejb.EJB;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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

    @GET
    @Path("/")
    public List<OccurrenceDTO> getAllOccurrences(){
        return toDTOS(occurrenceBean.getAllOccurrences());
    }

    @GET
    @Path("/{id}")
    public Response getOccurrenceDetails(@PathParam("id") long id){
        Occurrence occurrence = occurrenceBean.find(id);
        if(occurrence != null){
            return Response.ok().entity(toDTO(occurrence)).build();
        }
        return Response.status(Response.Status.NOT_FOUND)
                .entity("ERROR_FINDING_OCCURRENCE")
                .build();
    }

    @POST
    @Path("/")
    public Response create(OccurrenceDTO occurrenceDTO){
        Occurrence occurrence = occurrenceBean.create(
                occurrenceDTO.getUsernameClient(),
                occurrenceDTO.getDate(),
                occurrenceDTO.getInsuredAssetType(),
                occurrenceDTO.getState(),
                occurrenceDTO.getInsuranceCode(),
                occurrenceDTO.getDescription()
        );
        if(occurrence == null){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.status(Response.Status.CREATED)
                .entity(toDTO(occurrence))
                .build();
    }

    @PUT
    @Path("/{id}")
    public Response updateOccurrence(@PathParam("id") long id, OccurrenceDTO occurrenceDTO){
        Occurrence occurrence;
        try{
            occurrence = occurrenceBean.update(
                    id,
                    occurrenceDTO.getUsernameClient(),
                    occurrenceDTO.getDate(),
                    occurrenceDTO.getState(),
                    occurrenceDTO.getInsuranceCode()
            );
        }catch (Exception e){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.status(Response.Status.ACCEPTED).entity(toDTO(occurrence)).build();
    }

    @PATCH
    @Path("/{id}/expert/{username}")
    //a seguradora atribui um perito à ocorrencia - verificar que o perito é da mesma seguradora
    public Response addExpert(@PathParam("id") long id, @PathParam("username") String username) {
        Occurrence occurrence = occurrenceBean.find(id);
        if(occurrence == null){
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("ERROR_FINDING_OCCURRENCE")
                    .build();
        }
        Expert expert = expertBean.find(username);
        if(expert == null){
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("ERROR_FINDING_EXPERT")
                    .build();
        }

        int response = occurrenceBean.addExpert(id, username);
        switch (response){
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
//            case -4:
//                return Response.status(Response.Status.BAD_REQUEST)
//                        .entity("EXPERT_IS_ALREADY_ASSOCIATED_TO_THAT_OCCURRENCE")
//                        .build();
            case -5:
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("EXPERT_NOT_FROM_SAME_INSURANCE")
                        .build();
            default:
                return Response.status(Response.Status.ACCEPTED)
                        .entity(toDTO(occurrence))
                        .build();
        }
    }

    @PATCH
    @Path("/{id}/approve")
    public Response approveOccurrence(@PathParam("id") long id){
        Occurrence occurrence = occurrenceBean.find(id);
        if(occurrence == null){
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("ERROR_FINDING_OCCURRENCE")
                    .build();
        }

        occurrenceBean.approveOccurrence(id);

        return Response.status(Response.Status.OK).build();
    }

    @PATCH
    @Path("/{id}/disapprove")
    public Response disapproveOccurrence(@PathParam("id") long id){
        Occurrence occurrence = occurrenceBean.find(id);
        if(occurrence == null){
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("ERROR_FINDING_OCCURRENCE")
                    .build();
        }

        occurrenceBean.disapproveOccurrence(id);

        return Response.status(Response.Status.OK).build();
    }

    @PATCH
    @Path("/{id}/repairer/{username}") //o perito, caso aprove a cobertura, atribui um reparador à ocorrência
    public Response addRepairer(@PathParam("id") long id, @PathParam("username") String username){
        Occurrence occurrence = occurrenceBean.find(id);
        if(occurrence == null){
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("ERROR_FINDING_OCCURRENCE")
                    .build();
        }
        Repairer repairer = repairerBean.find(username);
        if(repairer == null){
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("ERROR_FINDING_REPAIRER")
                    .build();
        }

        int response = occurrenceBean.addRepairer(id, username);
        switch (response){
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
//            case -4:
//                return Response.status(Response.Status.BAD_REQUEST)
//                        .entity("REPAIRER_IS_ALREADY_ASSOCIATED_TO_THAT_OCCURRENCE")
//                        .build();
            default:
                return Response.status(Response.Status.ACCEPTED)
                        .entity(toDTO(occurrence))
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
    public Response deleteOccurrence(@PathParam("id") long id){
        try{
            occurrenceBean.delete(id);
        }catch (Exception exception){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.status(Response.Status.OK).build();
    }

    private List<OccurrenceDTO> toDTOS(List<Occurrence> allOccurrences) {
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
                occurrence.getDescription()
        );
    }
}
