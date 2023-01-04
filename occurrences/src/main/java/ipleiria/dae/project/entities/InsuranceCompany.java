package ipleiria.dae.project.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

@NamedQueries({
        @NamedQuery(
                name = "getAllInsuranceCompanies",
                query = "SELECT i FROM InsuranceCompany i ORDER BY i.name" // JPQL
        ),
})
@Entity
public class InsuranceCompany extends User implements Serializable {

    @OneToMany(mappedBy = "insuranceCompany", cascade = CascadeType.REMOVE)
    List<Expert> experts;

    public InsuranceCompany() {
        experts = new LinkedList<>();
    }

    public InsuranceCompany(String username, String password, String name, String email) {
        super(username, password, name, email);
        experts = new LinkedList<>();
    }

    public InsuranceCompany(String username, String password, String name, String email, List<Expert> experts) {
        super(username, password, name, email);
        this.experts = new LinkedList<>(experts);
    }

    public void addExpert(Expert expert) {
        if (expert == null || experts.contains(expert)) {
            throw new IllegalArgumentException("Expert is null or already exists");
        }
        experts.add(expert);
    }

    public void removeExpert(Expert expert) {
        if (expert == null || !experts.contains(expert)) {
            throw new IllegalArgumentException("Expert is null or doesn't exist");
        }
        experts.remove(expert);
    }

    public List<Expert> getExperts() {
        return experts;
    }

    public void setExperts(List<Expert> experts) {
        this.experts = experts;
    }
}
