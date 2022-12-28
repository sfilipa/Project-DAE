package ipleiria.dae.project.ejbs;

import ipleiria.dae.project.entities.*;
import ipleiria.dae.project.enumerators.InsuredAssetType;
import ipleiria.dae.project.enumerators.State;
import org.hibernate.Hibernate;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;

@Stateless
public class OccurrenceBean {
    @PersistenceContext
    private EntityManager em;

    public Occurrence find(long id) {
        return em.find(Occurrence.class, id);
    }

    public Occurrence findOrFail(long code) {
        Occurrence occurrence = em.getReference(Occurrence.class, code);
        Hibernate.initialize(occurrence);
        return occurrence;
    }

    public List<Occurrence> getAllOccurrences() {
        return (List<Occurrence>) em.createNamedQuery("getAllOccurrences").getResultList();
    }

    public Occurrence create(String usernameClient, Date date, InsuredAssetType insuredAssetType, State state, String insuranceCode, String description) {
        Client client = em.find(Client.class, usernameClient);
        Insurance insurance = em.find(Insurance.class, insuranceCode);
        Occurrence occurrence = new Occurrence(client, date, state, insuredAssetType, insurance, description, null);
        em.persist(occurrence);
        return occurrence;
    }

    public void delete(long id) {
        Occurrence occurrence = find(id);
        em.remove(occurrence);
    }

    public Occurrence update(long id, String usernameClient, Date date, State state, String insuranceCode) { //TODO : Exceptions
        Occurrence occurrence = em.find(Occurrence.class, id);
        Client client = em.find(Client.class, usernameClient);
        Insurance insurance = em.find(Insurance.class, insuranceCode);
        occurrence.setClient(client);
        occurrence.setDate(date);
        occurrence.setState(state);
        occurrence.setInsurance(insurance);
        return null;
    }

    public void disapproveOccurrence(long id) {
        Occurrence occurrence = em.find(Occurrence.class, id);
        if (occurrence == null) {
            return;
        }
        occurrence.setState(State.FAILED);
    }

    public void approveOccurrence(long id) {
        Occurrence occurrence = em.find(Occurrence.class, id);
        if (occurrence == null) {
            return;
        }
        occurrence.setState(State.ACTIVE);
    }

    public int addExpert(long id, String username) {
        Occurrence occurrence = em.find(Occurrence.class, id);
        if (occurrence == null) {
            return -1; //devolver exception
        }
        if(occurrence.getState() != State.PENDING) {
            return -2; //devolver exception
        }
        Expert expert = em.find(Expert.class, username);
        if (expert == null) {
            return -3; //devolver exception
        }
        if(occurrence.isExpertInOccurrence(expert)){
            return -4; //devolver exception
        }

        //verificar que o perito é da mesma seguradora
        if(expert.getCompany() != occurrence.getInsurance().getCompany()){
            return -5; //devolver exception
        }

        occurrence.addExpert(expert);
//        occurrence.setExpert(expert);
        return 0;
    }

    public int removeExpert(long id, String username){
        Occurrence occurrence = em.find(Occurrence.class, id);
        if (occurrence == null) {
            return -1; //devolver exception
        }
        Expert expert = em.find(Expert.class, username);
        if (expert == null) {
            return -2; //devolver exception
        }
        if(!occurrence.isExpertInOccurrence(expert)){
            return -3; //devolver exception
        }

        occurrence.removeExpert(expert);
        expert.removeOccurrence(occurrence);
        return 0;
    }

    public int assignRepairer(long id, String username) {
        Occurrence occurrence = em.find(Occurrence.class, id);
        if (occurrence == null) {
            return -1; //devolver exception
        }
        if(occurrence.getState() != State.ACTIVE){
            return -2; //devolver exception
        }
        Repairer repairer = em.find(Repairer.class, username);
        if (repairer == null) {
            return -3; //devolver exception
        }

        occurrence.setRepairer(repairer);
        repairer.addOccurrence(occurrence);
        return 0;
    }

    public int unassignRepairer(long id){
        Occurrence occurrence = em.find(Occurrence.class, id);
        if (occurrence == null) {
            return -1; //devolver exception
        }

        Repairer repairer = occurrence.getRepairer();
        if (repairer == null) {
            return -2; //devolver exception
        }

        occurrence.setRepairer(null);
        repairer.removeOccurrence(occurrence);
        return 0;
    }
}
