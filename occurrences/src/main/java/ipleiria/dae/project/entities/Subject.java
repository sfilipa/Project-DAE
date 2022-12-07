package ipleiria.dae.project.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

@NamedQueries({
    @NamedQuery(
        name = "getAllSubjects",
        query = "SELECT s FROM Subject s ORDER BY s.course.name,s.scholarYear DESC, s.courseYear, s.name" // JPQL
    )
})

@Table(
    name = "subjects",
    uniqueConstraints = @UniqueConstraint(columnNames = {"name", "course_code", "scholar_year"})
)

@Entity
public class Subject extends Versionable implements Serializable {
    @Id
    long code;
    @NotNull
    String name;
    @NotNull
    @ManyToOne
    Course course;
    @Column(name = "course_year")
    int courseYear;
    @Column(name = "scholar_year")
    int scholarYear;
    @NotNull
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "subjects_students",
        joinColumns = @JoinColumn(name = "subject_code", referencedColumnName = "code"),
        inverseJoinColumns = @JoinColumn(name = "student_username", referencedColumnName = "username"))
    List<Student> students;

    @NotNull
    @ManyToMany
    @JoinTable(name = "subjects_teachers",
        joinColumns = @JoinColumn(name = "subject_code", referencedColumnName = "code"),
        inverseJoinColumns = @JoinColumn(name = "teacher_username", referencedColumnName = "username"))
    List<Teacher> teachers;

    @Version
    private int version;

    public Subject() {
        students = new LinkedList<>();
        teachers = new LinkedList<>();
    }

    public Subject(long code, String name, Course course, int couseYear, int scholarYear) {
        this.code = code;
        this.name = name;
        this.course = course;
        this.courseYear = couseYear;
        this.scholarYear = scholarYear;
        students = new LinkedList<>();
        teachers = new LinkedList<>();
    }

    public void addStudent(Student student) {
        if (student == null || students.contains(student)) {
            return;
        }
        students.add(student);
    }

    public void removeStudent(Student student) {
        if (student == null || !students.contains(student)) {
            return;
        }

        students.remove(student);
    }

    public void associate(Teacher teacher) {
        if (teacher == null || teachers.contains(teacher)) {
            return;
        }
        teachers.add(teacher);
    }

    public void dissociate(Teacher teacher) {
        if (teacher == null || !teachers.contains(teacher)) {
            return;
        }

        teachers.remove(teacher);
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

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public int getCourseYear() {
        return courseYear;
    }

    public void setCourseYear(int courseYear) {
        this.courseYear = courseYear;
    }

    public int getScholarYear() {
        return scholarYear;
    }

    public void setScholarYear(int scholarYear) {
        this.scholarYear = scholarYear;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public List<Teacher> getTeachers() {
        return teachers;
    }

    public void setTeachers(List<Teacher> teachers) {
        this.teachers = teachers;
    }
}
