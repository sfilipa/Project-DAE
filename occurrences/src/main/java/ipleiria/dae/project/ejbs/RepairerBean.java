package ipleiria.dae.project.ejbs;

import ipleiria.dae.project.entities.Company;
import ipleiria.dae.project.entities.Expert;
import ipleiria.dae.project.entities.Repairer;
import ipleiria.dae.project.security.Hasher;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class RepairerBean {
    @PersistenceContext
    EntityManager em;

    @Inject // import javax.inject.Inject;
    private Hasher hasher;

    public List<Repairer> getAllRepairers() {
        return (List<Repairer>) em.createNamedQuery("getAllRepairers").getResultList();
    }

    public Repairer create(String username, String password, String name, String email, String company_username) {
        Repairer repairer = find(username);
        Company company = em.find(Company.class, company_username);

        if(company == null ){
            return null;
        }

        if (repairer != null){
            return null;
        }
        repairer = new Repairer(username, hasher.hash(password), name, email, company);
        em.persist(repairer);
        company.addRepairer(repairer);
        return find(username);
    }

    public Repairer update(String username, String password, String name, String email, long company_usernmae) {
        Repairer repairer = find(username);
        if (repairer == null){
            return null;
        }
        Company company = em.find(Company.class, company_usernmae);

        if(company == null ){
            return null;
        }
        repairer.setPassword(password);
        repairer.setName(name);
        repairer.setEmail(email);
        repairer.setCompany(company);
        return repairer;
    }

    public Repairer find(String username) {
        return em.find(Repairer.class, username);
    }

    public void delete(String username) {
        Repairer repairer = find(username);
        if (repairer == null){

        }

        Company company = repairer.getCompany();
        company.removeRepairer(repairer);
        em.remove(repairer);
    }

}
