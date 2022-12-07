package ipleiria.dae.project.ws;

import ipleiria.dae.project.dtos.SubjectDTO;
import ipleiria.dae.project.dtos.TeacherDTO;
import ipleiria.dae.project.ejbs.TeacherBean;
import ipleiria.dae.project.entities.Subject;
import ipleiria.dae.project.entities.Teacher;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Path("teachers") // relative url web path for this service
@Produces({MediaType.APPLICATION_JSON}) // injects header “Content-Type: application/json”
@Consumes({MediaType.APPLICATION_JSON}) // injects header “Accept: application/json”

public class TeacherService {

    @EJB
    private TeacherBean teacherBean;

    @GET // means: to call this endpoint, we need to use the HTTP GET method
    @Path("/all") // means: the relative url path is “/api/students/”
    public List<TeacherDTO> getAllTeachersWS() {
        return toDTOs(teacherBean.getAllTeachers());
    }

    // Converts an entity Student to a DTO Student class
    private TeacherDTO toDTO(Teacher teacher) {
        return new TeacherDTO(
            teacher.getUsername(),
            teacher.getPassword(),
            teacher.getName(),
            teacher.getEmail(),
            teacher.getOffice()
        );
    }

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

    private List<TeacherDTO> toDTOs(List<Teacher> teachers) {
        return teachers.stream().map(this::toDTO).collect(Collectors.toList());
    }

    private List<SubjectDTO> SubjectstoDTOs(List<Subject> subjects) {
        return subjects.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @POST
    @Path("/{username}/{code}")
    public Response associateTeacherToSubject(@PathParam("username") String username, @PathParam("code") long subjectCode) {
        try {
            teacherBean.associateTeacherToSubject(username, subjectCode);
        } catch (Exception exception) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.status(Response.Status.ACCEPTED).build();
    }

    @GET
    @Path("/{username}/subjects")
    public List<SubjectDTO> getAllSubjectsOfTeacherWS(@PathParam("username") String username) {
        return SubjectstoDTOs(teacherBean.getAllSubjectsOfTeacher(username));
    }

    @POST
    @Path("/")
    public Response create(TeacherDTO teacherDTO) {
        Teacher teacher;
        try {
            teacher = teacherBean.create(
                teacherDTO.getUsername(),
                teacherDTO.getPassword(),
                teacherDTO.getName(),
                teacherDTO.getEmail(),
                teacherDTO.getOffice()
            );
        } catch (Exception exception) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.status(Response.Status.CREATED).entity(toDTO(teacher)).build();
    }

    @GET
    @Path("/{username}")
    public Response getTeacher(@PathParam("username") String username) {
        Teacher teacher;
        try {
            teacher = teacherBean.find(username);
        } catch (Exception exception) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity("ERROR_FINDING_STUDENT")
                .build();
        }
        return Response.ok(toDTO(teacher)).build();
    }

    @PUT
    @Path("/{username}")
    public Response updateTeacher(@PathParam("username") String username, TeacherDTO teacherDTO) {
        Teacher updatedTeacher;
        try {
            updatedTeacher = teacherBean.update(
                username,
                teacherDTO.getPassword(),
                teacherDTO.getName(),
                teacherDTO.getEmail(),
                teacherDTO.getOffice()
            );
        } catch (Exception exception) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.status(Response.Status.ACCEPTED).entity(toDTO(updatedTeacher)).build();
    }

    @DELETE
    @Path("/{username}")
    public Response deleteTeacher(@PathParam("username") String username) {
        try {
            teacherBean.delete(username);
        } catch (Exception exception) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.status(Response.Status.OK).build();
    }
}
