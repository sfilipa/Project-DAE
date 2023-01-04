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

    public Client create(String username, String password, String name, String email, String address, long phoneNumber, long nif_nipc) throws MyEntityExistsException {
        Client client = find(username);
        if (client != null) {
            throw new MyEntityExistsException("Client with username: " + username + " already exists");
        }
        //see if nif_nipc is valid
        if (!validateNif(nif_nipc)) {
            throw new MyEntityExistsException("Client with nif_nipc: " + nif_nipc + " is not valid");//TODO: MUDAR ESTAS EXCEÇOES
        }
        //TODO:MAKE THIS VERIFICATION
        //see if the nif_nipc has already been used

        if (nif_nipcAlreadyUsed(nif_nipc)) {
            throw new MyEntityExistsException("Client with nif_nipc: " + nif_nipc + " already exists");
        }
        client = new Client(username, hasher.hash(password), name, email, address, phoneNumber, nif_nipc);
        em.persist(client);
        return client;
    }

    private boolean nif_nipcAlreadyUsed(long nif_nipc) {
        return em.createNamedQuery("clientNif", Client.class)
                .setParameter("nif_nipc", nif_nipc)
                .getResultList().size() > 0;
    }

    public Client update(String username, String password, String name, String email, String address, long phoneNumber, long nif_nipc) throws MyEntityNotFoundException {
        Client client = find(username);
        if (client == null) {
            throw new MyEntityNotFoundException("Client not found");
        }
        //see if nif_nipc is valid
        if (!validateNif(nif_nipc)) {
            throw new MyEntityNotFoundException("Client with nif_nipc: " + nif_nipc + " is not valid");//TODO: MUDAR ESTAS EXCEÇOES
        }
        //see if the nif_nipc has already been used
        if (nif_nipc != client.getNif_nipc()) {
            if (nif_nipcAlreadyUsed(nif_nipc)) {
                throw new MyEntityNotFoundException("Client with nif_nipc: " + nif_nipc + " already exists");//TODO: MUDAR ESTAS EXCEÇOES
            }
            client.setNif_nipc(nif_nipc);
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

    private boolean validateNif(long nif_nipc) {
        //see if nif_nipc has 9 digits
        if (nif_nipc < 100000000 || nif_nipc > 999999999) {
            return false;
        }
        return true;
    }

    public List<Occurrence> clientOccurrences(String username) {
        var occurrences = findOrFail(username).getOccurrences();
        Hibernate.initialize(occurrences);

        return occurrences;
    }
}
