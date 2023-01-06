package ipleiria.dae.project.ws;

import ipleiria.dae.project.dtos.Auth;
import ipleiria.dae.project.dtos.UserDTO;
import ipleiria.dae.project.ejbs.AdministratorBean;
import ipleiria.dae.project.ejbs.UserBean;
import ipleiria.dae.project.entities.Administrator;
import ipleiria.dae.project.entities.User;
import ipleiria.dae.project.exceptions.APIBadResponseException;
import ipleiria.dae.project.exceptions.EntityManagerPersistDBException;
import ipleiria.dae.project.exceptions.MyEntityExistsException;
import ipleiria.dae.project.exceptions.MyEntityNotFoundException;
import ipleiria.dae.project.security.Authenticated;
import ipleiria.dae.project.security.Hasher;
import ipleiria.dae.project.security.TokenIssuer;

import javax.annotation.security.RolesAllowed;
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
    @EJB
    private AdministratorBean administratorBean;
    @Context
    private SecurityContext securityContext;
    @Inject
    private Hasher hasher;

    @GET
    @Authenticated
    @Path("/user")
    public Response getAuthenticatedUser() throws MyEntityNotFoundException {
        String username = securityContext.getUserPrincipal().getName();
        User user = userBean.findOrFail(username);
        if (user == null) {
            throw new MyEntityNotFoundException("User not found");
        }
        return Response.ok(UserDTO.from(user)).build();
    }

    @POST
    @RolesAllowed({"Expert", "Repairer", "Client"})
    @Path("/login")
    public Response authenticate(@Valid Auth auth) throws MyEntityNotFoundException {
        if (userBean.canLogin(auth.getUsername(), auth.getPassword())) {
            String token = issuer.issue(auth.getUsername());
            return Response.ok(token).build();
        }
        return Response.status(Response.Status.UNAUTHORIZED).header("Error-Message", "The password provided was incorrect").build();
    }

    @POST
    @RolesAllowed({"Administrator"})
    @Path("/login/admin")
    public Response authenticateAdmin(@Valid Auth auth) throws MyEntityNotFoundException, APIBadResponseException, EntityManagerPersistDBException {
        Administrator administrator = userBean.canAdminLogin(auth.getUsername());
        if (administrator.getPassword().equals(hasher.hash(auth.getPassword()))) {
            administratorBean.create(administrator);
            String token = issuer.issue(auth.getUsername());
            return Response.ok(token).build();
        }
        return Response.status(Response.Status.UNAUTHORIZED).header("Error-Message", "The password provided was incorrect").build();
    }
}