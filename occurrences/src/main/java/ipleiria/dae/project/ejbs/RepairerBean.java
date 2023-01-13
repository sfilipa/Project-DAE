package ipleiria.dae.project.ejbs;

import ipleiria.dae.project.entities.Expert;
import ipleiria.dae.project.entities.Occurrence;
import ipleiria.dae.project.entities.Repairer;
import ipleiria.dae.project.enumerators.State;
import ipleiria.dae.project.exceptions.APIBadResponseException;
import ipleiria.dae.project.exceptions.MyEntityExistsException;
import ipleiria.dae.project.exceptions.MyEntityNotFoundException;
import ipleiria.dae.project.exceptions.NotAuthorizedException;
import ipleiria.dae.project.security.Hasher;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Stateless
public class RepairerBean {
    @PersistenceContext
    EntityManager em;
    @Inject // import javax.inject.Inject;
    private Hasher hasher;
    @EJB
    private MockAPIBean mockAPIBean;
    @EJB
    private EmailBean emailBean;
    @EJB
    private ExpertBean expertBean;
    @EJB
    private ZipFilesBean zipFilesBean;

    public List<Repairer> getAllRepairers() {
        return (List<Repairer>) em.createNamedQuery("getAllRepairers").getResultList();
    }

    public Repairer create(String username, String password, String name, String email, String address) throws MyEntityExistsException {
        try {
            // Find if the repairer already exists
            Repairer repairer = find(username);
            if (repairer != null) {
                throw new MyEntityExistsException("Repairer " + repairer.getUsername() + " already exists");
            }

            // Create the repairer
            repairer = new Repairer(username, hasher.hash(password), name, email, address);
            em.persist(repairer);
            return find(username);
        } catch (MyEntityExistsException e) {
            throw new MyEntityExistsException(e.getMessage());
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public Repairer update(String username, String name, String email, String address) throws MyEntityNotFoundException {
        try {
            // Find the repairer
            Repairer repairer = find(username);

            validateRepairer(repairer);
            // Update the repairer
            repairer.setName(name);
            repairer.setEmail(email);
            repairer.setAddress(address);

            em.merge(repairer);
            return repairer;
        } catch (MyEntityNotFoundException e) {
            throw new MyEntityNotFoundException(e.getMessage());
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public Repairer find(String username) {
        return em.find(Repairer.class, username);
    }

    public Repairer findRepairerOrThrow(String username) throws MyEntityNotFoundException {
        Repairer repairer = em.find(Repairer.class, username);
        if (repairer == null) {
            throw new MyEntityNotFoundException("Repairer " + username + " not found");
        }
        return repairer;
    }

    public void delete(String username) throws MyEntityNotFoundException {
        Repairer repairer = findRepairerOrThrow(username);
        em.remove(repairer);
    }

    public void assignOccurrence(String username, long occurrenceCode, String link) throws MyEntityNotFoundException, NotAuthorizedException, APIBadResponseException {
        try {
            // Find Repairer
            Repairer repairer = find(username);
            validateRepairer(repairer);

            // Find Occurrence
            Occurrence occurrence = em.find(Occurrence.class, occurrenceCode);

            validateOccurrenceExists(occurrence);
            validateOccurrenceState(occurrence, State.APPROVED);

            //Check if repairer is in insurance companies' experts. If so, then we don't need experts' approval
            if (checkIfRepairerIsInInsuranceCompanyRepairers(repairer.getUsername(), occurrence.getInsurance().getInsuranceCompany())) {
                occurrence.setState(State.REPAIRER_WAITING_LIST);
                // Send email to the repairer that the occurrence was assigned
                emailBean.send(repairer.getEmail(), "Occurrence " + occurrence.getId() + " assigned to you",
                        "A new occurrence has been assigned to you by " + occurrence.getClient().getUsername() + ".\n\n" + occurrence.getDescription() + '\n'+
                        "You can access the occurrence at " + link);
            } else {
                occurrence.setState(State.WAITING_FOR_APPROVAL_OF_REPAIRER_BY_EXPERT);
                // Send email to Experts to approve the repairer
                List<Expert> experts = expertBean.getAllExperts();
                for (Expert expert : experts) {
                    emailBean.send(expert.getEmail(), "Occurrence " + occurrence.getId() + " assigned to " + repairer.getUsername() + " waiting approval",
                            "A new occurrence has been assigned to " + repairer.getUsername() + " by " + occurrence.getClient().getUsername() + " and is waiting for your approval.\n\n" + occurrence.getDescription() + '\n'+
                                    "You can access the occurrence at " + link);
                }
            }

            occurrence.setRepairer(repairer);
            repairer.addOccurrence(occurrence);

        } catch (MyEntityNotFoundException e) {
            throw new MyEntityNotFoundException(e.getMessage());
        } catch (NotAuthorizedException e) {
            throw new NotAuthorizedException(e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public void unassignOccurrence(String username, long occurrenceCode, String link) throws MyEntityNotFoundException, NotAuthorizedException {
        try {
            // Find Repairer
            Repairer repairer = find(username);
            validateRepairer(repairer);

            // Find Occurrence
            Occurrence occurrence = em.find(Occurrence.class, occurrenceCode);
            validateOccurrence(repairer, occurrence, null);

            // Validate if the occurrence is in correct state
            List<State> validStates = new ArrayList<>();
            validStates.add(State.REPAIRER_WAITING_LIST);
            validStates.add(State.WAITING_FOR_APPROVAL_OF_REPAIRER_BY_EXPERT);

            validateRepairerIsAssignedToOccurrence(repairer, occurrence);

            occurrence.setState(State.APPROVED);
            occurrence.setRepairer(null);
            repairer.removeOccurrence(occurrence);

            // Send email to the repairer that the occurrence is no longer assigned to him
            emailBean.send(occurrence.getClient().getEmail(), "Occurrence " + occurrence.getId() + " started",
                    "The occurrence " + occurrence.getId() + " has been started by " + repairer.getUsername() + ".\n"+
                            "You can see the occurrence at " + link);

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public void startOccurrence(String username, long occurrenceCode, String link) throws MyEntityNotFoundException, NotAuthorizedException {
        try {
            // Find Repairer
            Repairer repairer = find(username);
            validateRepairer(repairer);

            // Find Occurrence
            Occurrence occurrence = em.find(Occurrence.class, occurrenceCode);
            validateOccurrence(repairer, occurrence, State.REPAIRER_WAITING_LIST);

            // Start Occurrence
            occurrence.setState(State.ACTIVE);

            // Send email to the client that the occurrence was started
            emailBean.send(occurrence.getClient().getEmail(), "Occurrence " + occurrence.getId() + " started",
                    "The occurrence " + occurrence.getId() + " has been started by " + repairer.getUsername() + ".\n\n"+
                            occurrence.getDescription() + '\n'+ "You can see the occurrence at " + link);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public void failOccurrence(String username, long occurrenceCode, String description) throws MyEntityNotFoundException, NotAuthorizedException {
        try {
            // Find Repairer
            Repairer repairer = find(username);
            validateRepairer(repairer);

            // Find Occurrence
            Occurrence occurrence = em.find(Occurrence.class, occurrenceCode);
            validateOccurrence(repairer, occurrence, State.ACTIVE);

            // Fail Occurrence
            occurrence.setState(State.FAILED);

            //Update final date occurrence
            Date finalDate = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            String finalDateStr = formatter.format(finalDate);

            occurrence.setFinalDate(finalDateStr);

            // Get Occurrence Description
            String[] descriptionReceived = description.split("&");

            if(descriptionReceived.length != 2){
                throw new IllegalArgumentException("Occurrence description is not valid");
            }

            String link = descriptionReceived[0];
            String occurrenceDescription = descriptionReceived[1];

            // Build Occurrence Description
            String newOccurrenceDescription = occurrence.getDescription() + "\n[" + repairer.getUsername() + "]: " + occurrenceDescription;
            occurrence.setDescription(newOccurrenceDescription);

            // Send email to the client that the occurrence was failed
            emailBean.send(occurrence.getClient().getEmail(), "Occurrence " + occurrence.getId() + " failed",
                    "The occurrence " + occurrence.getId() + " has been failed by " + repairer.getUsername() + ".\n\n" + newOccurrenceDescription +
                            "\nYou can see the occurrence at " + link);

            // Transform Documents into a Blob
            zipFilesBean.compressDocuments(occurrence);

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void finishOccurrence(String username, long occurrenceCode, String description) throws MyEntityNotFoundException, NotAuthorizedException {
        try {
            // Find Repairer
            Repairer repairer = find(username);
            validateRepairer(repairer);

            // Find Occurrence
            Occurrence occurrence = em.find(Occurrence.class, occurrenceCode);
            validateOccurrence(repairer, occurrence, State.ACTIVE);

            // Approve Occurrence
            occurrence.setState(State.RESOLVED);

            //Update final date occurrence
            Date finalDate = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            String finalDateStr = formatter.format(finalDate);

            occurrence.setFinalDate(finalDateStr);

            // Get Occurrence Description
            String[] descriptionReceived = description.split("&");

            if(descriptionReceived.length != 2){
                throw new IllegalArgumentException("Occurrence description is not valid");
            }

            String link = descriptionReceived[0];
            String occurrenceDescription = descriptionReceived[1];

            // Build Occurrence Description
            String newOccurrenceDescription = occurrence.getDescription() + "\n[" + repairer.getUsername() + "]: " + occurrenceDescription;
            occurrence.setDescription(newOccurrenceDescription);

            // Send email to the client that the occurrence was finished
            emailBean.send(occurrence.getClient().getEmail(), "Occurrence " + occurrence.getId() + " finished",
                    "The occurrence " + occurrence.getId() + " has been finished by " + repairer.getUsername() + ".\n\n" + newOccurrenceDescription +
                            "\nYou can see the occurrence at " + link);

            // Transform Documents into a Blob
            zipFilesBean.compressDocuments(occurrence);

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void validateRepairer(Repairer repairer) throws MyEntityNotFoundException {
        if (repairer == null) {
            throw new MyEntityNotFoundException("Repairer not found");
        }
    }

    private void validateOccurrenceExists(Occurrence occurrence) {
        if (occurrence == null) {
            throw new IllegalArgumentException("Occurrence not found");
        }
    }

    private void validateOccurrence(Repairer repairer, Occurrence occurrence, State state) throws MyEntityNotFoundException, NotAuthorizedException {
        try {
            validateOccurrenceExists(occurrence);
            validateRepairerIsAssignedToOccurrence(repairer, occurrence);
            validateOccurrenceState(occurrence, state);
        } catch (MyEntityNotFoundException e) {
            throw new MyEntityNotFoundException(e.getMessage());
        } catch (NotAuthorizedException e) {
            throw new NotAuthorizedException(e.getMessage());
        }
    }

    private void validateRepairerIsAssignedToOccurrence(Repairer repairer, Occurrence occurrence) throws NotAuthorizedException, MyEntityNotFoundException {
        // Check if Repairer is assigned to Occurrence
        if (!occurrence.getRepairer().getUsername().equals(repairer.getUsername())) {
            throw new NotAuthorizedException("Repairer " + repairer.getUsername() + " is not assigned to this occurrence " + occurrence.getId());
        }
    }

    private void validateOccurrenceState(Occurrence occurrence, State state) throws NotAuthorizedException {
        // Check if Occurrence is in the correct state
        if (occurrence.getState() != state) {
            throw new NotAuthorizedException("Occurrence is not in the correct state, current state is " + occurrence.getState());
        }
    }

    private void validateOccurrenceState(Occurrence occurrence, List<State> state) throws NotAuthorizedException {
        // Check if Occurrence is in the correct state
        if (!state.contains(occurrence.getState())) {
            throw new NotAuthorizedException("Occurrence " + occurrence.getId() + " is not in the correct state. Current state is " + occurrence.getState());
        }
    }

    private boolean checkIfRepairerIsInInsuranceCompanyRepairers(String repairerUsername, String insuranceCompanyName) throws MyEntityNotFoundException, APIBadResponseException {
        List<String> repairersFromInsuranceOfOccurrence = mockAPIBean.
                getAttributeFromSpecificInsuranceCompany("insuranceCompanies", "name", insuranceCompanyName, "repairers");
        for (String string : repairersFromInsuranceOfOccurrence) {
            if (repairerUsername.equals(string)) {
                return true;
            }
        }
        return false;
    }

    public List<Occurrence> occurrences(String username) throws MyEntityNotFoundException {
        try {
            // Find Repairer
            Repairer repairer = find(username);
            if (repairer == null) {
                throw new MyEntityNotFoundException("Repairer not found");
            }

            // Get Occurrences
            Hibernate.initialize(repairer.getOccurrences());
            return repairer.getOccurrences();
        } catch (MyEntityNotFoundException e) {
            throw new MyEntityNotFoundException(e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public void updatePassword(String username, String password, String oldPassword) {
        Repairer repairer = find(username);
        if (repairer == null) {
            throw new MyEntityNotFoundException("Repairer not found");
        }

        if(!hasher.hash(oldPassword).equals(repairer.getPassword())){
            throw new NotAuthorizedException("Old password is incorrect");
        }

        em.lock(repairer, LockModeType.OPTIMISTIC);
        repairer.setPassword(hasher.hash(password));
    }

    public List<Occurrence> getRepairerAssignedOccurrences(int limit, int pageNumber, String username) {
        Repairer repairer = find(username);
        if(repairer == null){
            throw new MyEntityNotFoundException("Repairer not found");
        }

        return em.createNamedQuery("getRepairerOccurrences", Occurrence.class)
                .setParameter("repairer", repairer)
                .setFirstResult((pageNumber - 1) * limit)
                .setMaxResults(limit)
                .getResultList();
    }

    public Long countAssignedOccurrences(String username) {
        Repairer repairer = find(username);
        if(repairer == null){
            throw new MyEntityNotFoundException("Client not found");
        }

        return em.createNamedQuery("countRepairerOccurrences", Long.class)
                .setParameter("repairer", repairer)
                .getSingleResult();
    }
}
