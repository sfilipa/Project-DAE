package ipleiria.dae.project.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Insurance implements Serializable {
    //TODO: Change company to insurance Company

    String code;
    Company company;
    String name;


    public Insurance() {

    }

    public Insurance( String code, Company company, String name) {
        this.code = code;
        this.company = company;
        this.name = name;
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

}
