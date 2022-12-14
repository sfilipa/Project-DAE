package ipleiria.dae.project.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@NamedQuery(
        name = "getClientDocuments",
        query = "SELECT doc FROM Document doc WHERE doc.client.username = :username"
)
public class Document {
    @NotNull
    String filepath, filename;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    public Document() {
    }

    public Document(String filepath, String filename) {
        this.filepath = filepath;
        this.filename = filename;
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

    public Long getId() {
        return id;
    }
}
