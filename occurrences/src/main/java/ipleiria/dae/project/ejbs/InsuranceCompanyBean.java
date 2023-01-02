package ipleiria.dae.project.ejbs;

import ipleiria.dae.project.entities.InsuranceCompany;
import ipleiria.dae.project.entities.Repairer;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class InsuranceCompanyBean {
    @PersistenceContext
    EntityManager em;

    public List<InsuranceCompany> getAllInsuranceCompanies() {
        return (List<InsuranceCompany>) em.createNamedQuery("getAllInsuranceCompanies").getResultList();
    }

    public InsuranceCompany create(String name) {
        InsuranceCompany insuranceCompany = find(name);
        if (insuranceCompany != null){
            return null;
        }
        insuranceCompany = new InsuranceCompany(name);
        em.persist(insuranceCompany);
        return find(name);
    }

    public InsuranceCompany find(String name) {
        return em.find(InsuranceCompany.class, name);
    }
}
