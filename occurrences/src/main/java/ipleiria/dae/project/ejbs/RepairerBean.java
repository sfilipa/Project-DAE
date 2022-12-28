package ipleiria.dae.project.ejbs;

import ipleiria.dae.project.entities.Company;
import ipleiria.dae.project.entities.Expert;
import ipleiria.dae.project.entities.Occurrence;
import ipleiria.dae.project.entities.Repairer;
import ipleiria.dae.project.enumerators.State;
import ipleiria.dae.project.security.Hasher;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class RepairerBean {
    @PersistenceContext
    EntityManager em;

    @Inject // import javax.inject.Inject;
    private Hasher hasher;

    public List<Repairer> getAllRepairers() {
        return (List<Repairer>) em.createNamedQuery("getAllRepairers").getResultList();
    }

    public Repairer create(String username, String password, String name, String email, String address) {
        Repairer repairer = find(username);
        if (repairer != null){
            return null;
        }
        repairer = new Repairer(username, hasher.hash(password), name, email, address);
        em.persist(repairer);
//        company.addRepairer(repairer);
        return find(username);
    }

    public Repairer update(String username, String password, String name, String email, String address) {
        Repairer repairer = find(username);
        if (repairer == null){
            return null;
        }
        repairer.setPassword(password);
        repairer.setName(name);
        repairer.setEmail(email);
        repairer.setAddress(address);
        return repairer;
    }

    public Repairer find(String username) {
        return em.find(Repairer.class, username);
    }

    //findOrFail

    public void delete(String username) {
//        Repairer repairer = find(username);
//        if (repairer == null){
//            return;
//        }
//        Company company = repairer.getCompany();
//        company.removeRepairer(repairer);
//        em.remove(repairer);
        em.remove(find(username));
    }

    public void startOccurrence(String username, long occurrenceCode) {
        try {
            // Find Repairer
            Repairer repairer = find(username);
            validateRepairer(repairer);

            // Find Occurrence
            Occurrence occurrence = em.find(Occurrence.class, occurrenceCode);
            validateOccurrence(repairer, occurrence, State.APPROVED);

            // Start Occurrence
            occurrence.setState(State.ACTIVE);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public void failOccurrence(String username, long occurrenceCode, String description) {
        try {
            // Find Repairer
            Repairer repairer = find(username);
            validateRepairer(repairer);

            // Find Occurrence
            Occurrence occurrence = em.find(Occurrence.class, occurrenceCode);
            validateOccurrence(repairer, occurrence, State.ACTIVE);

            // Disapprove Occurrence
            occurrence.setState(State.FAILED_BY_REPAIRER);

            // Get Occurrence Description
            String occurrenceDescription = occurrence.getDescription();

            // Build Occurrence Description
            String newOccurrenceDescription = occurrenceDescription + "\n- " + repairer.getUsername() + ": " + description;
            occurrence.setDescription(newOccurrenceDescription);

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public void finishOccurrence(String username, long occurrenceCode, String description) {
        try {
            // Find Repairer
            Repairer repairer = find(username);
            validateRepairer(repairer);

            // Find Occurrence
            Occurrence occurrence = em.find(Occurrence.class, occurrenceCode);
            validateOccurrence(repairer, occurrence, State.ACTIVE);

            // Approve Occurrence
            occurrence.setState(State.RESOLVED);

            // Get Occurrence Description
            String occurrenceDescription = occurrence.getDescription();

            // Build Occurrence Description
            String newOccurrenceDescription = occurrenceDescription + "\n- " + repairer.getUsername() + ": " + description;
            occurrence.setDescription(newOccurrenceDescription);

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private void validateRepairer(Repairer repairer) {
        if (repairer == null){
            throw new IllegalArgumentException("Repairer not found");
        }
    }

    private void validateOccurrenceExists(Occurrence occurrence) {
        if(occurrence == null) {
            throw new IllegalArgumentException("Occurrence not found");
        }
    }

    private void validateOccurrence(Repairer repairer, Occurrence occurrence, State state) {
        try {
            validateOccurrenceExists(occurrence);
            //validateRepairerIsAssignedToOccurrence(repairer, occurrence);
            validateOccurrenceState(occurrence, state);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private void validateRepairerIsAssignedToOccurrence(Repairer repairer, Occurrence occurrence) {
        // Check if Repairer is assigned to Occurrence
        if(occurrence.getRepairer() == null || !occurrence.getRepairer().getUsername().equals(repairer.getUsername())) {
            throw new IllegalArgumentException("Expert is not assigned to this occurrence");
        }
    }

    private void validateOccurrenceState(Occurrence occurrence, State state) {
        // Check if Occurrence is in the correct state
        if(occurrence.getState() != state) {
            throw new IllegalArgumentException("Occurrence is not in the correct state");
        }
    }
}
