package ipleiria.dae.project.dtos;

import ipleiria.dae.project.entities.Client;
import ipleiria.dae.project.entities.Insurance;
import ipleiria.dae.project.enumerators.InsuredAssetType;
import ipleiria.dae.project.enumerators.State;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

public class OccurrenceDTO implements Serializable {
    @Id
    private long id;
    private String usernameClient;
    private Date date;
    private State state;
    private InsuredAssetType insuredAssetType;
    private Insurance insurance;
    private String description;

    public OccurrenceDTO() {}

    public OccurrenceDTO(long id, String usernameClient, Date date, State state, InsuredAssetType insuredAssetType, Insurance insurance, String description) {
        this.id = id;
        this.usernameClient = usernameClient;
        this.date = date;
        this.state = state;
        this.insuredAssetType = insuredAssetType;
        this.insurance = insurance;
        this.description = description;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
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

    public Insurance getInsurance() {
        return insurance;
    }

    public void setInsurance(Insurance insurance) {
        this.insurance = insurance;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}
