package ipleiria.dae.project.entities;

import ipleiria.dae.project.enumerators.State;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static javax.persistence.GenerationType.SEQUENCE;

@Table(name = "occurrences")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)

@Entity
public class Occurrence implements Serializable {
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotNull
    private Client client;
    @NotNull
    @GeneratedValue
    private Date date;
    @NotNull
    private State state;
    private String insuredAssetType;


    private List<Expert> experts;

    public Occurrence() {

        experts = new LinkedList<>();
    }
}
