package ipleiria.dae.project.ws;

import ipleiria.dae.project.ejbs.MockAPIBean;
import ipleiria.dae.project.exceptions.MyEntityNotFoundException;
import org.json.JSONArray;

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
    @Path("/{resource}")
    public Response getMockData(@PathParam("resource") String resource) throws MyEntityNotFoundException {
        JSONArray jsonArray = mockAPIBean.getDataAPI(resource, "","");
        return Response.ok(jsonArray.toString()).build();
    }

    @GET
    @Path("/{resource}/{attribute}/{attributeToGet}")
    public Response getMockDataByAttribute(@PathParam("resource") String resource, @PathParam("attribute") String attribute, @PathParam("attributeToGet") String attributeToGet) throws MyEntityNotFoundException {
        JSONArray jsonArray = mockAPIBean.getDataAPI(resource, attribute , attributeToGet);
        return Response.ok(jsonArray.toString()).build();
    }

    @GET
    @Path("/{resource}/{attribute}/{attributeToGet}/{attributeToGet2}")
    public Response getMockDataRepairers(@PathParam("resource") String resource, @PathParam("attribute") String attribute, @PathParam("attributeToGet") String attributeToGet, @PathParam("attributeToGet2") String attributeToGet2) throws MyEntityNotFoundException {
        JSONArray jsonArray = mockAPIBean.getAttributeFromSpecificInsuranceCompany(resource, attribute, attributeToGet, attributeToGet2.toLowerCase());
        return Response.ok(jsonArray.toString()).build();
    }

}
