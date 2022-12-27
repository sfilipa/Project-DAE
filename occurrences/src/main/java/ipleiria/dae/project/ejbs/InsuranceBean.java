package ipleiria.dae.project.ejbs;

import ipleiria.dae.project.entities.Client;
import ipleiria.dae.project.entities.Company;
import ipleiria.dae.project.entities.Insurance;
import ipleiria.dae.project.security.Hasher;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
@Stateless
public class InsuranceBean {
    //TODO: Change when MOCKAPI is ready

    @PersistenceContext
    private EntityManager em;

    public void create(String insuranceCode, Company company, String name) {
        var insurance = new Insurance(insuranceCode, company, name);
        em.persist(insurance);
    }
}
