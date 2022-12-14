package ipleiria.dae.project.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

@NamedQueries({
        @NamedQuery(
                name = "getAllCompanies",
                query = "SELECT c FROM Company c ORDER BY c.name" // JPQL
        ),
})

@Entity
public class Company extends Client implements Serializable {

    @OneToMany(mappedBy = "company", cascade = CascadeType.REMOVE)
    List<Expert> experts;

    @NotNull
    long nipc;

    public Company() {
        experts = new LinkedList<>();
    }

    public Company(String username, String password, String name, String email, String address, long phoneNumber, long nipc) {
        super(username, password, name, email, address, phoneNumber);
        this.nipc = nipc;
    }

    public void add(Expert expert) {
        if (expert == null || experts.contains(expert)) {
            return;
        }
        experts.add(expert);
    }

    public void remove(Expert expert) {
        if (expert == null || !experts.contains(expert)) {
            return;
        }
        experts.remove(expert);
    }

    public long getNipc() {
        return nipc;
    }

    public void setNipc(long nipc) {
        this.nipc = nipc;
    }

    public List<Expert> getExperts() {
        return experts;
    }

    public void setExperts(List<Expert> experts) {
        this.experts = experts;
    }
}
