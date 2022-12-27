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
    protected String username;

    @Email
    @NotNull
    protected String email;

    @NotNull
    protected String password;

    @NotNull
    protected String name;

    @Version
    protected int version;

    public User() {
    }

    public User(String username, String password, String name, String email) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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