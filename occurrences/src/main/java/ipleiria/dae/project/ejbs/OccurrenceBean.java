package ipleiria.dae.project.ejbs;

import ipleiria.dae.project.entities.Client;
import ipleiria.dae.project.entities.Insurance;
import ipleiria.dae.project.entities.Occurrence;
import ipleiria.dae.project.enumerators.InsuredAssetType;
import ipleiria.dae.project.enumerators.State;
import org.hibernate.Hibernate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;

public class OccurrenceBean {
    @PersistenceContext
    private EntityManager em;

    public Occurrence find(long code){
        return em.find(Occurrence.class, code);
    }

    public Occurrence findOrFail(long code){
        Occurrence occurrence = em.getReference(Occurrence.class, code);
        Hibernate.initialize(occurrence);
        return occurrence;
    }

    public List<Occurrence> getAllOccurrences() {
        return (List<Occurrence>) em.createNamedQuery("getAllOccurrences").getResultList();
    }

    public Occurrence create(long id, Client client, Date date, InsuredAssetType insuredAssetType, State state, Insurance insurance) {
        Occurrence occurrence = find(id);
        if(occurrence != null){
            return null;
        }

        occurrence = new Occurrence(id, client, date, state, insuredAssetType, insurance);
        em.persist(occurrence);
        return occurrence;
    }
}
