package ipleiria.dae.project.entities;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Table(name = "occurrences")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)

@Entity
public class Occurrence {
    private Client client;
    private Date date;
    private List<Expert> experts;

    public Occurrence() {
        experts = new LinkedList<>();
    }
}
