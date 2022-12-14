package ipleiria.dae.project.dtos;

import ipleiria.dae.project.entities.Client;
import ipleiria.dae.project.enumerators.State;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

public class OccurrenceDTO implements Serializable {
    @Id
    private long id;
    private Client client;
    private Date date;
    private State state;
    private String insuredAssetType;

    public OccurrenceDTO() {}

    public OccurrenceDTO(long id, Client client, Date date, State state, String insuredAssetType) {
        this.id = id;
        this.client = client;
        this.date = date;
        this.state = state;
        this.insuredAssetType = insuredAssetType;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
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

    public String getInsuredAssetType() {
        return insuredAssetType;
    }

    public void setInsuredAssetType(String insuredAssetType) {
        this.insuredAssetType = insuredAssetType;
    }
}
