package ipleiria.dae.project.exceptions;

import javax.ejb.EJBException;

public class MyEntityExistsException extends EJBException {
    public MyEntityExistsException(String message) {
        super(message);
    }
}
