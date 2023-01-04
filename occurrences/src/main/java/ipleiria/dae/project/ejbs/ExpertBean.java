package ipleiria.dae.project.ejbs;

import ipleiria.dae.project.entities.Expert;
import ipleiria.dae.project.entities.Occurrence;
import ipleiria.dae.project.enumerators.State;
import ipleiria.dae.project.security.Hasher;

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

    public Expert create(String username, String password, String name, String email, String insuranceCompany) {
        try {
            // Find Insurance Company
            String company = findInsuranceCompany(insuranceCompany);

            // Verify if the username already exists
            Expert expert = find(username);
            validateExpertExists(expert);

            // Create Expert
            Expert newExpert = new Expert(username, hasher.hash(password), name, email, company);
            em.persist(newExpert);
            return find(username);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public int addOccurrence(String username, String occurrence_code) {
        Expert expert = find(username);
        if (expert == null){
            return -1;
        }
        Occurrence occurrence = em.find(Occurrence.class, occurrence_code);
        if (occurrence == null){
            return -2;
        }

        expert.addOccurrence(occurrence);
        occurrence.addExpert(expert);
        return 0;
    }

    public int removeOccurrence(String username, String occurrence_code) {
        Expert expert = find(username);
        if (expert == null){
            return -1;
        }
        Occurrence occurrence = em.find(Occurrence.class, occurrence_code);
        if (occurrence == null){
            return -2;
        }

        expert.removeOccurrence(occurrence);
        occurrence.removeExpert(expert);
        return 0;
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

            // Disapprove Occurrence
            occurrence.setState(State.DISAPPROVED);

            // Get Occurrence Description
            String occurrenceDescription = occurrence.getDescription();

            // Build Occurrence Description
            String newOccurrenceDescription = occurrenceDescription + "\n- " + expert.getUsername() + ": " + description;
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

            // Approve Occurrence
            occurrence.setState(State.APPROVED);

            // Get Occurrence Description
            String occurrenceDescription = occurrence.getDescription();

            // Build Occurrence Description
            String newOccurrenceDescription = occurrenceDescription + "\n- " + expert.getUsername() + ": " + description;
            occurrence.setDescription(newOccurrenceDescription);

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private void validateExpertExists(Expert expert) {
        if (expert == null){
            throw new IllegalArgumentException("Expert not found");
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
            //validateExpertIsAssignedToOccurrence(expert, occurrence);
            validateOccurrenceIsPending(occurrence);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private void validateExpertIsAssignedToOccurrence(Expert expert, Occurrence occurrence) {
        // Check if Expert is assigned to Occurrence
        if(occurrence.isExpertInOccurrence(expert)) {
            throw new IllegalArgumentException("Expert is not assigned to this occurrence");
        }
    }

    private void validateOccurrenceIsPending(Occurrence occurrence) {
        // Check if Occurrence is in the correct state
        if(occurrence.getState() != State.PENDING) {
            throw new IllegalArgumentException("Occurrence is not in the correct state");
        }
    }
}
