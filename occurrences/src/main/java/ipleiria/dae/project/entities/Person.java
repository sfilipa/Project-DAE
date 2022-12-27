package ipleiria.dae.project.entities;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@NamedQueries({
        @NamedQuery(
                name = "getAllClientsPerson",
                query = "SELECT p FROM Person p ORDER BY p.name" // JPQL
        ),
})

@Entity
public class Person extends Client implements Serializable {
    @NotNull
    private long nif;

    public Person() {
    }

    public Person(String username, String password, String name, String email, String address, long phoneNumber, long nif) {
        super(username, password, name, email, address, phoneNumber);
        this.nif = nif;
    }

    public long getNif() {
        return nif;
    }

    public void setNif(long nif) {
        this.nif = nif;
    }
}
