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

    @OneToMany(mappedBy = "company", cascade = CascadeType.REMOVE)
    List<Repairer> repairers;

    @NotNull
    long nipc;

    public Company() {
        experts = new LinkedList<>();
        repairers = new LinkedList<>();
    }

    public Company(String password, String name, String email, String address, long phoneNumber, long nipc) {
        super(password, name, email, address, phoneNumber);
        this.nipc = nipc;
    }

    public void addExpert(Expert expert) {
        if (expert == null || experts.contains(expert)) {
            return;
        }
        experts.add(expert);
    }

    public void removeExpert(Expert expert) {
        if (expert == null || !experts.contains(expert)) {
            return;
        }
        experts.remove(expert);
    }

    public void removeRepairer(Repairer repairer) {
        if (repairer == null || !repairers.contains(repairer)) {
            return;
        }
        experts.remove(repairer);
    }

    public void addRepairer(Repairer repairer) {
        if (repairer == null || repairers.contains(repairer)) {
            return;
        }
        repairers.add(repairer);
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
