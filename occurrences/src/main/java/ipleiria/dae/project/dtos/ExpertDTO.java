package ipleiria.dae.project.dtos;

import ipleiria.dae.project.entities.Expert;

import javax.persistence.Id;
import java.util.List;
import java.util.stream.Collectors;

public class ExpertDTO {
    @Id
    String username;

    String name, email;

    String company_username;

    public ExpertDTO() {
    }

    public ExpertDTO(String username, String name, String email, String company_username) {
        this.username = username;
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
                expert.getName(),
                expert.getEmail(),
                expert.getInsuranceCompany()
        );
    }

    public static List<ExpertDTO> from(List<Expert> experts){
        return experts.stream().map(ExpertDTO::from).collect(Collectors.toList());
    }
}
