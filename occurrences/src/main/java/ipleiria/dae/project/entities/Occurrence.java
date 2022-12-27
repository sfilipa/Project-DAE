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
    @GeneratedValue
    private Date date;
    @NotNull
    private State state;
    @NotNull
    private InsuredAssetType insuredAssetType;
    @NotNull
    private String description;

    @ManyToOne
    @JoinColumn(name = "client_username")
    private Client client;

    @ManyToOne
    @JoinColumn(name = "insurance_code")
    private Insurance insurance;

    @OneToMany(mappedBy = "occurrence")
    List<Document> documents;

    @OneToMany(mappedBy = "occurrence", cascade = CascadeType.REMOVE)
    private List<Expert> experts;

    @OneToMany(mappedBy = "occurrence", cascade = CascadeType.REMOVE)
    private List<Repairer> repairers;

    public Occurrence() {
        experts = new LinkedList<>();
        repairers = new LinkedList<>();
        documents = new LinkedList<>();
    }

    //de inicio, todas as occurrences devem ser criadas com o State.PENDING
    public Occurrence(Client client, Date date, State state, InsuredAssetType insuredAssetType, Insurance insurance, String description) {
        this.client = client;
        this.date = date;
        this.state = state;
        this.insuredAssetType = insuredAssetType;
        this.insurance = insurance;
        this.experts = new LinkedList<>();
        this.repairers = new LinkedList<>();
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

    public void addExpert(Expert expert){
        if(expert != null){
            experts.add(expert);
        }
    }

    public boolean isExpertInOccurrence(Expert expert){
        return experts.contains(expert);
    }

    public boolean isRepairerInOccurrence(Repairer repairer) {
        return experts.contains(repairer);
    }

    public void removeExpert(Expert expert){
        if(expert != null){
            experts.remove(expert);
        }
    }

    public List<Repairer> getRepairers() {
        return repairers;
    }

    public void addRepairer(Repairer repairer){
        if(repairer != null){
            repairers.add(repairer);
        }
    }

    public void removeRepairer(Repairer repairer){
        if(repairer != null){
            repairers.remove(repairer);
        }
    }

    public void setRepairers(List<Repairer> repairers) {
        this.repairers = repairers;
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
