package ipleiria.dae.project.exceptions;

import javax.ejb.EJBException;

public class EntityManagerPersistDBException extends EJBException {
    public EntityManagerPersistDBException(String message) {
        super(message);
    }
}

