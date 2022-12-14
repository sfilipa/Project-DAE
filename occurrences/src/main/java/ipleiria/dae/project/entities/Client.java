package ipleiria.dae.project.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

@Entity
public class Client extends User implements Serializable {

    @NotNull
    private String address;
    @NotNull
    private long phoneNumber;
    @NotNull
    List<String> insurances;

    public Client() {
        insurances = new LinkedList<>();
    }

    public Client(String password, String name, String email, String address, long phoneNumber) {
        super(password, name, email);
        this.address = address;
        this.phoneNumber = phoneNumber;
        insurances = new LinkedList<>();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
