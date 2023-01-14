package ipleiria.dae.project.ejbs;

import ipleiria.dae.project.entities.Expert;
import ipleiria.dae.project.entities.Occurrence;
import ipleiria.dae.project.enumerators.State;
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
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Stateless
public class ExpertBean {
    @PersistenceContext
    EntityManager em;
    @EJB
    private EmailBean emailBean;
    @EJB
    private ZipFilesBean zipFilesBean;
    @Inject
    private Hasher hasher;

    public List<Expert> getAllExperts() {
        try {
            return (List<Expert>) em.createNamedQuery("getAllExperts").getResultList();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("No experts found");
        }
    }

    public Expert create(String username, String password, String name, String email, String insuranceCompany) throws MyEntityExistsException, IllegalArgumentException{
        // Find Insurance Company
        String company = findInsuranceCompany(insuranceCompany);

        // Verify if the username already exists
        Expert expert = find(username);
        validateExpertDoesNotExist(expert);

        // Create Expert
        Expert newExpert = new Expert(username, hasher.hash(password), name, email, company);
        em.persist(newExpert);
        return newExpert;
    }

    public Expert update(String username, String name, String email) throws MyEntityNotFoundException {
        try {
            // Find Expert
            Expert expert = em.find(Expert.class, username);
            validateExpertExists(expert);

            // Update
            expert.setName(name);
            expert.setEmail(email);

            // To ensure the changes are persisted to the database
            em.merge(expert);

            return expert;
        } catch (MyEntityNotFoundException e) {
            throw new MyEntityNotFoundException(e.getMessage());
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public Expert find(String username) throws MyEntityNotFoundException {
        return em.find(Expert.class, username);
    }

    public Expert findOrFail(String username) throws MyEntityNotFoundException {
        Expert expert = em.find(Expert.class, username);
        validateExpertExists(expert);
        return expert;
    }

    public String findInsuranceCompany(String insuranceCompany) throws MyEntityNotFoundException {
        // Find Insurance Company
        String company = MockAPIBean.getInsuranceCompany(insuranceCompany);
        validateCompanyExists(company);
        return company;
    }

    public void delete(String username) throws MyEntityNotFoundException {
        // Find Expert
        Expert expert = find(username);
        validateExpertExists(expert);

        // Delete Expert
        em.remove(expert);
    }

    public void disapproveOccurrence(String username, long occurrenceCode, String description) throws MyEntityNotFoundException, NotAuthorizedException, IOException {
        // Find Expert
        Expert expert = find(username);
        validateExpertExists(expert);

        // Find Occurrence
        Occurrence occurrence = em.find(Occurrence.class, occurrenceCode);
        validateOccurrence(expert, occurrence);

        validateOccurrenceState(occurrence, State.PENDING);

        // Disapprove Occurrence
        occurrence.setState(State.DISAPPROVED);

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
        String newOccurrenceDescription = occurrence.getDescription() + "\n[" + expert.getUsername() + "]: " + occurrenceDescription;
        occurrence.setDescription(newOccurrenceDescription);

        // Send Email to Client
        sendDisapprovalEmail(occurrence, expert, newOccurrenceDescription, link);

        //Compress document
        zipFilesBean.compressDocuments(occurrence);
    }

    private void sendDisapprovalEmail(Occurrence occurrence, Expert expert, String newOccurrenceDescription, String link) {
        emailBean.send(
                occurrence.getClient().getEmail(),
                "Occurrence " + occurrence.getId() + " disapproved",
                "Your occurrence was disapproved by " + expert.getUsername() + ".\n\n" + newOccurrenceDescription  +
                        "You can access the occurrence at " + link + "\n"
        );
    }

    public void approveOccurrence(String username, long occurrenceCode, String description) throws MyEntityNotFoundException, NotAuthorizedException {
        try {
            // Find Expert
            Expert expert = find(username);
            validateExpertExists(expert);

            // Find Occurrence
            Occurrence occurrence = em.find(Occurrence.class, occurrenceCode);
            validateOccurrence(expert, occurrence);

            validateOccurrenceState(occurrence, State.PENDING);

            // Approve Occurrence
            occurrence.setState(State.APPROVED);

            // Get Occurrence Description
            String[] descriptionReceived = description.split("&");

            if(descriptionReceived.length != 2){
                throw new IllegalArgumentException("Occurrence description is not valid");
            }

            String link = descriptionReceived[0];
            String occurrenceDescription = descriptionReceived[1];

            // Build Occurrence Description
            String newOccurrenceDescription = occurrence.getDescription() + "\n[" + expert.getUsername() + "]: " + occurrenceDescription;
            occurrence.setDescription(newOccurrenceDescription);

            // Send Email to Client
            sendApprovalEmail(occurrence, expert, newOccurrenceDescription, link);

        } catch (MyEntityNotFoundException e) {
            throw new MyEntityNotFoundException(e.getMessage());
        } catch (NotAuthorizedException e) {
            throw new NotAuthorizedException(e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private void sendApprovalEmail(Occurrence occurrence, Expert expert, String newOccurrenceDescription, String link) {
        emailBean.send(
                occurrence.getClient().getEmail(),
                "Occurrence " + occurrence.getId() + " approved",
                "Your occurrence was approved by " + expert.getUsername() + ".\n\n" + newOccurrenceDescription+ '\n'+
                        "You can access the occurrence at " + link
        );
    }

    public void addOccurrence(String username, long occurrenceCode) throws MyEntityNotFoundException, NotAuthorizedException {
        try {
            // Find Expert
            Expert expert = find(username);
            validateExpertExists(expert);

            // Find Occurrence
            Occurrence occurrence = em.find(Occurrence.class, occurrenceCode);

            if (!expert.getInsuranceCompany().equals(occurrence.getInsurance().getInsuranceCompany())) {
                throw new IllegalArgumentException("Expert and Occurrence are not from the same company");
            }

            // Validate Occurrence
            validateOccurrenceExists(occurrence);

            // Validate if the occurrence is in correct state
            List<State> validStates = new ArrayList<>();
            validStates.add(State.PENDING);
            validStates.add(State.APPROVED);
            validStates.add(State.WAITING_FOR_APPROVAL_OF_REPAIRER_BY_EXPERT);
            validateOccurrenceState(occurrence, validStates);

            expert.addOccurrence(occurrence);
            occurrence.addExpert(expert);
        } catch (MyEntityNotFoundException e) {
            throw new MyEntityNotFoundException(e.getMessage());
        } catch (NotAuthorizedException e) {
            throw new NotAuthorizedException(e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public void removeOccurrence(String username, long occurrenceCode) throws MyEntityNotFoundException, NotAuthorizedException {
        try {
            // Find Expert
            Expert expert = find(username);
            validateExpertExists(expert);

            // Find Occurrence
            Occurrence occurrence = em.find(Occurrence.class, occurrenceCode);
            validateOccurrence(expert, occurrence);

            // Validate if expert has made an action
            if(occurrence.getDescription().contains(expert.getUsername())){
                throw new NotAuthorizedException("Expert has already made an action. Can't unassign this occurrence");
            }

            expert.removeOccurrence(occurrence);
            occurrence.removeExpert(expert);
        } catch (MyEntityNotFoundException e) {
            throw new MyEntityNotFoundException(e.getMessage());
        } catch (NotAuthorizedException e) {
            throw new NotAuthorizedException(e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public List<Occurrence> occurrences(String username) throws MyEntityNotFoundException {
        try {
            // Find Expert
            Expert expert = find(username);
            validateExpertExists(expert);

            // Get Occurrences
            Hibernate.initialize(expert.getOccurrences());
            return expert.getOccurrences();
        } catch (MyEntityNotFoundException e) {
            throw new MyEntityNotFoundException(e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public void acceptRepairer(String username, long occurrenceCode, String description) throws MyEntityNotFoundException, NotAuthorizedException, IllegalArgumentException {
        try {
            // Find Expert
            Expert expert = find(username);
            validateExpertExists(expert);

            // Find Occurrence
            Occurrence occurrence = em.find(Occurrence.class, occurrenceCode);
            validateOccurrence(expert, occurrence);

            validateExpertIsAssignedToOccurrence(expert, occurrence);

            validateOccurrenceState(occurrence, State.WAITING_FOR_APPROVAL_OF_REPAIRER_BY_EXPERT);

            occurrence.setState(State.REPAIRER_WAITING_LIST);

            // Get Occurrence Description
            String[] descriptionReceived = description.split("&");

            if(descriptionReceived.length != 2){
                throw new IllegalArgumentException("Occurrence description is not valid");
            }

            String link = descriptionReceived[0];
            String occurrenceDescription = descriptionReceived[1];

            // Build Occurrence Description
            String newOccurrenceDescription = occurrence.getDescription() + "\n[" + expert.getUsername() + "]: " + occurrenceDescription;
            occurrence.setDescription(newOccurrenceDescription);

            // Send Email to Repairer about being accepted to repair the occurrence
            emailBean.send(occurrence.getRepairer().getEmail(), "Occurrence " + occurrence.getId() + " accepted",
                    "You were accepted to repair the occurrence " + occurrence.getId() + " by " + expert.getUsername() + ".\n\n" + occurrence.getDescription()+ '\n'+
                            "You can access the occurrence at " + link);

            // Send Email to Client about the Repairer of the occurrence being accepted by the Expert
            emailBean.send(occurrence.getClient().getEmail(), "Occurrence " + occurrence.getId() + " accepted",
                    "The repairer " + occurrence.getRepairer().getUsername() + " was accepted to repair the occurrence " + occurrence.getId() + " by " + expert.getUsername() + ".\n\n" + occurrence.getDescription()+ '\n'+
                            "You can access the occurrence at " + link);

        } catch (MyEntityNotFoundException e) {
            throw new MyEntityNotFoundException(e.getMessage());
        } catch (NotAuthorizedException e) {
            throw new NotAuthorizedException(e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public void rejectRepairer(String username, long occurrenceCode, String description) throws MyEntityNotFoundException, NotAuthorizedException {
        try {
            // Find Expert
            Expert expert = find(username);
            validateExpertExists(expert);

            // Find Occurrence
            Occurrence occurrence = em.find(Occurrence.class, occurrenceCode);
            validateOccurrence(expert, occurrence);

            validateExpertIsAssignedToOccurrence(expert, occurrence);

            validateOccurrenceState(occurrence, State.WAITING_FOR_APPROVAL_OF_REPAIRER_BY_EXPERT);

            //Reject current assigned repairer
            occurrence.setRepairer(null);

            occurrence.setState(State.APPROVED);

            // Get Occurrence Description
            String[] descriptionReceived = description.split("&");

            if(descriptionReceived.length != 2){
                throw new IllegalArgumentException("Occurrence description is not valid");
            }

            String link = descriptionReceived[0];
            String occurrenceDescription = descriptionReceived[1];

            // Build Occurrence Description
            String newOccurrenceDescription = occurrence.getDescription() + "\n[" + expert.getUsername() + "]: " + occurrenceDescription;
            occurrence.setDescription(newOccurrenceDescription);

            // Send Email to Client about the Repairer of the occurrence being rejected by the Expert
            emailBean.send(occurrence.getClient().getEmail(), "Occurrence " + occurrence.getId() + " repairer service rejected",
                    "The repairer " + occurrence.getRepairer().getUsername() + " was rejected to repair the occurrence " + occurrence.getId() + " by " + expert.getUsername() + ".\n\n" + occurrence.getDescription()+ '\n'+
                            "You can access the occurrence at " + link);
        } catch (MyEntityNotFoundException e) {
            throw new MyEntityNotFoundException(e.getMessage());
        } catch (NotAuthorizedException e) {
            throw new NotAuthorizedException(e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private void validateExpertExists(Expert expert) throws MyEntityNotFoundException {
        if (expert == null) {
            throw new MyEntityNotFoundException("Expert not found");
        }
    }

    private void validateExpertDoesNotExist(Expert expert) throws MyEntityExistsException {
        if (expert != null) {
            throw new MyEntityExistsException("Expert " + expert.getUsername() + " already exists");
        }
    }

    private void validateOccurrenceExists(Occurrence occurrence) throws MyEntityNotFoundException {
        if (occurrence == null) {
            throw new MyEntityNotFoundException("Occurrence not found");
        }
    }

    public void validateCompanyExists(String company) throws MyEntityNotFoundException {
        if (company.equals("")) {
            throw new MyEntityNotFoundException("Company not found");
        }
    }

    private void validateOccurrence(Expert expert, Occurrence occurrence) throws MyEntityNotFoundException, NotAuthorizedException {
        try {
            validateOccurrenceExists(occurrence);
            validateExpertIsAssignedToOccurrence(expert, occurrence);
        } catch (MyEntityNotFoundException e) {
            throw new MyEntityNotFoundException(e.getMessage());
        } catch (NotAuthorizedException e) {
            throw new NotAuthorizedException(e.getMessage());
        }
    }

    private void validateExpertIsAssignedToOccurrence(Expert expert, Occurrence occurrence) throws NotAuthorizedException {
        // Check if Expert is assigned to Occurrence
        if (!occurrence.isExpertInOccurrence(expert)) {
            throw new NotAuthorizedException("Expert " + expert.getUsername() + " is not assigned to this occurrence " + occurrence.getId());
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

    public void updatePassword(String username, String password, String oldPassword) {
        Expert expert = find(username);
        if (expert == null) {
            throw new MyEntityNotFoundException("Expert not found");
        }

        if(!hasher.hash(oldPassword).equals(expert.getPassword())){
            throw new NotAuthorizedException("Old password is incorrect");
        }

        em.lock(expert, LockModeType.OPTIMISTIC);
        expert.setPassword(hasher.hash(password));
    }

    public void updateInsuranceCompany(String username, String insuranceCompany) {
        Expert expert = find(username);
        if (expert == null) {
            throw new MyEntityNotFoundException("Expert not found");
        }
        em.lock(expert, LockModeType.OPTIMISTIC);
        Hibernate.initialize(expert);

        var occurrences = expert.getOccurrences();
        Hibernate.initialize(occurrences);

        if(checkIfHasOccurrencesInProgress(occurrences)){
            throw new MyEntityExistsException("Expert has occurrences in progress - can't update his insurance company");
        }

        expert.setInsuranceCompany(insuranceCompany);
    }

    private boolean checkIfHasOccurrencesInProgress(List<Occurrence> occurrences) {
        for (Occurrence occurrence : occurrences) {
            State state = occurrence.getState();
            if(state != State.FAILED && state != State.RESOLVED){
                return true;
            }
        }
        return false;
    }

    public List<Occurrence> getExpertAssignedOccurrences(int limit, int pageNumber, String username) {
        Expert expert = find(username);
        if(expert == null){
            throw new MyEntityNotFoundException("Expert not found");
        }

        return em.createNamedQuery("getExpertOccurrences", Occurrence.class)
                .setParameter("username", username)
                .setFirstResult((pageNumber - 1) * limit)
                .setMaxResults(limit)
                .getResultList();
    }

    public Long countAssignedOccurrences(String username) {
        Expert expert = find(username);
        if(expert == null){
            throw new MyEntityNotFoundException("Client not found");
        }

        return em.createNamedQuery("countExpertOccurrences", Long.class)
                .setParameter("username", username)
                .getSingleResult();
    }
}
