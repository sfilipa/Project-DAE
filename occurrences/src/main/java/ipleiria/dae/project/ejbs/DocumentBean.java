package ipleiria.dae.project.ejbs;

import ipleiria.dae.project.entities.Document;
import ipleiria.dae.project.entities.Student;
import ipleiria.dae.project.exceptions.MyConstraintViolationException;
import ipleiria.dae.project.exceptions.MyEntityNotFoundException;
import org.hibernate.Hibernate;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolationException;
import java.util.List;

@Stateless
public class DocumentBean {

    @PersistenceContext
    EntityManager em;

    public Document create(String filepath, String filename, String username) throws MyEntityNotFoundException, MyConstraintViolationException {
        Document document;
        Student student = em.find(Student.class, username);

        if (student == null)
            throw new MyEntityNotFoundException(username + " - Student was not Found");

        try {
            document = new Document(filepath, filename, student);
            em.persist(document);
            student.add(document);
        } catch (ConstraintViolationException e) {
            throw new MyConstraintViolationException(e);
        }
        return em.find(Document.class, document.getId());
    }

    public Document find(Long id) {
        return em.find(Document.class, id);
    }

    public List<Document> getStudentDocuments(String username) throws MyEntityNotFoundException {
        Student student = em.find(Student.class, username);
        if (student == null)
            throw new MyEntityNotFoundException(username + " - Student was not Found");

        Hibernate.initialize(student.getDocuments());
        return student.getDocuments();
    }
}
