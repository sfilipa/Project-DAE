package ipleiria.dae.project.ws;

import ipleiria.dae.project.entities.Course;
import ipleiria.dae.project.exceptions.MyEntityExistsException;
import ipleiria.dae.project.security.Authenticated;
import ipleiria.dae.project.dtos.CourseDTO;
import ipleiria.dae.project.ejbs.CourseBean;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Path("courses") // relative url web path for this service
@Produces({MediaType.APPLICATION_JSON}) // injects header “Content-Type: application/json”
@Consumes({MediaType.APPLICATION_JSON}) // injects header “Accept: application/json”


public class CourseService {
    @EJB
    private CourseBean courseBean;

    @GET // means: to call this endpoint, we need to use the HTTP GET method
    @Path("/all") // means: the relative url path is “/api/courses/all”
    public List<CourseDTO> getAllCoursesWS() {
        return toDTOs(courseBean.getAllCourses());
    }

    // Converts an entity Course to a DTO Course class
    private CourseDTO toDTO(Course course) {
        return new CourseDTO(
            course.getCode(),
            course.getName()
        );
    }

    // converts an entire list of entities into a list of DTOs
    private List<CourseDTO> toDTOs(List<Course> courses) {
        return courses.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @POST
    @Path("/")
    @Authenticated
    @RolesAllowed({"Administrator"})
    public Response create(CourseDTO courseDTO) throws MyEntityExistsException {
        Course newCourse;
            newCourse = courseBean.create(courseDTO.getCode(), courseDTO.getName());

        return Response.status(Response.Status.CREATED)
                .entity(toDTO(newCourse))
                .build();    }
}
