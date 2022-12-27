package ipleiria.dae.project.dtos;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class PersonDTO implements Serializable {
    @Id
    String username;
    String password, name, email,address;
    Long phoneNumber,nif;
  //  List<String> insurances;

    public PersonDTO() {
       // insurances = new LinkedList<>();
    }

    public PersonDTO(String username, String password, String name, String email, String address, Long phoneNumber, Long nif) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.nif = nif;
       // insurances = new LinkedList<>();
    }

    public PersonDTO(String username, String password, String name, String email, String address, Long phoneNumber, Long nif, List<String> insurances) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.nif = nif;
      //  this.insurances = insurances;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(Long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Long getNif() {
        return nif;
    }

    public void setNif(Long nif) {
        this.nif = nif;
    }

  /*  public List<String> getInsurances() {
        return insurances;
    }

    public void setInsurances(List<String> insurances) {
        this.insurances = insurances;
    }*/
}
