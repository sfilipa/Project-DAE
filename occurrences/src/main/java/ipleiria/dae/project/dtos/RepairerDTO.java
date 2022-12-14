package ipleiria.dae.project.dtos;

import javax.persistence.Id;

public class RepairerDTO {
    @Id
    String username;

    String password, name, email;

    String company_username;

    public RepairerDTO() {
    }

    public RepairerDTO(String username, String password, String name, String email, String company_username) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.company_username = company_username;
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

    public String getCompany_username() {
        return company_username;
    }

    public void setCompany_username(String company_username) {
        this.company_username = company_username;
    }
}
