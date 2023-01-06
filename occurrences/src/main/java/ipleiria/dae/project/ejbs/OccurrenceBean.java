package ipleiria.dae.project.ejbs;

import ipleiria.dae.project.entities.*;
import ipleiria.dae.project.enumerators.CoverageType;
import ipleiria.dae.project.enumerators.InsuredAssetType;
import ipleiria.dae.project.enumerators.State;
import ipleiria.dae.project.exceptions.*;
import org.hibernate.Hibernate;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;

@Stateless
public class OccurrenceBean {
    @PersistenceContext
    private EntityManager em;

    @EJB
    private MockAPIBean mockAPIBean;

    public Occurrence find(long id) {
        return em.find(Occurrence.class, id);
    }

    public Occurrence findOrFail(long code) {
        Occurrence occurrence = em.getReference(Occurrence.class, code);
        Hibernate.initialize(occurrence);
        return occurrence;
    }

    public List<Occurrence> getAllOccurrences() {
        return (List<Occurrence>) em.createNamedQuery("getAllOccurrences").getResultList();
    }

    public Occurrence create(String usernameClient, String entryDate, State state, String insuranceCode, CoverageType coverageType, String description) {
        JSONArray jsonArray = mockAPIBean.getDataAPI("insurances", "code", insuranceCode);

        JSONObject jsonObject = jsonArray.getJSONObject(0);
        if (jsonObject == null) {
            throw new MyEntityNotFoundException("Insurance not found");
        }

        long policyNumberAPI = jsonObject.getLong("policyNumber");
        String insuranceCompanyAPI = jsonObject.getString("insuranceCompany");
        long clientNifAPI = jsonObject.getLong("clientNif");
        String clientNameAPI = jsonObject.getString("clientName");
        String initialDateAPI = jsonObject.getString("initialDate");
        String validUntilAPI = jsonObject.getString("validUntil");
        String insuranceTypeAPI = jsonObject.getString("type").toUpperCase();
        String insuranceObjectAPI = jsonObject.getString("object");
        JSONArray insuranceCoversAPI = jsonObject.getJSONArray("covers");

        Client client = em.find(Client.class, usernameClient);
        if (client == null) {
            throw new MyEntityNotFoundException("Client not found");
        }
        if (clientNifAPI != client.getNif_nipc()) {
            throw new NotAuthorizedException("Client is not the owner of the insurance");
        }

        Date initialDate_formatDate = new Date(initialDateAPI);
        Date validUntil_formatDate = new Date(validUntilAPI);
        Date entryDate_formatDate = new Date(entryDate);

        if ((entryDate_formatDate.before(initialDate_formatDate) || entryDate_formatDate.after(validUntil_formatDate))) {
            throw new DateOutsideRangeException("Entry date is not between the initial date and the valid until date");
        }

        InsuredAssetType insuredAssetType = null;
        try {
            insuredAssetType = InsuredAssetType.valueOf(insuranceTypeAPI);
        } catch (IllegalArgumentException e) {
            throw new MyEntityNotFoundException("Insurance type not found");
        }

        Insurance insurance = new Insurance(insuranceCode, policyNumberAPI, insuranceCompanyAPI, clientNifAPI, clientNameAPI, initialDateAPI, validUntilAPI, insuranceObjectAPI, insuredAssetType, description);

        List<CoverageType> covers = CoverageType.getCoverageTypeList(insuranceCoversAPI);
        insurance.setCovers(covers);

        if (!covers.contains(coverageType)) {
            throw new MyIllegalArgumentException("The insurance does not cover this type of coverage");
        }

        description = "[" + usernameClient + "]: " + description;
        Occurrence occurrence = new Occurrence(entryDate, insuranceObjectAPI, description, insurance, coverageType, state, client);
        client.addOccurrence(occurrence);
        em.persist(occurrence);
        return occurrence;
    }

    public void delete(long id) throws MyEntityNotFoundException {
        Occurrence occurrence = find(id);
        if (occurrence == null) {
            throw new MyEntityNotFoundException("Occurrence not found");
        }
        occurrence.getClient().removeOccurrence(occurrence);
        em.remove(occurrence);
    }

    public Occurrence update(long id, String usernameClient, String entryDate, State state, String insuranceCode, CoverageType coverageType, String description) {
        Occurrence occurrence = em.find(Occurrence.class, id);
        if (occurrence == null) {
            throw new MyEntityNotFoundException("Occurrence not found");
        }

        JSONArray jsonArray = mockAPIBean.getDataAPI("insurances", "code", insuranceCode);
        JSONObject jsonObject = jsonArray.getJSONObject(0);

        if (jsonObject == null) {
            throw new MyEntityNotFoundException("Insurance not found");
        }

        long policyNumberAPI = jsonObject.getLong("policyNumber");
        String insuranceCompanyAPI = jsonObject.getString("insuranceCompany");
        long clientNifAPI = jsonObject.getLong("clientNif");
        String clientNameAPI = jsonObject.getString("clientName");
        String initialDateAPI = jsonObject.getString("initialDate");
        String validUntilAPI = jsonObject.getString("validUntil");
        String insuranceTypeAPI = jsonObject.getString("type").toUpperCase();
        String insuranceObjectAPI = jsonObject.getString("object");
        JSONArray insuranceCoversAPI = jsonObject.getJSONArray("covers");

        if (clientNifAPI != occurrence.getClient().getNif_nipc()) {
            occurrence.getClient().removeOccurrence(occurrence);

            Client client = em.find(Client.class, usernameClient);
            if (client == null) {
                throw new MyEntityNotFoundException("Client not found");
            }
            if (clientNifAPI != client.getNif_nipc()) {
                throw new NotAuthorizedException("Client is not the owner of the insurance");
            }

            occurrence.setClient(client);
            client.addOccurrence(occurrence);
        }
        InsuredAssetType insuredAssetType = null;
        Insurance insurance = new Insurance(insuranceCode, policyNumberAPI, insuranceCompanyAPI, clientNifAPI, clientNameAPI, initialDateAPI, validUntilAPI, insuranceObjectAPI, insuredAssetType, description);

        List<CoverageType> covers = CoverageType.getCoverageTypeList(insuranceCoversAPI);
        insurance.setCovers(covers);

        if (!covers.contains(coverageType)) {
            throw new MyIllegalArgumentException("The insurance does not cover this type of coverage");
        }

        try {
            insuredAssetType = InsuredAssetType.valueOf(insuranceTypeAPI);
        } catch (IllegalArgumentException e) {
            throw new MyEntityNotFoundException("Insurance type not found");
        }

        Date initialDate_formatDate = new Date(initialDateAPI);
        Date validUntil_formatDate = new Date(validUntilAPI);
        Date entryDate_formatDate = new Date(entryDate);

        if ((entryDate_formatDate.before(initialDate_formatDate) || entryDate_formatDate.after(validUntil_formatDate))) {
            throw new DateOutsideRangeException("Entry date is not between the initial date and the valid until date");
        }
        occurrence.setEntryDate(entryDate);
        occurrence.setState(state);
        occurrence.setInsurance(insurance);
        occurrence.setDescription(description);
        occurrence.setObjectInsured(insuranceObjectAPI);
        occurrence.setCoverageType(coverageType);

        return occurrence;
    }
}
