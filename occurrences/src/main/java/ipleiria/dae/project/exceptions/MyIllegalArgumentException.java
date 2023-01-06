package ipleiria.dae.project.exceptions;

import javax.ejb.EJBException;

public class MyIllegalArgumentException extends EJBException {
    public MyIllegalArgumentException(String message) {
        super(message);
    }
}
