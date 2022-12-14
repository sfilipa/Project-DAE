package ipleiria.dae.project.entities;

import ipleiria.dae.project.enumerators.InsuredAssetType;
import ipleiria.dae.project.enumerators.State;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Table(name = "occurrences")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Entity
@NamedQueries({
        @NamedQuery(
                name = "getAllOccurrences",
                query = "SELECT o FROM Occurrence o ORDER BY o.id" // JPQL
        )
})
public class Occurrence implements Serializable {
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotNull
    private Client client;
    @NotNull
    @GeneratedValue
    private Date date;
    @NotNull
    private State state;
    @NotNull
    private InsuredAssetType insuredAssetType;
    @NotNull
    private Insurance insurance;
    @NotNull
    private String description;

    @OneToMany(mappedBy = "document", cascade = CascadeType.REMOVE)
    List<Document> documents;

    private List<Expert> experts;

    public Occurrence() {
        experts = new LinkedList<>();
        documents = new LinkedList<>();
    }

    public Occurrence(Client client, Date date, State state, InsuredAssetType insuredAssetType, Insurance insurance, String description) {
        this.client = client;
        this.date = date;
        this.state = state;
        this.insuredAssetType = insuredAssetType;
        this.insurance = insurance;
        this.experts = new LinkedList<>();
        this.documents = new LinkedList<>();
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public Insurance getInsurance() {
        return insurance;
    }

    public void setInsurance(Insurance insurance) {
        this.insurance = insurance;
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

    public InsuredAssetType getInsuredAssetType() {
        return insuredAssetType;
    }

    public void setInsuredAssetType(InsuredAssetType insuredAssetType) {
        this.insuredAssetType = insuredAssetType;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    public List<Expert> getExperts() {
        return experts;
    }

    public void setExperts(List<Expert> experts) {
        this.experts = experts;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
