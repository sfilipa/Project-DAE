package ipleiria.dae.project.dtos.create;

import javax.validation.constraints.NotNull;

public class UpdatePasswordDTO {
    @NotNull
    private String password;

    @NotNull
    private String oldPassword;

    public UpdatePasswordDTO() {
    }

    public UpdatePasswordDTO(String password, String oldPassword) {
        this.password = password;
        this.oldPassword = oldPassword;
    }

    public String getPassword() {
        return password;
    }

    public String getOldPassword() {
        return oldPassword;
    }
}
