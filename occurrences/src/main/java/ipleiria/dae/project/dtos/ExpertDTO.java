package ipleiria.dae.project.dtos;

import ipleiria.dae.project.entities.Company;

import javax.persistence.Id;

public class ExpertDTO {
    @Id
    String username;

    String password, name, email;

    long company_nipc;

    public ExpertDTO() {
    }

    public ExpertDTO(String username, String password, String name, String email, long company_nipc) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.company_nipc = company_nipc;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getCompany_nipc() {
        return company_nipc;
    }

    public void setCompany_nipc(long company_nipc) {
        this.company_nipc = company_nipc;
    }
}
