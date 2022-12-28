package ipleiria.dae.project.entities;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

@NamedQueries({
        @NamedQuery(
                name = "getAllInsuranceCompanies",
                query = "SELECT c FROM InsuranceCompany c ORDER BY c.name" // JPQL
        ),
})
@Entity
public class InsuranceCompany {
    @Id
    private String name;

    @OneToMany(mappedBy = "insuranceCompany", cascade = CascadeType.REMOVE)
    List<Expert> experts;

    public InsuranceCompany() {
        experts = new LinkedList<>();
    }

    public InsuranceCompany(String name) {
        this.name = name;
        experts = new LinkedList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public List<Expert> getExperts() {
        return experts;
    }

    public void setExperts(List<Expert> experts) {
        this.experts = experts;
    }
}
