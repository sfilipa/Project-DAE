package ipleiria.dae.project.ejbs;

import ipleiria.dae.project.entities.Administrator;
import ipleiria.dae.project.entities.User;
import ipleiria.dae.project.exceptions.APIBadResponseException;
import ipleiria.dae.project.exceptions.MyEntityNotFoundException;
import ipleiria.dae.project.security.Hasher;
import org.hibernate.Hibernate;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class UserBean {
    @PersistenceContext
    private EntityManager em;
    @Inject
    private Hasher hasher;

    public User find(String username) {
        User user = em.find(User.class, username);
        if (user == null) {
            throw new MyEntityNotFoundException(username + ", User not found");
        }

        return user;
    }

    public User findOrFail(String username) {
        User user = em.getReference(User.class, username);
        Hibernate.initialize(user);
        if (user == null) {
            throw new MyEntityNotFoundException(username + ", User not found");
        }

        return user;
    }

    public boolean canLogin(String username, String password) throws MyEntityNotFoundException {
        User user = find(username);

        // if the received password is the same as the one stored in the database
        return user.getPassword().equals(hasher.hash(password));
    }

    public Administrator canAdminLogin(String username) throws MyEntityNotFoundException, APIBadResponseException {
        return MockAPIBean.getAdministrator(username);
    }
}
