package ipleiria.dae.project.ws;

import ipleiria.dae.project.dtos.ExpertDTO;
import ipleiria.dae.project.ejbs.ExpertBean;
import ipleiria.dae.project.entities.Expert;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Path("experts") // relative url web path for this service
@Produces({MediaType.APPLICATION_JSON}) // injects header “Content-Type: application/json”
@Consumes({MediaType.APPLICATION_JSON})
public class ExpertService {
    @EJB
    private ExpertBean expertBean;

    @GET // means: to call this endpoint, we need to use the HTTP GET method
    @Path("/") // means: the relative url path is “/api/students/”
    public List<ExpertDTO> getAllExperts() {
        return toDTOs(expertBean.getAllExperts());
    }

    private List<ExpertDTO> toDTOs(List<Expert> experts) {
        return experts.stream().map(this::toDTO).collect(Collectors.toList());
    }

    private ExpertDTO toDTO(Expert expert) {
        return new ExpertDTO(
                expert.getUsername(),
                expert.getPassword(),
                expert.getName(),
                expert.getEmail(),
                expert.getCompany().getUsername()
        );
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

        return Response.status(Response.Status.CREATED)
                .entity(toDTO(expert))
                .build();
    }

    @DELETE
    @Path("/{username}")
    public Response delete(@PathParam("username") String username) {

        expertBean.delete(username);

        return Response.status(Response.Status.ACCEPTED).build();
    }

}
