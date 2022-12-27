package ipleiria.dae.project.ejbs;

import ipleiria.dae.project.entities.Client;
import ipleiria.dae.project.security.Hasher;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class ClientBean {

    @PersistenceContext
    private EntityManager em;

    @EJB
    private ClientBean clientBean;

    @Inject
    private Hasher hasher;

    public void create(String username, String password, String name, String email, String address, long phoneNumber) {
        var client = new Client(username, hasher.hash(password), name, email, address, phoneNumber);
        em.persist(client);
    }
}
