package ipleiria.dae.project.ws;

import ipleiria.dae.project.ejbs.MockAPIBean;
import ipleiria.dae.project.exceptions.APIBadResponseException;
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
    public Response getMockData(@PathParam("resource") String resource) {
        JSONArray jsonArray = mockAPIBean.getDataAPI(resource, "","");
        return Response.ok(jsonArray.toString()).build();
    }

    @GET
    @Path("/{resource}/{attribute}/{attributeToGet}")
    public Response getMockDataByAttribute(@PathParam("resource") String resource, @PathParam("attribute") String attribute, @PathParam("attributeToGet") String attributeToGet) {
        JSONArray jsonArray = mockAPIBean.getDataAPI(resource, attribute , attributeToGet);
        return Response.ok(jsonArray.toString()).build();
    }

    @GET
    @Path("/{resource}/{attribute}/{attributeToGet}/{attributeArrayToFilter}")
    public Response getMockDataRepairers(@PathParam("resource") String resource, @PathParam("attribute") String attribute, @PathParam("attributeToGet") String attributeToGet, @PathParam("attributeArrayToFilter") String attributeArrayToFilter) throws MyEntityNotFoundException, APIBadResponseException {
        List<String> availableRepairers = mockAPIBean.getAttributeFromSpecificInsuranceCompany(resource, attribute, attributeToGet, attributeArrayToFilter.toLowerCase());
        return Response.ok(availableRepairers).build();
    }

}
