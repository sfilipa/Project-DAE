package ipleiria.dae.project.dtos;

import ipleiria.dae.project.entities.Repairer;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class RepairerDTO implements Serializable {
    String username;

    String name, email;

    String address;

    public RepairerDTO() {
    }

    public RepairerDTO(String username, String name, String email, String address) {
        this.username = username;
        this.name = name;
        this.email = email;
        this.address = address;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public static RepairerDTO from(Repairer repairer) {
        return new RepairerDTO(
                repairer.getUsername(),
                repairer.getName(),
                repairer.getEmail(),
                repairer.getAddress());
    }

    public static List<RepairerDTO> from(List<Repairer> repairers){
        return repairers.stream().map(RepairerDTO::from).collect(Collectors.toList());
    }
}
