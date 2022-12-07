package ipleiria.dae.project.entities;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@NamedQueries({
        @NamedQuery(
                name = "getAllClientsPrivate",
                query = "SELECT p FROM Private p ORDER BY p.name" // JPQL
        ),
})

@Entity
public class Private extends Client implements Serializable {
    @NotNull
    private long nif;

    public Private() {
    }

    public Private(String password, String name, String email, String address, long phoneNumber, long nif) {
        super(password, name, email, address, phoneNumber);
        this.nif = nif;
    }

    public long getNif() {
        return nif;
    }

    public void setNif(long nif) {
        this.nif = nif;
    }
}
