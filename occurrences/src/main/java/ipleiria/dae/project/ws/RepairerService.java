package ipleiria.dae.project.ws;

import ipleiria.dae.project.dtos.ExpertDTO;
import ipleiria.dae.project.dtos.RepairerDTO;
import ipleiria.dae.project.ejbs.ExpertBean;
import ipleiria.dae.project.ejbs.RepairerBean;
import ipleiria.dae.project.entities.Expert;
import ipleiria.dae.project.entities.Repairer;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Path("repairers") // relative url web path for this service
@Produces({MediaType.APPLICATION_JSON}) // injects header “Content-Type: application/json”
@Consumes({MediaType.APPLICATION_JSON})
public class RepairerService {
    @EJB
    private RepairerBean repairerBean;

    @GET // means: to call this endpoint, we need to use the HTTP GET method
    @Path("/") // means: the relative url path is “/api/students/”
    public List<RepairerDTO> getAllRepairers() {
        return toDTOs(repairerBean.getAllRepairers());
    }

    private List<RepairerDTO> toDTOs(List<Repairer> repairers) {
        return repairers.stream().map(this::toDTO).collect(Collectors.toList());
    }

    private RepairerDTO toDTO(Repairer repairer) {
        return new RepairerDTO(
                repairer.getUsername(),
                repairer.getPassword(),
                repairer.getName(),
                repairer.getEmail(),
                repairer.getCompany().getUsername()
        );
    }

    @POST
    @Path("/")
    public Response create(RepairerDTO repairerDTO) {

        Repairer repairer = repairerBean.create(
                repairerDTO.getUsername(),
                repairerDTO.getPassword(),
                repairerDTO.getName(),
                repairerDTO.getEmail(),
                repairerDTO.getCompany_username()
        );

        return Response.status(Response.Status.CREATED)
                .entity(toDTO(repairer))
                .build();
    }

    @DELETE
    @Path("/{username}")
    public Response delete(@PathParam("username") String username) {

        repairerBean.delete(username);

        return Response.status(Response.Status.ACCEPTED).build();
    }

}
