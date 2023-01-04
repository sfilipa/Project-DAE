package ipleiria.dae.project.ejbs;

import ipleiria.dae.project.entities.InsuranceCompany;
import ipleiria.dae.project.entities.Repairer;
import org.glassfish.jaxb.runtime.v2.model.runtime.RuntimeTypeInfoSet;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.LinkedHashSet;
import java.util.List;

@Stateless
public class InsuranceCompanyBean {
    @PersistenceContext
    EntityManager em;

    public List<InsuranceCompany> getAllInsuranceCompanies() {
        return (List<InsuranceCompany>) em.createNamedQuery("getAllInsuranceCompanies").getResultList();
    }

    public InsuranceCompany create(String username, String name, String email, String password) {
        InsuranceCompany insuranceCompany = find(username);
        if (insuranceCompany != null){
            throw new IllegalArgumentException("Insurance Company already exists");
        }
        insuranceCompany = new InsuranceCompany(username, name, email, password);
        em.persist(insuranceCompany);
        return find(name);
    }

    public InsuranceCompany find(String username) {
        return em.find(InsuranceCompany.class, username);
    }

    public InsuranceCompany findOrFail(String username) {
        InsuranceCompany insuranceCompany = em.find(InsuranceCompany.class, username);
        if (insuranceCompany == null){
            throw new IllegalArgumentException("Insurance Company does not exist");
        }
        return insuranceCompany;
    }
}
