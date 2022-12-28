package ipleiria.dae.project.ejbs;

import ipleiria.dae.project.entities.Client;
import ipleiria.dae.project.entities.Company;
import ipleiria.dae.project.entities.Insurance;
import ipleiria.dae.project.entities.Occurrence;
import ipleiria.dae.project.enumerators.InsuredAssetType;
import ipleiria.dae.project.enumerators.State;
import org.hibernate.Hibernate;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;

@Stateless
public class CompanyBean {
    @PersistenceContext
    private EntityManager em;

    public Company find(String username) {
        return em.find(Company.class, username);
    }

    public Company findOrFail(String username) {
        Company company = em.getReference(Company.class, username);
        Hibernate.initialize(company);
        return company;
    }

    public List<Company> getAllCompanies() {
        return (List<Company>) em.createNamedQuery("getAllCompanies").getResultList();
    }

    public Company create(String username, String password, String name, String email, String address, long phone, long nipc) {
        Company company = new Company(username, password, name, email, address, phone, nipc);
        em.persist(company);
        return company;
    }
}
