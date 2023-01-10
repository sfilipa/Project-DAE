package ipleiria.dae.project.dtos.create;

import ipleiria.dae.project.dtos.ClientDTO;

import javax.validation.constraints.NotNull;

public class ClientCreateDTO extends ClientDTO {
    @NotNull
    private String password;

    public ClientCreateDTO() {
    }

    public ClientCreateDTO(String username, String name, String email, String address, Long phoneNumber, long nif_nipc, String password) {
        super(username, name, email, address, phoneNumber, nif_nipc);
        this.password = password;
    }

    public ClientCreateDTO(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
