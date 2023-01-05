package ipleiria.dae.project.dtos;

import javax.validation.constraints.NotNull;

public class ExpertCreateDTO extends ExpertDTO{
    @NotNull
    private String password;

    public ExpertCreateDTO() {
    }

    public ExpertCreateDTO(String username, String name, String email, String company_username, String password) {
        super(username, name, email, company_username);
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
