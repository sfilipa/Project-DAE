package ipleiria.dae.project.ejbs;

import ipleiria.dae.project.entities.Document;
import ipleiria.dae.project.exceptions.MyConstraintViolationException;
import ipleiria.dae.project.exceptions.MyEntityNotFoundException;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolationException;
import java.util.List;

@Stateless
public class DocumentBean {
    @EJB
    private OccurrenceBean occurrenceBean;

    @PersistenceContext
    private EntityManager em;

    public Document create(String filepath, String filename, long occurenceId) {
        var occurence = occurrenceBean.find(occurenceId);
        if (occurence == null) {
            throw new MyEntityNotFoundException("Occurrence not found");
        }
        var document = new Document(filepath, filename, occurence);

        em.persist(document);
        occurence.addDocument(document);

        return document;
    }

    public Document find(Long id) {
        return em.find(Document.class, id);
    }

    public Document findOrFail(Long id) {
        var document = em.getReference(Document.class, id);
        Hibernate.initialize(document);
        return document;
    }

    public List<Document> getOccurenceDocuments(long occurenceId) {
        return em.createNamedQuery("getOccurenceDocuments", Document.class).setParameter("id", occurenceId).getResultList();
    }
}
