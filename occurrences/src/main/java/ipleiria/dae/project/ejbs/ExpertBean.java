package ipleiria.dae.project.ejbs;

import ipleiria.dae.project.entities.Expert;
import ipleiria.dae.project.entities.InsuranceCompany;
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

    public Expert create(String username, String password, String name, String email, String company_username) {
        InsuranceCompany company = em.find(InsuranceCompany.class, company_username);
        if(company == null ){
            return null;
        }

//        Expert expert = find(username);
//        if (expert != null){
//            return null;
//        }

        Expert expert = new Expert(username, hasher.hash(password), name, email, company);
        em.persist(expert);
        company.addExpert(expert);
        return find(username);
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


    //penso que nunca se vai fazer updates pq isto vem da API
    public Expert update(String username, String password, String name, String email, long company_usernmae) {
        Expert expert = find(username);
        if (expert == null){
            return null;
        }
        InsuranceCompany company = em.find(InsuranceCompany.class, company_usernmae);

        if(company == null ){
            return null;
        }
        expert.setPassword(password);
        expert.setName(name);
        expert.setEmail(email);
        expert.setCompany(company);
        return expert;
    }

    public Expert find(String username) {
        return em.find(Expert.class, username);
    }

    public void delete(String username) {
        Expert expert = find(username);
        if (expert == null){
            throw new IllegalArgumentException("Expert not found");
        }

        InsuranceCompany company = expert.getCompany();
        company.removeExpert(expert);
        em.remove(expert);
    }

    public void disapproveOccurrence(String username, long occurrenceCode, String description) {
        try {
            // Find Expert
            Expert expert = find(username);
            validateExpert(expert);

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
            validateExpert(expert);

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

    private void validateExpert(Expert expert) {
        if (expert == null){
            throw new IllegalArgumentException("Expert not found");
        }
    }

    private void validateOccurrenceExists(Occurrence occurrence) {
        if(occurrence == null) {
            throw new IllegalArgumentException("Occurrence not found");
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
