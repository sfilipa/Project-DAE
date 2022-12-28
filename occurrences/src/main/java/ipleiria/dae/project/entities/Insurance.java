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
    Company company;
    String name;

    @OneToMany(mappedBy = "insurance", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Occurrence> occurrences;

    public Insurance() {
        occurrences = new LinkedList<>();
    }

    public Insurance( String code, Company company, String name) {
        this.code = code;
        this.company = company;
        this.name = name;
        occurrences = new LinkedList<>();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
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
