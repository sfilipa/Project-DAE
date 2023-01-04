package ipleiria.dae.project.enumerators;

public enum State {
    PENDING, //initial state
    APPROVED, //when expert approves the occurrence's coverage by insurance company
    DISAPPROVED, //when expert disapproves the occurrence's coverage by insurance company
    WAITING_FOR_APPROVAL_OF_REPAIRER_BY_EXPERT, //when expert approves and there's a waiting for the client to choose where to repair
    REPAIRER_WAITING_LIST, //when expert approves and there's a waiting for the repairer to begin work
    ACTIVE, //when repairer is solving the occurrence's problem
    FAILED, //when something fails -> it requires to have a reason in the occurrence description
    RESOLVED //final state
}
