package ipleiria.dae.project.ws;

import ipleiria.dae.project.dtos.Auth;
import ipleiria.dae.project.dtos.UserDTO;
import ipleiria.dae.project.ejbs.AdministratorBean;
import ipleiria.dae.project.ejbs.UserBean;
import ipleiria.dae.project.entities.Administrator;
import ipleiria.dae.project.entities.User;
import ipleiria.dae.project.exceptions.MyEntityExistsException;
import ipleiria.dae.project.exceptions.MyEntityNotFoundException;
import ipleiria.dae.project.security.Authenticated;
import ipleiria.dae.project.security.Hasher;
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
    @EJB
    private AdministratorBean administratorBean;
    @Context
    private SecurityContext securityContext;
    @Inject
    private Hasher hasher;

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

    @POST
    @Path("/login/admin")
    public Response authenticateAdmin(@Valid Auth auth) throws MyEntityNotFoundException, MyEntityExistsException {
        try {
            Administrator administrator = userBean.canAdminLogin(auth.getUsername());
            if (administrator.getPassword().equals(hasher.hash(auth.getPassword()))) {
                administratorBean.create(administrator);
                String token = issuer.issue(auth.getUsername());
                return Response.ok(token).build();
            }
            return Response.status(Response.Status.UNAUTHORIZED).build();
        } catch (MyEntityExistsException e) {
            throw new MyEntityExistsException(e.getMessage());
        } catch (MyEntityNotFoundException e) {
            throw new MyEntityNotFoundException(e.getMessage());
        }
    }
}