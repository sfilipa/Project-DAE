package ipleiria.dae.project.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Insurance implements Serializable {
    //TODO: Change company to insurance Company

    String code;
    String insuranceCompany;
    String name;


    public Insurance() {

    }

    public Insurance( String code, String insuranceCompany, String name) {
        this.code = code;
        this.insuranceCompany = insuranceCompany;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCompany() {
        return insuranceCompany;
    }

    public void setCompany(String insuranceCompany) {
        this.insuranceCompany = insuranceCompany;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}