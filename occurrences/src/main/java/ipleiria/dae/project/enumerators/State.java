package ipleiria.dae.project.enumerators;

public enum State {
    ACTIVE, //when repairer is solving the occurrence's problem
    FAILED, //when something fails -> it requires to have a reason in the occurrence description
    DISAPPROVED , //when expert disapproves the occurrence's coverage by insurance company
    PENDING, //initial state
    APPROVED, //when expert approves the occurrence's coverage by insurance company
    RESOLVED //final state
}
