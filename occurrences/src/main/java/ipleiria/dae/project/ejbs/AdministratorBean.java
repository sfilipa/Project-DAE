package ipleiria.dae.project.ejbs;

import ipleiria.dae.project.entities.Administrator;
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

    public Administrator findOrFail(String username) {
        try {
            return MockAPIBean.getAdministrator(username);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
