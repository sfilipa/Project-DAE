package ipleiria.dae.project.entities;

import javax.persistence.Entity;
import java.io.Serializable;

@Entity
public class Expert extends User implements Serializable {

    public Expert() {
    }

    public Expert(String password, String name, String email) {
        super(password, name, email);
    }
}
