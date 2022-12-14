package ipleiria.dae.project.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

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
    @JoinColumn(name = "company_nipc")
    Company company;

    public Expert() {
    }

    public Expert(String username, String password, String name, String email, Company company) {
        super(/*username,*/password, name, email);
        this.company = company;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
}
