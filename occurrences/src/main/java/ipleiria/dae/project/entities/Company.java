package ipleiria.dae.project.entities;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@NamedQueries({
        @NamedQuery(
                name = "getAllClients",
                query = "SELECT c FROM Company c ORDER BY c.name" // JPQL
        ),
})

@Entity
public class Company extends Client implements Serializable {
    @NotNull
    private long nipc;

    public Company() {
    }

    public Company(String password, String name, String email, String address, long phoneNumber, long nipc) {
        super(password, name, email, address, phoneNumber);
        this.nipc = nipc;
    }

    public long getNipc() {
        return nipc;
    }

    public void setNipc(long nipc) {
        this.nipc = nipc;
    }
}
