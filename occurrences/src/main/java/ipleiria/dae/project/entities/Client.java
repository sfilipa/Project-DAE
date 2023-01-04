package ipleiria.dae.project.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

@Entity
@NamedQueries({
        @NamedQuery(
                name = "clientNif",
                query = "SELECT c FROM Client c WHERE c.nif_nipc=:nif_nipc"
        )
})
public class Client extends User implements Serializable {

    @NotNull
    private String address;
    @NotNull
    private long phoneNumber;
    @NotNull
    private long nif_nipc;

    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Occurrence> occurrences;

    @Transient
    private List<Policy> policies;

   /* @NotNull
    List<String> insurances;*/

    public Client() {
        //insurances = new LinkedList<>();
        occurrences = new LinkedList<>();
    }

    public Client(String username, String password, String name, String email, String address, long phoneNumber, long nif_nipc) {
        super(username, password, name, email);
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.nif_nipc = nif_nipc;
        occurrences = new LinkedList<>();
        //insurances = new LinkedList<>();
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

    public List<Occurrence> getOccurrences() {
        return occurrences;
    }

    public void setOccurrences(List<Occurrence> occurrences) {
        this.occurrences = occurrences;
    }

    public long getNif_nipc() {
        return nif_nipc;
    }

    public void setNif_nipc(long nif_nipc) {
        this.nif_nipc = nif_nipc;
    }

    public void add(Policy policy) {
        if (policy == null || policies.contains(policy)) {
            // Throw exception
            throw new IllegalArgumentException("Policy is null or already exists");
        }
        policies.add(policy);
    }

    public void remove(Policy policy) {
        if (policy == null || !policies.contains(policy)) {
            // Throw exception
            throw new IllegalArgumentException("Policy is null or doesn't exist");
        }
        policies.remove(policy);
    }
}
