package ipleiria.dae.project.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@NamedQuery(
        name = "getStudentDocuments",
        query = "SELECT doc FROM Document doc WHERE doc.student.username = :username"
)
public class Document {
    @NotNull
    String filepath, filename;
    @NotNull
    @ManyToOne
    Student student;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    public Document() {
    }

    public Document(String filepath, String filename, Student student) {
        this.filepath = filepath;
        this.filename = filename;
        this.student = student;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Long getId() {
        return id;
    }
}
