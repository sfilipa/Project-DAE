package ipleiria.dae.project.ejbs;

import ipleiria.dae.project.entities.Client;
import ipleiria.dae.project.entities.Occurrence;
import ipleiria.dae.project.security.Hasher;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class ClientBean {

    @PersistenceContext
    private EntityManager em;

    @Inject
    private Hasher hasher;

    public void create(String username, String password, String name, String email, String address, long phoneNumber) {
        var client = new Client(username, hasher.hash(password), name, email, address, phoneNumber);
        em.persist(client);
    }
    public Client find(String username) {
        return em.find(Client.class, username);
    }

    public Client findOrFail(String username) {
        var client = em.getReference(Client.class, username);
        Hibernate.initialize(client);

        return client;
    }

    public List<Occurrence> clientOccurrences(String username) {
        var occurrences = findOrFail(username).getOccurrences();
        Hibernate.initialize(occurrences);

        return occurrences;
    }
}
