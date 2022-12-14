package ipleiria.dae.project.ws;

import ipleiria.dae.project.ejbs.OccurrenceBean;
import ipleiria.dae.project.entities.Occurrence;

import javax.ws.rs.*;
import javax.ejb.EJB;
import javax.ws.rs.core.MediaType;

@Path("occurrences")
@Produces({MediaType.APPLICATION_JSON}) // injects header “Content-Type: application/json”
@Consumes({MediaType.APPLICATION_JSON})
public class OccurrenceService {
    @EJB
    private OccurrenceBean occurrenceBean;

    @GET
    @Path("/")
    public List<OccurrenceDTO> getAllOccurrences(){
        return toDTOsOccurrences();
    }

//    private OccurrenceDTO toDTO(Occurrence occurrence){
//        return new OccurrenceDTO(
//
//        );
//    }
}
