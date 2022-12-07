package ipleiria.dae.project.dtos;

import jakarta.persistence.Id;

public class SubjectDTO {

    @Id
    long code;
    String name, courseName;
    long courseCode;
    int courseYear, schoolarYear;

    public SubjectDTO() {
    }

    public SubjectDTO(long code, String name, String courseName, long courseCode, int courseYear, int schoolarYear) {
        this.code = code;
        this.name = name;
        this.courseName = courseName;
        this.courseCode = courseCode;
        this.courseYear = courseYear;
        this.schoolarYear = schoolarYear;
    }

    public long getCode() {
        return code;
    }

    public void setCode(long code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public long getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(long courseCode) {
        this.courseCode = courseCode;
    }

    public int getCourseYear() {
        return courseYear;
    }

    public void setCourseYear(int courseYear) {
        this.courseYear = courseYear;
    }

    public int getSchoolarYear() {
        return schoolarYear;
    }

    public void setSchoolarYear(int schoolarYear) {
        this.schoolarYear = schoolarYear;
    }
}
