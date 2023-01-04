package ipleiria.dae.project.dtos;

import ipleiria.dae.project.entities.Expert;

import javax.persistence.Id;
import java.util.List;
import java.util.stream.Collectors;

public class ExpertDTO {
    @Id
    String username;

    String password, name, email;

    String company_username;

    public ExpertDTO() {
    }

    public ExpertDTO(String username, String password, String name, String email, String company_username) {
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

//    public void setCompany_username(String company_username) { //penso que para fazer isto teriamos que criar mesmo uma InsuranceCompany, caso esta nao existisse
//        this.company_username = company_username;
//    }

    public static ExpertDTO from(Expert expert){
        return new ExpertDTO(
                expert.getUsername(),
                expert.getPassword(),
                expert.getName(),
                expert.getEmail(),
                expert.getCompany().getName()
        );
    }

    public static List<ExpertDTO> from(List<Expert> experts){
        return experts.stream().map(ExpertDTO::from).collect(Collectors.toList());
    }
}
