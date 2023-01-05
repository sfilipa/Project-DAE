package ipleiria.dae.project.dtos;

import ipleiria.dae.project.entities.Expert;
import ipleiria.dae.project.enumerators.InsuredAssetType;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class InsuredAssetTypeDTO {

    private InsuredAssetType insuredAssetTypes;

    public InsuredAssetTypeDTO() {
    }

    public InsuredAssetTypeDTO(InsuredAssetType insuredAssetTypes) {
        this.insuredAssetTypes = insuredAssetTypes;
    }

    public static InsuredAssetTypeDTO from(InsuredAssetType insuredAssetType){
        return new InsuredAssetTypeDTO(
            insuredAssetType
        );
    }

    public static List<InsuredAssetTypeDTO> from(List<InsuredAssetType> insuredAssetTypes) {
        return insuredAssetTypes.stream().map(InsuredAssetTypeDTO::from).collect(Collectors.toList());
    }

    public InsuredAssetType getInsuredAssetTypes() {
        return insuredAssetTypes;
    }

    public void setInsuredAssetTypes(InsuredAssetType insuredAssetTypes) {
        this.insuredAssetTypes = insuredAssetTypes;
    }
}
