package ipleiria.dae.project.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

@NamedQueries({
        @NamedQuery(
                name = "getAllCompanies",
                query = "SELECT c FROM Company c ORDER BY c.name" // JPQL
        ),
})
@Entity
public class Company extends Client implements Serializable {
    @NotNull
    long nipc;

    public Company() {

    }

    public Company(String username, String password, String name, String email, String address, long phoneNumber, long nipc) {
        super(username, password, name, email, address, phoneNumber);
        this.nipc = nipc;
    }

    public long getNipc() {
        return nipc;
    }

    public void setNipc(long nipc) {
        this.nipc = nipc;
    }

}
