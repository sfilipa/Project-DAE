package ipleiria.dae.project.exceptions;

import javax.ejb.EJBException;

public class NifNotValidException extends EJBException {

    public NifNotValidException(String message) {
        super(message);
    }
}
