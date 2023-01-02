package ipleiria.dae.project.ws;

import ipleiria.dae.project.dtos.OccurrenceDTO;
import ipleiria.dae.project.dtos.PolicyDTO;
import ipleiria.dae.project.ejbs.ClientBean;
import ipleiria.dae.project.ejbs.PolicyBean;
import ipleiria.dae.project.entities.Occurrence;
import ipleiria.dae.project.entities.Policy;
import ipleiria.dae.project.exceptions.MyEntityNotFoundException;
import ipleiria.dae.project.security.Authenticated;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.List;

@Path("policies")
@Produces({MediaType.APPLICATION_JSON}) // injects header “Content-Type: application/json”
@Consumes({MediaType.APPLICATION_JSON})
public class PolicyService {
    @EJB
    private PolicyBean policyBean;
    @EJB
    private ClientBean clientBean;
    @Context
    private SecurityContext securityContext;

    @GET
    @Path("/")
    public List<PolicyDTO> getAllPolicies() {
        return PolicyDTO.from(policyBean.getAllPolicies());
    }

    @GET
    @Path("/{code}")
    public Response getPolicyDetails(@PathParam("code") String code) {
        Policy policy = policyBean.find(code);
        if (policy == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("ERROR_FINDING_POLICY").build();
        }
        return Response.ok().entity(PolicyDTO.from(policy)).build();

    }

    @POST
    @Authenticated
    @Path("/")
    public Response create(PolicyDTO policyDTO) throws MyEntityNotFoundException {
        try {
            // Creates the policy
            Policy policy = policyBean.create(
                    policyDTO.getCode(),
                    policyDTO.getType(),
                    policyDTO.getClient_username(),
                    policyDTO.getCovers());

            if (policy == null) {
                throw new IllegalArgumentException("ERROR_CREATING_POLICY");
            }

            return Response.ok().entity(PolicyDTO.from(policy)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{code}")
    public Response updateOccurrence(@PathParam("code") String code, PolicyDTO policyDTO) {
        try {
            Policy policy = policyBean.update(
                    code,
                    policyDTO.getType(),
                    policyDTO.getClient_username(),
                    policyDTO.getCovers());

            if (policy == null) {
                throw new IllegalArgumentException("ERROR_UPDATING_POLICY");
            }
            return Response.ok().entity(PolicyDTO.from(policy)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{code}")
    public Response deleteOccurrence(@PathParam("code") String code) {
        try {
            policyBean.delete(code);
            return Response.ok().build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
}
