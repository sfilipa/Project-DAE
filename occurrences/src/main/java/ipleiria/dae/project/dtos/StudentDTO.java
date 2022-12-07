package ipleiria.dae.project.dtos;


import javax.persistence.Id;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class StudentDTO implements Serializable {
    @Id
    String username;
    String password, name, email;
    long courseCode;
    String courseName;
    List<SubjectDTO> subjects;

    public StudentDTO() {
        subjects = new LinkedList<>();
    }

    public StudentDTO(String username, String password, String name, String email,long courseCode, String courseName) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.courseCode = courseCode;
        this.courseName = courseName;
        subjects = new LinkedList<>();
    }

    public StudentDTO(String username, String password, String name, String email,long courseCode, String courseName, List<SubjectDTO> subjectDTOS) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.subjects = subjectDTOS;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(long courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public List<SubjectDTO> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<SubjectDTO> subjects) {
        this.subjects = subjects;
    }
}
