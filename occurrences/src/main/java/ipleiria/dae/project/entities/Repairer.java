package ipleiria.dae.project.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

@Entity
@NamedQueries({
        @NamedQuery(
                name = "getAllRepairers",
                query = "SELECT r FROM Repairer r ORDER BY r.name" // JPQL
        )
})
public class Repairer extends User implements Serializable {

//    @NotNull
//    @ManyToOne
//    @JoinColumn(name = "company_username")
//    Company company;

//    @ManyToOne
//    @JoinColumn(name = "expert_repairer")
//    Expert associatedExpert;

    @NotNull
    private String address;

    @OneToMany(mappedBy = "repairer", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Occurrence> occurrences;

    public Repairer() {
    }

    public Repairer(String username, String password, String name, String email, String address) {
        super(username,password, name, email);
        this.address = address;
        occurrences = new LinkedList<>();
    }

//    public Company getCompany() {
//        return company;
//    }
//
//    public void setCompany(Company company) {
//        this.company = company;
//    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void addOccurrence(Occurrence occurrence){
        if(occurrences.contains(occurrence)){
            return;
        }
        occurrences.add(occurrence);
    }

    public void removeOccurrence(Occurrence occurrence){
        if(!occurrences.contains(occurrence)){
            return;
        }
        occurrences.remove(occurrence);
    }

    public List<Occurrence> getOccurrences() {
        return occurrences;
    }

    public void setOccurrences(List<Occurrence> occurrences) {
        this.occurrences = occurrences;
    }

    //    public Expert getAssociatedExpert() {
//        return associatedExpert;
//    }
//
//    public void associateExpert(Expert expert) {
//        if (this.associatedExpert != null) {
//            throw new IllegalStateException("Repairer already has an associated expert");
//        }
//        this.associatedExpert = expert;
//    }
//
//    public void disassociateExpert() {
//        this.associatedExpert = null;
//    }
}
