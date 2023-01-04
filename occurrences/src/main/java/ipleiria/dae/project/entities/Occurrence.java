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
    private String entryDate;
    private String finalDate;
    @NotNull
    private String objectInsured;
    @NotNull
    private String description;
    @NotNull
    private Insurance insurance;
    @NotNull
    private State state;
    @ManyToOne
    @JoinColumn(name = "client_username")
    private Client client;
    @ManyToOne
    @JoinColumn(name = "repairer_name")
    private Repairer repairer;
    @OneToMany(mappedBy = "occurrence", fetch = FetchType.EAGER)
    private List<Document> documents;
    @ManyToMany(mappedBy = "occurrences", fetch = FetchType.LAZY)
    private List<Expert> experts;

    public Occurrence() {
        documents = new LinkedList<>();
        experts = new LinkedList<>();
    }

    //de inicio, todas as occurrences devem ser criadas com o State.PENDING


    public Occurrence(String entryDate, String objectInsured, String description, Insurance insurance, State state, Client client) {
        this.entryDate = entryDate;
        this.objectInsured = objectInsured;
        this.description = description;
        this.insurance = insurance;
        this.state = state;
        this.client = client;
        documents = new LinkedList<>();
        experts = new LinkedList<>();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getObjectInsured() {
        return objectInsured;
    }

    public void setObjectInsured(String objectInsured) {
        this.objectInsured = objectInsured;
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

    public void addExpert(Expert expert){
        if(expert != null){
            experts.add(expert);
        }
    }

    public void removeExpert(Expert expert){
        if(expert != null){
            experts.remove(expert);
        }
    }

    public boolean isExpertInOccurrence(Expert expert){
        return experts.contains(expert);
    }

    public Repairer getRepairer() {
        return repairer;
    }

    public void setRepairer(Repairer repairer) {
        this.repairer = repairer;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void addDocument(Document document) {
        if (! this.documents.contains(document)) {
            this.documents.add(document);
        }
    }

    public void removeDocument(Document document) {
        this.documents.remove(document);
    }

}
