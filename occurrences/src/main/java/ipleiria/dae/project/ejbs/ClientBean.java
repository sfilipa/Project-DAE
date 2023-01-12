package ipleiria.dae.project.ejbs;

import ipleiria.dae.project.entities.Client;
import ipleiria.dae.project.entities.Insurance;
import ipleiria.dae.project.entities.Occurrence;
import ipleiria.dae.project.exceptions.*;
import ipleiria.dae.project.security.Hasher;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class ClientBean {

    @PersistenceContext
    private EntityManager em;

    @Inject
    private Hasher hasher;

    @EJB
    private OccurrenceBean occurrenceBean;

    public Client create(String username, String password, String name, String email, String address, long phoneNumber, long nif_nipc) {
        Client client = find(username);
        if (client != null) {
            throw new MyEntityExistsException("Client with username: " + username + " already exists");
        }
        //see if nif_nipc is valid
        if (!validateNif(nif_nipc)) {
            throw new ArgumentNotValidException("Invalid NIF format");
        }
        if (nif_nipcAlreadyUsed(nif_nipc)) {
            throw new MyEntityExistsException("Client with NIF - " + nif_nipc + " already exists");
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

    public Client update(String username, String name, String email, String address, long phoneNumber, long nif_nipc) {
        Client client = find(username);
        if (client == null) {
            throw new MyEntityNotFoundException("Client not found");
        }
        //see if the nif_nipc has already been used
        if (nif_nipc != client.getNif_nipc()) {
            //see if nif_nipc is valid
            if (!validateNif(nif_nipc)) {
                throw new ArgumentNotValidException("Invalid NIF format");
            }
            if (nif_nipcAlreadyUsed(nif_nipc)) {
                throw new MyEntityNotFoundException("Client with NIF - " + nif_nipc + " already exists");
            }
            client.setNif_nipc(nif_nipc);
        }
        em.lock(client, LockModeType.OPTIMISTIC);
        client.setName(name);
        client.setEmail(email);
        client.setAddress(address);
        client.setPhoneNumber(phoneNumber);
        return client;
    }

    public void delete(String username) {
        Client client = find(username);
        if (client == null) {
            throw new MyEntityNotFoundException("Client not found");
        }
        em.remove(client);
    }

    public Client find(String username) {
        return em.find(Client.class, username);
    }

    public Client findOrFail(String username) throws MyEntityNotFoundException {
        var client = em.getReference(Client.class, username);
        Hibernate.initialize(client);
        if (client == null) {
            throw new MyEntityNotFoundException("Client not found");
        }
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
        Client client = find(username);
        if(client == null){
            throw new MyEntityNotFoundException("Client not found");
        }
        Hibernate.initialize(client);

        var occurrences = client.getOccurrences();
        Hibernate.initialize(occurrences);

        return occurrences;
    }

    public List<Insurance> insurances(String username) {
        try {
            // Get client
            Client client = findOrFail(username);
            if (client == null) {
                throw new MyEntityNotFoundException("Client " + username + " not found");
            }

            // Get insurances
            return MockAPIBean.getInsurances(client.getNif_nipc());
        } catch (APIBadResponseException e) {
            throw new APIBadResponseException(e.getMessage());
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public void verifyOccurrenceBelongsToClient(String username, long id) throws MyEntityNotFoundException, NotAuthorizedException {
        Occurrence occurrence = occurrenceBean.find(id);
        if (occurrence == null) {
            throw new MyEntityNotFoundException("Occurrence not found");
        }

        Client client = find(username);
        if (client == null) {
            throw new MyEntityNotFoundException("Client not found");
        }

        if (!occurrence.getClient().getUsername().equals(username)) {
            throw new NotAuthorizedException("Occurrence does not belong to client");
        }
    }

    public void updatePassword(String username, String password, String oldPassword) {
        Client client = find(username);
        if (client == null) {
            throw new MyEntityNotFoundException("Client not found");
        }

        if(!hasher.hash(oldPassword).equals(client.getPassword())){
            throw new NotAuthorizedException("Old password is incorrect");
        }

        em.lock(client, LockModeType.OPTIMISTIC);
        client.setPassword(hasher.hash(password));
    }

    public List<Client> getAllClients() {
        try {
            return (List<Client>) em.createNamedQuery("getAllClients").getResultList();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("No clients found");
        }
    }
}
