package ipleiria.dae.project.dtos;

import ipleiria.dae.project.entities.Insurance;
import ipleiria.dae.project.entities.Occurrence;
import ipleiria.dae.project.enumerators.CoverageType;
import ipleiria.dae.project.enumerators.InsuredAssetType;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class InsuranceDTO implements Serializable {
    @Id
    private String code;
    private long policyNumber;
    private String insuranceCompany;
    private long clientNif;
    private String clientName;
    private String initialDate;
    private String validUntil;
    private String objectInsured;
    private InsuredAssetType insuredAssetType;
    private String description;
    private List<CoverageType> covers;

    public static InsuranceDTO from(Insurance insurance) {
        return new InsuranceDTO(
                insurance.getCode(),
                insurance.getPolicyNumber(),
                insurance.getInsuranceCompany(),
                insurance.getClientNif(),
                insurance.getClientName(),
                insurance.getInitialDate(),
                insurance.getValidUntil(),
                insurance.getObjectInsured(),
                insurance.getInsuredAssetType(),
                insurance.getDescription(),
                insurance.getCovers()
        );
    }

    public static List<InsuranceDTO> from(List<Insurance> insurances) {
        return insurances.stream().map(InsuranceDTO::from).collect(Collectors.toList());
    }

    public InsuranceDTO() {
    }

    public InsuranceDTO(String code, long policyNumber, String insuranceCompany, long clientNif, String clientName, String initialDate, String validUntil, String objectInsured, InsuredAssetType insuredAssetType, String description) {
        this.code = code;
        this.policyNumber = policyNumber;
        this.insuranceCompany = insuranceCompany;
        this.clientNif = clientNif;
        this.clientName = clientName;
        this.initialDate = initialDate;
        this.validUntil = validUntil;
        this.objectInsured = objectInsured;
        this.insuredAssetType = insuredAssetType;
        this.description = description;
        covers = new LinkedList<>();
    }

    public InsuranceDTO(String code, long policyNumber, String insuranceCompany, long clientNif, String clientName, String initialDate, String validUntil, String objectInsured, InsuredAssetType insuredAssetType, String description, List<CoverageType> covers) {
        this.code = code;
        this.policyNumber = policyNumber;
        this.insuranceCompany = insuranceCompany;
        this.clientNif = clientNif;
        this.clientName = clientName;
        this.initialDate = initialDate;
        this.validUntil = validUntil;
        this.objectInsured = objectInsured;
        this.insuredAssetType = insuredAssetType;
        this.description = description;
        this.covers = covers;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public long getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(long policyNumber) {
        this.policyNumber = policyNumber;
    }

    public String getInsuranceCompany() {
        return insuranceCompany;
    }

    public void setInsuranceCompany(String insuranceCompany) {
        this.insuranceCompany = insuranceCompany;
    }

    public long getClientNif() {
        return clientNif;
    }

    public void setClientNif(long clientNif) {
        this.clientNif = clientNif;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getInitialDate() {
        return initialDate;
    }

    public void setInitialDate(String initialDate) {
        this.initialDate = initialDate;
    }

    public String getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(String validUntil) {
        this.validUntil = validUntil;
    }

    public String getObjectInsured() {
        return objectInsured;
    }

    public void setObjectInsured(String objectInsured) {
        this.objectInsured = objectInsured;
    }

    public InsuredAssetType getInsuredAssetType() {
        return insuredAssetType;
    }

    public void setInsuredAssetType(InsuredAssetType insuredAssetType) {
        this.insuredAssetType = insuredAssetType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<CoverageType> getCovers() {
        return covers;
    }

    public void setCovers(List<CoverageType> covers) {
        this.covers = covers;
    }
}
