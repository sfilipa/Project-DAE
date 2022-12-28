package ipleiria.dae.project.dtos;

import ipleiria.dae.project.entities.Client;
import ipleiria.dae.project.entities.Insurance;
import ipleiria.dae.project.entities.Occurrence;
import ipleiria.dae.project.enumerators.InsuredAssetType;
import ipleiria.dae.project.enumerators.State;

import javax.persistence.Id;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class OccurrenceDTO implements Serializable {
    @Id
    private long id;
    private String usernameClient;
    private String date;
    private State state;
    private InsuredAssetType insuredAssetType;
    private String insuranceCode;
    private String description;
    private String insurancename;
    private String object;


    public OccurrenceDTO() {}


    public OccurrenceDTO(long id, String usernameClient, String date, State state, InsuredAssetType insuredAssetType, String insuranceCode, String insurancename, String description, String object) {
        this.id = id;
        this.usernameClient = usernameClient;
        this.date = date;
        this.state = state;
        this.insuredAssetType = insuredAssetType;
        this.insuranceCode = insuranceCode;
        this.insurancename = insurancename;
        this.description = description;
        this.object = object;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsernameClient() {
        return usernameClient;
    }

    public void setUsernameClient(String usernameClient) {
        this.usernameClient = usernameClient;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
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

    public String getInsuranceCode() {
        return insuranceCode;
    }

    public void setInsuranceCode(String insuranceCode) {
        this.insuranceCode = insuranceCode;
    }

    public String getInsurancename() {
        return insurancename;
    }

    public void setInsurancename(String insurancename) {
        this.insurancename = insurancename;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public static OccurrenceDTO from(Occurrence occurrence) {
        return new OccurrenceDTO(
                occurrence.getId(),
                occurrence.getClient().getUsername(),
                occurrence.getDate(),
                occurrence.getState(),
                occurrence.getInsuredAssetType(),
                occurrence.getInsurance().getCode(),
                occurrence.getInsurance().getName(),
                occurrence.getDescription(),
                occurrence.getObject()
        );
    }

    public static List<OccurrenceDTO> from(List<Occurrence> occurrences) {
        return occurrences.stream().map(OccurrenceDTO::from).collect(Collectors.toList());
    }
}
