package ipleiria.dae.project.ejbs;

import ipleiria.dae.project.entities.Occurrence;
import org.hibernate.Hibernate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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
}
