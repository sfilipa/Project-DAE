package ipleiria.dae.project.enumerators;

public enum State {
    ACTIVE, //when expert approves the occurence's coverage by insurance company
    FAILED, //when expert disapproves the occurence's coverage by insurance company
    PENDING, //initial state
    RESOLVED //final state
}
