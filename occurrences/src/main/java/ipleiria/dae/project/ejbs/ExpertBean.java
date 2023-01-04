package ipleiria.dae.project.ejbs;

import ipleiria.dae.project.entities.Expert;
import ipleiria.dae.project.entities.InsuranceCompany;
import ipleiria.dae.project.entities.Occurrence;
import ipleiria.dae.project.entities.*;
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

            validateOccurrenceState(occurrence, State.PENDING);

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

            validateOccurrenceState(occurrence, State.PENDING);

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

    public void addOccurrence(String username, String occurrenceCode) {
        try{
            // Find Expert
            Expert expert = find(username);
            validateExpert(expert);

            // Find Occurrence
            Occurrence occurrence = em.find(Occurrence.class, occurrenceCode);

            if(expert.getCompany() != occurrence.getInsurance().getCompany()){
                throw new IllegalArgumentException("Expert and Occurrence are not from the same company");
            }

            validateOccurrence(expert, occurrence);

            expert.addOccurrence(occurrence);
            occurrence.addExpert(expert);
        }catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public void removeOccurrence(String username, String occurrenceCode) {
        try{
            // Find Expert
            Expert expert = find(username);
            validateExpert(expert);

            // Find Occurrence
            Occurrence occurrence = em.find(Occurrence.class, occurrenceCode);
            validateOccurrence(expert, occurrence);

            expert.removeOccurrence(occurrence);
            occurrence.removeExpert(expert);
        }catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public void acceptRepairer(String username, long occurrenceCode){
        try{
            // Find Expert
            Expert expert = find(username);
            validateExpert(expert);

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
            validateExpert(expert);

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
            String newOccurrenceDescription = occurrenceDescription + "\n- " + expert.getUsername() + ": " + description;
            occurrence.setDescription(newOccurrenceDescription);
        }catch (IllegalArgumentException e) {
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
            //validateExpertIsAssignedToOccurrence(expert, occurrence); -> nao se pode fazer esta validação para o removeOccurrence
            validateOccurrenceState(occurrence, State.PENDING);
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

    private void validateOccurrenceState(Occurrence occurrence, State state) {
        // Check if Occurrence is in the correct state
        if(occurrence.getState() != state) {
            throw new IllegalArgumentException("Occurrence is not in the correct state");
        }
    }
}
