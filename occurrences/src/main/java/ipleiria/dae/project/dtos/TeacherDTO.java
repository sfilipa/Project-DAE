package ipleiria.dae.project.dtos;

import javax.persistence.Id;

public class TeacherDTO {

    @Id
    String username;
    String password, name, email, office;

    public TeacherDTO() {
    }

    public TeacherDTO(String username, String password, String name, String email, String office) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.office = office;
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

    public String getOffice() {
        return office;
    }

    public void setOffice(String office) {
        this.office = office;
    }
}
