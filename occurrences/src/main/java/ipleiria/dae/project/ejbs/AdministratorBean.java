package ipleiria.dae.project.ejbs;

import ipleiria.dae.project.entities.Administrator;
import ipleiria.dae.project.security.Hasher;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class AdministratorBean {
    @PersistenceContext
    EntityManager em;

    @Inject // import javax.inject.Inject;
    private Hasher hasher;

    public void create(String username, String password, String name, String email) {
        Administrator administrator = new Administrator(username, password, name, email);
        em.persist(administrator);
    }
}
