package ipleiria.dae.project.ejbs;

import ipleiria.dae.project.entities.Course;
import ipleiria.dae.project.entities.Student;
import ipleiria.dae.project.entities.Teacher;
import org.hibernate.Hibernate;
import ipleiria.dae.project.entities.Subject;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class SubjectBean {

    @PersistenceContext
    EntityManager em;

    public void create(long code, String name, Course course, int courseYear, int scholarYear) {
        Subject subject = new Subject(code, name, course, courseYear, scholarYear);
        em.persist(subject);
    }

    public List<Subject> getAllSubjects() {
        return (List<Subject>) em.createNamedQuery("getAllSubjects").getResultList();
    }

    public Subject find(long code) {
        return em.find(Subject.class, code);
    }

    public List<Teacher> getAllTeachersOfSubject(long code) {
        Subject subject = em.find(Subject.class, code);
        if (subject == null)
            return null;
        Hibernate.initialize(subject.getTeachers());
        return subject.getTeachers();
    }

    public List<Student> getAllStudents(long code) {
        Subject subject = em.find(Subject.class, code);
        return subject.getStudents();
    }
}
