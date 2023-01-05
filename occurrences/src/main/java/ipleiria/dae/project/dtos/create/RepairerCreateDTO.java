package ipleiria.dae.project.dtos.create;

import ipleiria.dae.project.dtos.RepairerDTO;

import javax.validation.constraints.NotNull;

public class RepairerCreateDTO extends RepairerDTO {
    @NotNull
    private String password;

    public RepairerCreateDTO() {
    }

    public RepairerCreateDTO(String username, String name, String email, String address, String password) {
        super(username, name, email, address);
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
