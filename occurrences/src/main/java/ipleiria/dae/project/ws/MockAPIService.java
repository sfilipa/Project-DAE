package ipleiria.dae.project.ws;

import ipleiria.dae.project.ejbs.MockAPIBean;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("mock")
@Consumes({"application/json"})
@Produces({"application/json"})
public class MockAPIService {
    @EJB
    private MockAPIBean mockAPIBean;

    @GET
    @Path("/")
    public Response getMockData() {
        return Response.ok(mockAPIBean.getAllDataAPI()).build();
    }

    @GET
    @Path("/{code}")
    public Response getMockDataByCode(@PathParam("code") String code) {
        return Response.ok(mockAPIBean.getDataAPICode(code)).build();
    }


}
