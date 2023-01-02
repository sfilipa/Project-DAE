package ipleiria.dae.project.entities;

import ipleiria.dae.project.enumerators.InsuredAssetType;
import ipleiria.dae.project.enumerators.State;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Table(name = "policies")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Entity
@NamedQueries({
        @NamedQuery(
                name = "getAllPolicies",
                query = "SELECT p FROM Policy p ORDER BY p.id" // JPQL
        )
})
public class Policy {
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String code;
    @NotNull
    @GeneratedValue
    private String type;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "client_username")
    private Client client;
    @NotNull
    private List<InsuredAssetType> covers;

    public Policy() {
    }

    public Policy(String code, String type, Client client) {
        this.code = code;
        this.type = type;
        this.client = client;
        covers = new LinkedList<>();
    }

    public Policy(String code, String type, Client client, List<InsuredAssetType> covers) {
        this.code = code;
        this.type = type;
        this.client = client;
        this.covers = covers;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public List<InsuredAssetType> getCovers() {
        return new LinkedList<>(covers);
    }

    public void setCovers(List<InsuredAssetType> covers) {
        this.covers = covers;
    }
}
