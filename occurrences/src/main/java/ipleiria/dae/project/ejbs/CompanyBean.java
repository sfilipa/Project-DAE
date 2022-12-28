package ipleiria.dae.project.ejbs;

import ipleiria.dae.project.entities.Company;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class CompanyBean {

    @PersistenceContext
    EntityManager em;

    public void create(String username, String password, String name, String email, String address, long phoneNumber, long nipc) {
        var company = new Company(username, password, name, email, address, phoneNumber, nipc);
        em.persist(company);
    }
}
