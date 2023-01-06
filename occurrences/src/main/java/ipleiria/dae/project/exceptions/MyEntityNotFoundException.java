package ipleiria.dae.project.exceptions;

import javax.ejb.EJBException;

public class MyEntityNotFoundException extends EJBException {
    public MyEntityNotFoundException(String message) {
        super(message);
    }
}
