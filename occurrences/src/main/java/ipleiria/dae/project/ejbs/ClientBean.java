package ipleiria.dae.project.ejbs;

import ipleiria.dae.project.entities.Client;
import ipleiria.dae.project.entities.Occurrence;
import ipleiria.dae.project.exceptions.MyConstraintViolationException;
import ipleiria.dae.project.exceptions.MyEntityExistsException;
import ipleiria.dae.project.exceptions.MyEntityNotFoundException;
import ipleiria.dae.project.security.Hasher;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Objects;

@Stateless
public class ClientBean {

    @PersistenceContext
    private EntityManager em;

    @Inject
    private Hasher hasher;

    public Client create(String username, String password, String name, String email, String address, long phoneNumber) throws MyEntityExistsException {
        Client client = find(username);
        if (client != null) {
            throw new MyEntityExistsException("Client with username: " + username + " already exists");
        }
        client = new Client(username, hasher.hash(password), name, email, address, phoneNumber);
        em.persist(client);
        return client;
    }

    public Client update(String username, String password, String name, String email, String address, long phoneNumber) throws MyEntityNotFoundException {
        Client client = find(username);
        if (client == null) {
            throw new MyEntityNotFoundException("Client not found");
        }
        em.lock(client, LockModeType.OPTIMISTIC);
        client.setPassword(password);
        client.setName(name);
        client.setEmail(email);
        client.setAddress(address);
        client.setPhoneNumber(phoneNumber);
        return client;
    }

    public void delete(String username) throws MyEntityNotFoundException {
        Client client = find(username);
        if (client == null) {
            throw new MyEntityNotFoundException("Client not found");
        }
        em.remove(client);
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
