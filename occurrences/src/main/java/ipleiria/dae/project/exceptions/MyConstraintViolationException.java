package ipleiria.dae.project.exceptions;

import javax.ejb.EJBException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;
import java.util.stream.Collectors;

public class MyConstraintViolationException extends EJBException {

    public MyConstraintViolationException(ConstraintViolationException e) {
        super(getConstraintViolationMessages(e));
    }
    private static String getConstraintViolationMessages(ConstraintViolationException e) {
        return e.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));
    }
}