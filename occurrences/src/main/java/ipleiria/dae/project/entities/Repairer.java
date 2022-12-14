package ipleiria.dae.project.entities;

import javax.persistence.Entity;
import java.io.Serializable;

@Entity
public class Repairer extends User implements Serializable {

    public Repairer() {
    }

    public Repairer(String username, String password, String name, String email) {
        super(username, password, name, email);
    }
}
