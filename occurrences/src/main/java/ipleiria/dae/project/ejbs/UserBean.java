package ipleiria.dae.project.ejbs;

import ipleiria.dae.project.entities.Administrator;
import ipleiria.dae.project.entities.User;
import ipleiria.dae.project.exceptions.APIBadResponseException;
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
        return em.find(User.class, username);
    }

    public User findOrFail(String username) {
        User user = em.getReference(User.class, username);
        Hibernate.initialize(user);
        return user;
    }

    public boolean canLogin(String username, String password) {
        User user = find(username);
        return user != null && user.getPassword().equals(hasher.hash(password));
    }

    public Administrator canAdminLogin(String username) throws APIBadResponseException {
        try {
            return MockAPIBean.getAdministrator(username);
        } catch (APIBadResponseException e) {
            throw new APIBadResponseException(e.getMessage());
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
