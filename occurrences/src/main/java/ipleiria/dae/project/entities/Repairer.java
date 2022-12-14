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

    public Repairer() {
    }

    public Repairer(String username, String password, String name, String email, Company company) {
        super(username,password, name, email);
        this.company = company;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
}
