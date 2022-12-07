package ipleiria.dae.project.ws;

import ipleiria.dae.project.dtos.Auth;
import ipleiria.dae.project.dtos.UserDTO;
import ipleiria.dae.project.ejbs.UserBean;
import ipleiria.dae.project.entities.User;
import ipleiria.dae.project.security.Authenticated;
import ipleiria.dae.project.security.TokenIssuer;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@Path("auth")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public class AuthService {
    @Inject
    private TokenIssuer issuer;
    @EJB
    private UserBean userBean;

    @Context
    private SecurityContext securityContext;

    @GET
    @Authenticated
    @Path("/user")
    public Response getAuthenticatedUser() {
        String username = securityContext.getUserPrincipal().getName();
        User user = userBean.findOrFail(username);
        return Response.ok(UserDTO.from(user)).build();
    }

    @POST
    @Path("/login")
    public Response authenticate(@Valid Auth auth) {
        if (userBean.canLogin(auth.getUsername(), auth.getPassword())) {
            String token = issuer.issue(auth.getUsername());
            return Response.ok(token).build();
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }
}