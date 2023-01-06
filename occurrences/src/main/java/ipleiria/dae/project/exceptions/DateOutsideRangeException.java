package ipleiria.dae.project.exceptions;

import javax.ejb.EJBException;

public class DateOutsideRangeException extends EJBException {

    public DateOutsideRangeException(String message) {
        super(message);
    }
}
