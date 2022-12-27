package ipleiria.dae.project.dtos;

import org.hibernate.Hibernate;
import ipleiria.dae.project.entities.User;

import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class UserDTO implements Serializable {
    @Id
    private String username;
    @NotNull
    private String name;
    @NotNull
    private String email;

    public UserDTO() {}
    public UserDTO(String username, String name, String email, String role) {
        this.username = username;
        this.name = name;
        this.email = email;
    }

    public static UserDTO from(User user) {
        return new UserDTO(
                user.getUsername(),
                user.getName(),
                user.getEmail(),
                Hibernate.getClass(user).getSimpleName()
        );
    }
    public static List<UserDTO> from(List<User> users) {
        return users.stream().map(UserDTO::from).collect(Collectors.toList());
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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