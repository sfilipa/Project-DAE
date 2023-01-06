package ipleiria.dae.project.exceptions;

import javax.ejb.EJB;
import javax.ejb.EJBException;

public class NotAuthorizedException extends EJBException {
    public NotAuthorizedException(String message) {
        super(message);
    }

}
