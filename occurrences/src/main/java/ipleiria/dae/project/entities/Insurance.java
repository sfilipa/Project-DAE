package ipleiria.dae.project.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

@Table(name = "insurances")
@Entity
public class Insurance implements Serializable {
    //TODO: Change when MOCKAPI is ready
    @Id
    String code;
    InsuranceCompany insuranceCompany;
    String name;

    @OneToMany(mappedBy = "insurance", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Occurrence> occurrences;

    public Insurance() {
        occurrences = new LinkedList<>();
    }

    public Insurance( String code, InsuranceCompany insuranceCompany, String name) {
        this.code = code;
        this.insuranceCompany = insuranceCompany;
        this.name = name;
        occurrences = new LinkedList<>();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public InsuranceCompany getCompany() {
        return insuranceCompany;
    }

    public void setCompany(InsuranceCompany insuranceCompany) {
        this.insuranceCompany = insuranceCompany;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Occurrence> getOccurrences() {
        return occurrences;
    }

    public void setOccurrences(List<Occurrence> occurrences) {
        this.occurrences = occurrences;
    }
}
