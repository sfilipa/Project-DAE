package ipleiria.dae.project.dtos;

import javax.persistence.Id;

public class CompanyDTO {

    @Id
    long nipc;

    String password, name, email;

    public CompanyDTO() {
    }

    public CompanyDTO(long nipc, String password, String name, String email) {
        this.nipc = nipc;
        this.password = password;
        this.name = name;
        this.email = email;
    }

    public long getNipc() {
        return nipc;
    }

    public void setNipc(long nipc) {
        this.nipc = nipc;
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
}
