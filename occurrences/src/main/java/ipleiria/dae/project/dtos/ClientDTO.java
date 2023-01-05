package ipleiria.dae.project.dtos;

import ipleiria.dae.project.entities.Client;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class ClientDTO implements Serializable {
    @Id
    String username;
    String name, email,address;
    Long phoneNumber;
    long nif_nipc;
   // List<String> insurances;

    public ClientDTO() {
       // insurances = new LinkedList<>();
    }

    public ClientDTO(String username, String name, String email, String address, Long phoneNumber, long nif_nipc) {
        this.username = username;
        this.name = name;
        this.email = email;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.nif_nipc = nif_nipc;
       // insurances = new LinkedList<>();
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

    public Long getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(Long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public long getNif_nipc() {
        return nif_nipc;
    }

    public void setNif_nipc(long nif_nipc) {
        this.nif_nipc = nif_nipc;
    }

    public static ClientDTO from(Client client) {
        return new ClientDTO(
                client.getUsername(),
                client.getName(),
                client.getEmail(),
                client.getAddress(),
                client.getPhoneNumber(),
                client.getNif_nipc()
        );
    }

    public static List<ClientDTO> from(List<Client> students) {
        return students.stream().map(ClientDTO::from).collect(Collectors.toList());
    }


    /*public List<String> getInsurances() {
        return insurances;
    }

    public void setInsurances(List<String> insurances) {
        this.insurances = insurances;
    }*/
}
