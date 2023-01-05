package ipleiria.dae.project.ejbs;

import ipleiria.dae.project.enumerators.InsuredAssetType;

import javax.ejb.Stateless;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Stateless
public class InsuredAssetTypeBean {

    List<InsuredAssetType> insuredAssetTypes;

    public List<InsuredAssetType> getInsuredAssetTypes() {
        insuredAssetTypes = new LinkedList<>();

        // Add each InsuredAssetType to the list
        insuredAssetTypes.addAll(Arrays.asList(InsuredAssetType.values()));
        return insuredAssetTypes;
    }
}
