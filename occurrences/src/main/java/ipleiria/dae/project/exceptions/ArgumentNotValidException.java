package ipleiria.dae.project.exceptions;

import javax.ejb.EJBException;

public class ArgumentNotValidException extends EJBException {

    public ArgumentNotValidException(String message) {
        super(message);
    }
}
