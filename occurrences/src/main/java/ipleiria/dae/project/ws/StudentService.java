package ipleiria.dae.project.ws;

import ipleiria.dae.project.dtos.EmailDTO;
import ipleiria.dae.project.entities.Student;
import ipleiria.dae.project.exceptions.MyConstraintViolationException;
import ipleiria.dae.project.exceptions.MyEntityExistsException;
import ipleiria.dae.project.exceptions.MyEntityNotFoundException;
import org.hibernate.Hibernate;
import ipleiria.dae.project.dtos.StudentDTO;
import ipleiria.dae.project.dtos.SubjectDTO;
import ipleiria.dae.project.ejbs.EmailBean;
import ipleiria.dae.project.ejbs.StudentBean;
import ipleiria.dae.project.entities.Subject;
import ipleiria.dae.project.security.Authenticated;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.mail.MessagingException;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.List;
import java.util.stream.Collectors;

@Path("students") // relative url web path for this service
@Produces({MediaType.APPLICATION_JSON}) // injects header “Content-Type: application/json”
@Consumes({MediaType.APPLICATION_JSON}) // injects header “Accept: application/json”
@Authenticated
@RolesAllowed({"Teacher", "Administrator"})
public class StudentService {
    @EJB
    private StudentBean studentBean;
    @EJB
    private EmailBean emailBean;
    @Context
    private SecurityContext securityContext;

    @GET // means: to call this endpoint, we need to use the HTTP GET method
    @Path("/all") // means: the relative url path is “/api/students/”
    public List<StudentDTO> getAllStudentsWS() {
        return toDTOsNoSubjects(studentBean.getAllStudents());
    }

    private StudentDTO toDTONoSubjects(Student student) {
        return new StudentDTO(
                student.getUsername(),
                student.getPassword(),
                student.getName(),
                student.getEmail(),
                student.getCourse().getCode(),
                student.getCourse().getName()
        );
    }

    private List<StudentDTO> toDTOsNoSubjects(List<Student> students) {
        return students.stream().map(this::toDTONoSubjects).collect(Collectors.toList());
    }

    private StudentDTO toDTO(Student student) {
        Hibernate.initialize(student.getSubjects());
        return new StudentDTO(
                student.getUsername(),
                student.getPassword(),
                student.getName(),
                student.getEmail(),
                student.getCourse().getCode(),
                student.getCourse().getName(),
                subjectsToDTOs(student.getSubjects())
        );
    }

    private List<StudentDTO> toDTOs(List<Student> students) {
        return students.stream().map(this::toDTO).collect(Collectors.toList());
    }

    // Converts an entity Student to a DTO Subject class
    private SubjectDTO toDTO(Subject subject) {
        return new SubjectDTO(
                subject.getCode(),
                subject.getName(),
                subject.getCourse().getName(),
                subject.getCourse().getCode(),
                subject.getCourseYear(),
                subject.getScholarYear()
        );
    }

    // converts an entire list of entities into a list of DTOs
    private List<SubjectDTO> subjectsToDTOs(List<Subject> subjects) {
        return subjects.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @GET
    @Authenticated
    @RolesAllowed({"Student"})
    @Path("/{username}")
    public Response getStudentDetails(@PathParam("username") String username) {
        Student student;
        try {
            student = studentBean.find(username);
        } catch (Exception exception) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("ERROR_FINDING_STUDENT")
                    .build();
        }
        return Response.ok(toDTO(student)).build();
    }

    @GET
    @Path("/{username}/subjects")
    public Response getStudentSubjects(@PathParam("username") String username) {
        List<SubjectDTO> listSubjects;
        try {
            listSubjects = subjectsToDTOs(studentBean.getSubjects(username));
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("ERROR_FINDING_STUDENT")
                    .build();
        }
        return Response.ok(listSubjects).build();
    }

    @POST
    @Path("/")
    public Response create(StudentDTO studentDTO)
            throws MyEntityExistsException, MyEntityNotFoundException, MyConstraintViolationException {

        Student student = studentBean.create(
                studentDTO.getUsername(),
                studentDTO.getPassword(),
                studentDTO.getName(),
                studentDTO.getEmail(),
                studentDTO.getCourseCode()
        );

        return Response.status(Response.Status.CREATED)
                .entity(toDTO(student))
                .build();
    }

    @POST
    @Path("/{username}")
    public Response delete(@PathParam("username") String username, StudentDTO studentDTO)
            throws MyEntityNotFoundException, MyConstraintViolationException {

        studentBean.delete(username, studentDTO.getCourseCode());

        return Response.status(Response.Status.ACCEPTED).build();
    }

    @PUT
    @Path("/{username}")
    public Response update(@PathParam("username") String username, StudentDTO studentDTO) {
        Student student;
        try {
            student = studentBean.update(
                    username,
                    studentDTO.getPassword(),
                    studentDTO.getName(),
                    studentDTO.getEmail(),
                    studentDTO.getCourseCode()
            );
        } catch (Exception exception) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.status(Response.Status.ACCEPTED).entity(toDTO(student)).build();
    }

    @POST
    @Path("/{username}/email/send")
    public Response sendEmail(@PathParam("username") String username, EmailDTO email)
            throws MyEntityNotFoundException, MessagingException {
        Student student = studentBean.find(username);
        if (student == null) {
            throw new MyEntityNotFoundException("Student with username '" + username
                    + "' not found in our records.");
        }
        emailBean.send(student.getEmail(), email.getSubject(), email.getMessage());
        return Response.status(Response.Status.OK).entity("E-mail sent").build();
    }
}
