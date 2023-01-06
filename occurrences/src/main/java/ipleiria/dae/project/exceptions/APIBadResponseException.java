package ipleiria.dae.project.exceptions;

import javax.ejb.EJBException;

public class APIBadResponseException extends EJBException {

    public APIBadResponseException(String message) {
        super(message);
    }
}
