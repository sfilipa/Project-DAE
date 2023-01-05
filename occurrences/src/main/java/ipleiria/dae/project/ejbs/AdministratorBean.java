package ipleiria.dae.project.ejbs;

import ipleiria.dae.project.entities.Administrator;
import ipleiria.dae.project.entities.Expert;
import ipleiria.dae.project.exceptions.MyEntityExistsException;
import ipleiria.dae.project.exceptions.MyEntityNotFoundException;
import ipleiria.dae.project.security.Hasher;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class AdministratorBean {
    @PersistenceContext
    EntityManager em;

    @Inject
    private Hasher hasher;

    // Find Administrator in MockAPI
    public Administrator findOrFail(String username) throws MyEntityNotFoundException {
        try {
            return MockAPIBean.getAdministrator(username);
        } catch (MyEntityNotFoundException e) {
            throw new MyEntityNotFoundException(e.getMessage());
        }
    }

    // Find Administrator in DB
    public Administrator find(String username) throws MyEntityNotFoundException {
        try {
            Administrator administrator = em.find(Administrator.class, username);
            if (administrator == null) {
                throw new MyEntityNotFoundException("Administrator not found");
            }
            return administrator;
        } catch (MyEntityNotFoundException e) {
            throw new MyEntityNotFoundException(e.getMessage());
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public void create(Administrator administrator) throws MyEntityNotFoundException, MyEntityExistsException {
        try {
            // Find if the administrator already exists
            find(administrator.getUsername());

        } catch (MyEntityNotFoundException e) {
            // Create Administrator
            Administrator newAdministrator = new Administrator(administrator.getUsername(), administrator.getPassword(), administrator.getName(), administrator.getEmail());
            em.persist(newAdministrator);
            find(administrator.getUsername());
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
