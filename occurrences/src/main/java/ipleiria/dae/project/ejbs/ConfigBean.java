package ipleiria.dae.project.ejbs;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.util.logging.Logger;

@Startup
@Singleton
public class ConfigBean {

    private static final Logger logger = Logger.getLogger("ejbs.ConfigBean");
    @EJB
    CourseBean courseBean;
    @EJB
    StudentBean studentBean;
    @EJB
    SubjectBean subjectBean;
    @EJB
    AdministratorBean administratorBean;
    @EJB
    TeacherBean teacherBean;

    @PostConstruct
    public void populateDB() {
        System.out.println("Hello Java EE!");

        try {
            courseBean.create(9119, "Engenharia Informatica");
            courseBean.create(9200, "Engenharia dos Engenheiros");
        } catch (Exception exception) {
            logger.severe(exception.getMessage());
        }
        // Creates Student in DB
        try {
            studentBean.create("ivoafonsobispo", "password", "Ivo Afonso Bispo", "ivoafobispo@gmail.com", 9119);
            studentBean.create("anamartin", "password", "Ana Luisa Pinto Martin", "anamartin@gmail.com", 9119);
            //studentBean.create("anamartin", "password", "Log of Error", "anamartin@gmail.com", 9119);
        } catch (Exception exception) {
            logger.severe(exception.getMessage());
        }

        // Creates Subject in DB
        subjectBean.create(1, "Filosofia", courseBean.find(9119), 2001, 1);
        subjectBean.create(2, "Matematica", courseBean.find(9119), 2005, 2);

        studentBean.enrollStudentInSubject("ivoafonsobispo", 1);
        studentBean.enrollStudentInSubject("ivoafonsobispo", 2);
        studentBean.enrollStudentInSubject("anamartin", 1);

        administratorBean.create("josematias", "omatias", "Jose Matias", "josematias@mail.pt");

        teacherBean.create("mariajoana", "mariaejoana", "Maria Joana", "mariajoana@mail.pt", "L-31");

        teacherBean.associateTeacherToSubject("mariajoana", 1);
    }
}
