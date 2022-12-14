package ipleiria.dae.project.ws;

import ipleiria.dae.project.dtos.OccurrenceDTO;
import ipleiria.dae.project.ejbs.OccurrenceBean;
import ipleiria.dae.project.entities.Occurrence;

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

    @GET
    @Path("/")
    public List<OccurrenceDTO> getAllOccurrences(){
        return toDTOS(occurrenceBean.getAllOccurrences());
    }

    @POST
    @Path("/")
    public Response create(OccurrenceDTO occurrenceDTO){
        Occurrence occurrence = occurrenceBean.create(
                occurrenceDTO.getId(),
                occurrenceDTO.getClient(),
                occurrenceDTO.getDate(),
                occurrenceDTO.getInsuredAssetType(),
                occurrenceDTO.getState(),
                occurrenceDTO.getInsurance()
        );
        if(occurrence == null){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.status(Response.Status.CREATED)
                .entity(toDTO(occurrence))
                .build();
    }
    

    private List<OccurrenceDTO> toDTOS(List<Occurrence> allOccurrences) {
        return allOccurrences.stream().map(this::toDTO).collect(Collectors.toList());
    }

    private OccurrenceDTO toDTO(Occurrence occurrence){
        return new OccurrenceDTO(
            occurrence.getId(),
            occurrence.getClient(),
            occurrence.getDate(),
            occurrence.getState(),
            occurrence.getInsuredAssetType(),
            occurrence.getInsurance()
        );
    }
}
