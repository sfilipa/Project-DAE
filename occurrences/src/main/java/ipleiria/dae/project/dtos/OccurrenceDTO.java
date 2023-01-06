package ipleiria.dae.project.dtos;

import ipleiria.dae.project.entities.Expert;
import ipleiria.dae.project.entities.Occurrence;
import ipleiria.dae.project.enumerators.CoverageType;
import ipleiria.dae.project.enumerators.InsuredAssetType;
import ipleiria.dae.project.enumerators.State;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class OccurrenceDTO implements Serializable {
    @Id
    private long id;
    private String entryDate;
    private String finalDate;
    private String objectInsured;
    private String description;
    private String insuranceCode;
    private State state;
    private String usernameClient;
    private String usernameRepairer;
    private CoverageType coverageType;
    private String insuranceCompanyName;

    public OccurrenceDTO() {
    }

    public OccurrenceDTO(long id, String entryDate, String finalDate, String objectInsured, String description, String insuranceCode, CoverageType coverageType, State state, String usernameClient, String usernameRepairer, String insuranceCompanyName) {
        this.id = id;
        this.entryDate = entryDate;
        this.finalDate = finalDate;
        this.objectInsured = objectInsured;
        this.description = description;
        this.insuranceCode = insuranceCode;
        this.coverageType = coverageType;
        this.state = state;
        this.usernameClient = usernameClient;
        this.usernameRepairer = usernameRepairer;
        this.insuranceCompanyName = insuranceCompanyName;
    }

    public String getInsuranceCompanyName() {
        return insuranceCompanyName;
    }

    public void setInsuranceCompanyName(String insuranceCompanyName) {
        this.insuranceCompanyName = insuranceCompanyName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(String entryDate) {
        this.entryDate = entryDate;
    }

    public String getFinalDate() {
        return finalDate;
    }

    public void setFinalDate(String finalDate) {
        this.finalDate = finalDate;
    }

    public String getObjectInsured() {
        return objectInsured;
    }

    public void setObjectInsured(String objectInsured) {
        this.objectInsured = objectInsured;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInsuranceCode() {
        return insuranceCode;
    }

    public void setInsuranceCode(String insuranceCode) {
        this.insuranceCode = insuranceCode;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getUsernameClient() {
        return usernameClient;
    }

    public void setUsernameClient(String usernameClient) {
        this.usernameClient = usernameClient;
    }

    public String getUsernameRepairer() {
        return usernameRepairer;
    }

    public void setUsernameRepairer(String usernameRepairer) {
        this.usernameRepairer = usernameRepairer;
    }

    public CoverageType getCoverageType() {
        return coverageType;
    }

    public void setCoverageType(CoverageType coverageType) {
        this.coverageType = coverageType;
    }

    public static OccurrenceDTO from(Occurrence occurrence) {
        return new OccurrenceDTO(
                occurrence.getId(),
                occurrence.getEntryDate(),
                occurrence.getFinalDate(),
                occurrence.getObjectInsured(),
                occurrence.getDescription(),
                occurrence.getInsurance().getCode(),
                occurrence.getCoverageType(),
                occurrence.getState(),
                occurrence.getClient().getUsername(),
                occurrence.getRepairer() != null ? occurrence.getRepairer().getUsername() : null,
                occurrence.getInsurance().getInsuranceCompany()
        );
    }

    public static List<OccurrenceDTO> from(List<Occurrence> occurrences) {
        return occurrences.stream().map(OccurrenceDTO::from).collect(Collectors.toList());
    }
}
