package ipleiria.dae.project.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@NamedQueries({
        @NamedQuery(
                name = "getAllRepairers",
                query = "SELECT r FROM Repairer r ORDER BY r.name" // JPQL
        )
})
public class Repairer extends User implements Serializable {

    @NotNull
    @ManyToOne
    @JoinColumn(name = "company_username")
    Company company;

    @ManyToOne
    @JoinColumn(name = "expert_repairer")
    Expert associatedExpert;

    public Repairer() {
    }

    public Repairer(String username, String password, String name, String email, Company company, Expert associatedExpert) {
        super(username,password, name, email);
        this.company = company;
        this.associatedExpert = associatedExpert;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Expert getAssociatedExpert() {
        return associatedExpert;
    }

    public void associateExpert(Expert expert) {
        if (this.associatedExpert != null) {
            throw new IllegalStateException("Repairer already has an associated expert");
        }
        this.associatedExpert = expert;
    }

    public void disassociateExpert() {
        this.associatedExpert = null;
    }
}
