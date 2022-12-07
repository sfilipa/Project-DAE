package ipleiria.dae.project.entities;

import javax.persistence.Entity;
import java.io.Serializable;

@Entity
public class Administrator extends User implements Serializable {
    public Administrator() {
    }

    public Administrator(String password, String name, String email) {
        super(password, name, email);
    }
}
