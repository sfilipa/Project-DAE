package ipleiria.dae.project.entities;

import java.io.Serializable;

public class Insurance implements Serializable {
    Company company;
    String name;

    public Insurance() {
    }

    public Insurance(Company company, String name) {
        this.company = company;
        this.name = name;
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
}
