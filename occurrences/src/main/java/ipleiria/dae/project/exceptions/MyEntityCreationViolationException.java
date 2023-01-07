package ipleiria.dae.project.exceptions;

import javax.ejb.EJBException;

public class MyEntityCreationViolationException extends EJBException {
    public MyEntityCreationViolationException(String message) {
        super(message);
    }
}
