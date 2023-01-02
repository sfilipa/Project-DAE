package ipleiria.dae.project.ejbs;

import ipleiria.dae.project.entities.Client;
import ipleiria.dae.project.entities.Policy;
import ipleiria.dae.project.enumerators.InsuredAssetType;
import org.hibernate.Hibernate;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class PolicyBean {
    @PersistenceContext
    private EntityManager em;

    public Policy find(String code) {
        return em.find(Policy.class, code);
    }

    public Policy findOrFail(String code) {
        Policy policy = em.getReference(Policy.class, code);
        Hibernate.initialize(policy);
        return policy;
    }

    public List<Policy> getAllPolicies() {
        return (List<Policy>) em.createNamedQuery("getAllPolicies").getResultList();
    }

    public Policy create(String code, String type, String client_username, List<InsuredAssetType> covers) {
        // Verifies if the client exists
        Client client = em.find(Client.class, client_username);
        if (client == null) {
            // Throws an exception if the client does not exist
            throw new IllegalArgumentException("Client does not exist");
        }

        // Creates the policy
        Policy policy = new Policy(code, type, client, covers);
        em.persist(policy);
        try {
            client.add(policy);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        return find(policy.getCode());
    }

    // Delete Policy
    public void delete(String code) {
        // Verifies if the policy exists
        Policy policy = findOrFail(code);
        if (policy == null) {
            throw new IllegalArgumentException("Policy does not exist");
        }

        // Remove the policy from the client
        Client client = policy.getClient();
        try {
            client.remove(policy);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }

        // Remove the policy from the database
        em.remove(policy);
    }

    // Update Policy
    public Policy update(String code, String type, String client_username, List<InsuredAssetType> covers) {
        // Verifies if the policy exists
        Policy policy = findOrFail(code);
        if (policy == null) {
            throw new IllegalArgumentException("Policy does not exist");
        }

        // Verifies if the client exists
        Client client = em.find(Client.class, client_username);
        if (client == null) {
            // Throws an exception if the client does not exist
            throw new IllegalArgumentException("Client does not exist");
        }

        // Update the policy
        try {
            policy.update(type, client, covers);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        em.merge(policy);
        return find(policy.getCode());
    }

    // Add Insured Asset Type
    public void addInsuredAssetType(String code, InsuredAssetType insuredAssetType) {
        // Verifies if the policy exists
        Policy policy = findOrFail(code);
        if (policy == null) {
            throw new IllegalArgumentException("Policy does not exist");
        }

        // Add the insured asset type to the policy
        try {
            policy.add(insuredAssetType);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }

        em.merge(policy);
    }

    // Remove Insured Asset Type
    public void removeInsuredAssetType(String code, InsuredAssetType insuredAssetType) {
        // Verifies if the policy exists
        Policy policy = findOrFail(code);
        if (policy == null) {
            throw new IllegalArgumentException("Policy does not exist");
        }

        // Remove the insured asset type from the policy
        try {
            policy.remove(insuredAssetType);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        em.merge(policy);
    }
}
