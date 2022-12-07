package ipleiria.dae.project.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

@Entity
@NamedQueries({
    @NamedQuery(
        name = "getAllCourses",
        query = "SELECT c FROM Course c ORDER BY c.code" // JPQL
    )
})

@Table(
    name = "courses",
    uniqueConstraints = @UniqueConstraint(columnNames = {"name"})
)
public class Course extends Versionable implements Serializable {
    @Id
    long code;
    @NotNull
    String name;
    @NotNull
    @OneToMany(mappedBy = "course", cascade = CascadeType.REMOVE)
    List<Student> students;

    @NotNull
    @OneToMany
    List<Subject> subjects;

    @Version
    private int version;

    public Course() {
        students = new LinkedList<>();
        subjects = new LinkedList<>();
    }

    public Course(Long code, String name) {
        this.code = code;
        this.name = name;
        students = new LinkedList<>();
        subjects = new LinkedList<>();
    }

    public void add(Student student) {
        if (student == null || students.contains(student)) {
            return;
        }
        students.add(student);
    }

    public void remove(Student student) {
        if (student == null || !students.contains(student)) {
            return;
        }
        students.remove(student);
    }

    public void add(Subject subject) {
        if (subject == null || subjects.contains(subject)) {
            return;
        }
        subjects.add(subject);
    }

    public void remove(Subject subject) {
        if (subject == null || !subjects.contains(subject)) {
            return;
        }
        subjects.remove(subject);
    }

    public Long getCode() {
        return code;
    }

    public void setCode(Long code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public boolean equals(long subjectCode) {
        for (Subject subject : subjects) {
            if (subject.getCode() == subjectCode)
                return true;
        }
        return false;
    }
}
