package ipleiria.dae.project.ws;

import ipleiria.dae.project.dtos.CompanyDTO;
import ipleiria.dae.project.dtos.OccurrenceDTO;
import ipleiria.dae.project.ejbs.CompanyBean;
import ipleiria.dae.project.ejbs.ExpertBean;
import ipleiria.dae.project.ejbs.OccurrenceBean;
import ipleiria.dae.project.ejbs.RepairerBean;
import ipleiria.dae.project.entities.Company;
import ipleiria.dae.project.entities.Occurrence;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.List;
import java.util.stream.Collectors;

@Path("companies")
@Produces({MediaType.APPLICATION_JSON}) // injects header “Content-Type: application/json”
@Consumes({MediaType.APPLICATION_JSON})
public class CompanyService {
    @EJB
    private CompanyBean companyBean;

    @Context
    private SecurityContext securityContext;

    @GET
    @Path("/")
    public List<CompanyDTO> getAllCompanies(){
        return toDTOS(companyBean.getAllCompanies());
    }

    @GET
    @Path("/{id}")
    public Response getCompanyDetails(@PathParam("id") String username){
        Company company = companyBean.find(username);
        if(company != null){
            return Response.ok().entity(toDTO(company)).build();
        }
        return Response.status(Response.Status.NOT_FOUND)
                .entity("ERROR_FINDING_OCCURRENCE")
                .build();
    }

    @POST
    @Path("/")
    public Response create(CompanyDTO companyDTO){
        Company company = companyBean.create(
                companyDTO.getUsername(),
                companyDTO.getPassword(),
                companyDTO.getName(),
                companyDTO.getEmail(),
                companyDTO.getAddress(),
                companyDTO.getPhoneNumber(),
                companyDTO.getNipc()
        );
        if(company == null){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.status(Response.Status.CREATED)
                .entity(toDTO(company))
                .build();
    }

    private List<CompanyDTO> toDTOS(List<Company> allCompanies) {
        return allCompanies.stream().map(this::toDTO).collect(Collectors.toList());
    }

    private CompanyDTO toDTO(Company company){
        return new CompanyDTO(
                company.getUsername(),
                company.getPassword(),
                company.getName(),
                company.getEmail(),
                company.getAddress(),
                company.getPhoneNumber(),
                company.getNipc()
        );
    }
}
