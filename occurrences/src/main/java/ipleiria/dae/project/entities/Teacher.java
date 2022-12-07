package ipleiria.dae.project.entities;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

@Entity
@NamedQueries({
    @NamedQuery(
        name = "getAllTeachers",
        query = "SELECT t FROM Teacher t ORDER BY t.name" // JPQL
    ),
})

public class Teacher extends User implements Serializable {
    @NotNull
    String office;

    @NotNull
    @ManyToMany(mappedBy = "teachers")
    List<Subject> subjects;

    public Teacher() {
        subjects = new LinkedList<>();
    }

    public Teacher(String username, String password, String name, String email, String office) {
        super(username, password, name, email);
        this.office = office;
        subjects = new LinkedList<>();
    }

    public String getOffice() {
        return office;
    }

    public void setOffice(String office) {
        this.office = office;
    }

    public List<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<Subject> subjects) {
        this.subjects = subjects;
    }
}
