package ipleiria.dae.project.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

@Entity
@NamedQueries({
        @NamedQuery(
                name = "getAllExperts",
                query = "SELECT e FROM Expert e ORDER BY e.name" // JPQL
        ),
})
public class Expert extends User implements Serializable {

    @NotNull
    @ManyToOne
    @JoinColumn(name = "company_username")
    Company company;

    @OneToMany
    @JoinColumn(name = "expert_repairer")
    List<Repairer> associatedRepairers;

    public Expert() {
    }

    public Expert(String username, String password, String name, String email, Company company) {
        super(username,password, name, email);
        this.company = company;
        associatedRepairers = new LinkedList<>();
    }


    public Expert(String username, String password, String name, String email, Company company, List<Repairer> associatedRepairers) {
        super(username,password, name, email);
        this.company = company;
        this.associatedRepairers = new LinkedList<>(associatedRepairers);
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public void addRepairer(Repairer repairer) {
        if (this.associatedRepairers.contains(repairer)) {
            throw new IllegalStateException("Repairer already associated to this expert");
        }
        this.associatedRepairers.add(repairer);
    }

    public void removeRepairer(Repairer repairer) {
        if (!this.associatedRepairers.contains(repairer)) {
            throw new IllegalStateException("Repairer not associated to this expert");
        }
        this.associatedRepairers.remove(repairer);
    }
}
