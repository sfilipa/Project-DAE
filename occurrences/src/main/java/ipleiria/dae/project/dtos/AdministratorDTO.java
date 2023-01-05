package ipleiria.dae.project.dtos;

import ipleiria.dae.project.entities.Administrator;

import javax.persistence.Id;
import java.util.List;
import java.util.stream.Collectors;

public class AdministratorDTO {
    @Id
    private String username;
    private String password, name, email;

    public static AdministratorDTO from(Administrator administrator) {
        return new AdministratorDTO(
                administrator.getUsername(),
                administrator.getPassword(),
                administrator.getName(),
                administrator.getEmail()
        );
    }

    public static List<AdministratorDTO> from(List<Administrator> administrators) {
        return administrators.stream().map(AdministratorDTO::from).collect(Collectors.toList());
    }

    public AdministratorDTO() {
    }

    public AdministratorDTO(String username, String password, String name, String email) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
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
}
