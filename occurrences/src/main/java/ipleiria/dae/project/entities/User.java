package ipleiria.dae.project.entities;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Table(name = "users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)

@Entity
public class User extends Versionable implements Serializable {
    @Id
    @Email
    @NotNull
    private String email;
    @NotNull
    private String password;
    @NotNull
    private String name;
    @Version
    private int version;

    public User() {
    }

    public User(String password, String name, String email) {
        this.password = password;
        this.name = name;
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
}