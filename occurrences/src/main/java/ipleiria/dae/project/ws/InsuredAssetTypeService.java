package ipleiria.dae.project.ws;

import ipleiria.dae.project.dtos.ExpertDTO;
import ipleiria.dae.project.dtos.InsuredAssetTypeDTO;
import ipleiria.dae.project.ejbs.ExpertBean;
import ipleiria.dae.project.ejbs.InsuredAssetTypeBean;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("insured-asset-type") // relative url web path for this service
@Produces({MediaType.APPLICATION_JSON}) // injects header “Content-Type: application/json”
@Consumes({MediaType.APPLICATION_JSON})
public class InsuredAssetTypeService {

    @EJB
    private InsuredAssetTypeBean insuredAssetTypeBean;

    @GET
    @Path("/")
    public List<InsuredAssetTypeDTO> getAll() {
        return InsuredAssetTypeDTO.from(insuredAssetTypeBean.getInsuredAssetTypes());
    }
}
