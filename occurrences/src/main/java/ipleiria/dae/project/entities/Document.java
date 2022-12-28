package ipleiria.dae.project.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "documents")
@NamedQuery(
        name = "getOccurenceDocuments",
        query = "SELECT doc FROM Document doc WHERE doc.occurrence.id = :id"//TODO: change later
)
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @NotNull
    String filepath, filename;

    @ManyToOne
    private Occurrence occurrence;

    public Document() {
    }

    public Document(String filepath, String filename, Occurrence occurrence) {
        this.filepath = filepath;
        this.filename = filename;
        this.occurrence = occurrence;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Occurrence getOcurrence() {
        return occurrence;
    }

    public void setOcurrence(Occurrence occurrence) {
        this.occurrence = occurrence;
    }
}
