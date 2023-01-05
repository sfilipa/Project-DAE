package ipleiria.dae.project.ejbs;

import ipleiria.dae.project.entities.Expert;
import ipleiria.dae.project.entities.Occurrence;
import ipleiria.dae.project.enumerators.State;
import ipleiria.dae.project.exceptions.MyEntityExistsException;
import ipleiria.dae.project.security.Hasher;
import org.hibernate.Hibernate;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class ExpertBean {
    @PersistenceContext
    EntityManager em;

    @Inject // import javax.inject.Inject;
    private Hasher hasher;

    public List<Expert> getAllExperts() {
        return (List<Expert>) em.createNamedQuery("getAllExperts").getResultList();
    }

    public Expert create(String username, String password, String name, String email, String insuranceCompany) throws MyEntityExistsException {
        try {
            // Find Insurance Company
            String company = findInsuranceCompany(insuranceCompany);

            // Verify if the username already exists
            Expert expert = find(username);
            validateExpertDoesNotExist(expert);

            // Create Expert
            Expert newExpert = new Expert(username, hasher.hash(password), name, email, company);
            em.persist(newExpert);
            return find(username);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public Expert update(String username, String password, String name, String email, String insuranceCompany) {
        try {
            // Find company
            String company = MockAPIBean.getInsuranceCompany(insuranceCompany);
            validateCompanyExists(company);

            // Find Expert
            Expert expert = em.find(Expert.class, username);
            validateExpertExists(expert);

            // Update
            expert.setPassword(password);
            expert.setName(name);
            expert.setEmail(email);
            expert.setInsuranceCompany(company);
            return expert;
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public Expert find(String username) {
        return em.find(Expert.class, username);
    }

    public String findInsuranceCompany(String insuranceCompany) {
        // Find Insurance Company
        String company = MockAPIBean.getInsuranceCompany(insuranceCompany);
        if(company.equals("")){
            throw new IllegalArgumentException("Company not found");
        }
        return company;
    }

    public void delete(String username) {
        try {
            // Find Expert
            Expert expert = find(username);
            validateExpertExists(expert);

            // Delete Expert
            em.remove(expert);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public void disapproveOccurrence(String username, long occurrenceCode, String description) {
        try {
            // Find Expert
            Expert expert = find(username);
            validateExpertExists(expert);

            // Find Occurrence
            Occurrence occurrence = em.find(Occurrence.class, occurrenceCode);
            validateOccurrence(expert, occurrence);

            validateOccurrenceState(occurrence, State.PENDING);

            // Disapprove Occurrence
            occurrence.setState(State.DISAPPROVED);

            // Get Occurrence Description
            String occurrenceDescription = occurrence.getDescription();

            // Build Occurrence Description
            String newOccurrenceDescription = occurrenceDescription + "\n[" + expert.getUsername() + "]: " + description;
            occurrence.setDescription(newOccurrenceDescription);

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public void approveOccurrence(String username, long occurrenceCode, String description) {
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
            String occurrenceDescription = occurrence.getDescription();

            // Build Occurrence Description
            String newOccurrenceDescription = occurrenceDescription + "\n[" + expert.getUsername() + "]: " + description;
            occurrence.setDescription(newOccurrenceDescription);

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public void addOccurrence(String username, long occurrenceCode) {
        try{
            // Find Expert
            Expert expert = find(username);
            validateExpertExists(expert);

            // Find Occurrence
            Occurrence occurrence = em.find(Occurrence.class, occurrenceCode);

            if(!expert.getInsuranceCompany().equals(occurrence.getInsurance().getInsuranceCompany())){
                throw new IllegalArgumentException("Expert and Occurrence are not from the same company");
            }

            // Validate Occurrence
            validateOccurrenceExists(occurrence);

            // Validate if the occurrence is PENDING
            validateOccurrenceState(occurrence, State.PENDING);

            expert.addOccurrence(occurrence);
            occurrence.addExpert(expert);
        }catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public void removeOccurrence(String username, long occurrenceCode) {
        try{
            // Find Expert
            Expert expert = find(username);
            validateExpertExists(expert);

            // Find Occurrence
            Occurrence occurrence = em.find(Occurrence.class, occurrenceCode);
            validateOccurrence(expert, occurrence);

            expert.removeOccurrence(occurrence);
            occurrence.removeExpert(expert);
        }catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public List<Occurrence> occurrences(String username) {
        try {
            // Find Expert
            Expert expert = find(username);
            validateExpertExists(expert);

            // Get Occurrences
            Hibernate.initialize(expert.getOccurrences());
            return expert.getOccurrences();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public void acceptRepairer(String username, long occurrenceCode){
        try{
            // Find Expert
            Expert expert = find(username);
            validateExpertExists(expert);

            // Find Occurrence
            Occurrence occurrence = em.find(Occurrence.class, occurrenceCode);
            validateOccurrence(expert, occurrence);

            validateExpertIsAssignedToOccurrence(expert, occurrence);

            validateOccurrenceState(occurrence, State.WAITING_FOR_APPROVAL_OF_REPAIRER_BY_EXPERT);

            occurrence.setState(State.REPAIRER_WAITING_LIST);
        }catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public void rejectRepairer(String username, long occurrenceCode, String description){
        try{
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
            String occurrenceDescription = occurrence.getDescription();

            // Build Occurrence Description
            String newOccurrenceDescription = occurrenceDescription + "\n[" + expert.getUsername() + "]: " + description;
            occurrence.setDescription(newOccurrenceDescription);
        }catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private void validateExpertExists(Expert expert) {
        if (expert == null){
            throw new IllegalArgumentException("Expert not found");
        }
    }

    private void validateExpertDoesNotExist(Expert expert) {
        if (expert != null){
            throw new IllegalArgumentException("Expert already exists");
        }
    }

    private void validateOccurrenceExists(Occurrence occurrence) {
        if(occurrence == null) {
            throw new IllegalArgumentException("Occurrence not found");
        }
    }

    public void validateCompanyExists(String company) {
        if(company.equals("")){
            throw new IllegalArgumentException("Company not found");
        }
    }

    private void validateOccurrence(Expert expert, Occurrence occurrence) {
        try {
            validateOccurrenceExists(occurrence);
            validateExpertIsAssignedToOccurrence(expert, occurrence);
            validateOccurrenceState(occurrence, State.PENDING);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private void validateExpertIsAssignedToOccurrence(Expert expert, Occurrence occurrence) {
        // Check if Expert is assigned to Occurrence
        if(!occurrence.isExpertInOccurrence(expert)) {
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
