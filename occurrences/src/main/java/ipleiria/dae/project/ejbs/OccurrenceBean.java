package ipleiria.dae.project.ejbs;

import ipleiria.dae.project.entities.*;
import ipleiria.dae.project.enumerators.CoverageType;
import ipleiria.dae.project.enumerators.InsuredAssetType;
import ipleiria.dae.project.enumerators.State;
import ipleiria.dae.project.exceptions.MyEntityNotFoundException;
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

    public Occurrence create(String usernameClient, String entryDate, State state, String insuranceCode, String description) throws MyEntityNotFoundException {
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

        if (clientNifAPI != client.getNif_nipc()) {
            throw new MyEntityNotFoundException("Client is not the owner of the insurance");
        }

        Date initialDate_formatDate = new Date(initialDateAPI);
        Date validUntil_formatDate = new Date(validUntilAPI);
        Date entryDate_formatDate = new Date(entryDate);

        if ((entryDate_formatDate.before(initialDate_formatDate) || entryDate_formatDate.after(validUntil_formatDate))) {
            throw new MyEntityNotFoundException("Entry date is not between the initial date and the valid until date");
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

        Occurrence occurrence = new Occurrence(entryDate, insuranceObjectAPI, description, insurance, state, client);
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

    public Occurrence update(long id, String usernameClient, String entryDate, State state, String insuranceCode, String description) throws MyEntityNotFoundException {//TODO : Exceptions
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

            if (clientNifAPI != client.getNif_nipc()) {
                throw new MyEntityNotFoundException("Client is not the owner of the insurance");
            }

            occurrence.setClient(client);
            client.addOccurrence(occurrence);
        }
        InsuredAssetType insuredAssetType = null;
        Insurance insurance = new Insurance(insuranceCode, policyNumberAPI, insuranceCompanyAPI, clientNifAPI, clientNameAPI, initialDateAPI, validUntilAPI, insuranceObjectAPI, insuredAssetType, description);

        List<CoverageType> covers = CoverageType.getCoverageTypeList(insuranceCoversAPI);
        insurance.setCovers(covers);

        try {
            insuredAssetType = InsuredAssetType.valueOf(insuranceTypeAPI);
        } catch (IllegalArgumentException e) {
            throw new MyEntityNotFoundException("Insurance type not found");
        }
        occurrence.setEntryDate(entryDate);
        occurrence.setState(state);
        occurrence.setInsurance(insurance);
        occurrence.setDescription(description);
        occurrence.setObjectInsured(insuranceObjectAPI);

        return occurrence;
    }

//    public int addExpert(long id, String username) {
//        Occurrence occurrence = em.find(Occurrence.class, id);
//        if (occurrence == null) {
//            return -1; //devolver exception
//        }
//        if(occurrence.getState() != State.PENDING) {
//            return -2; //devolver exception
//        }
//        Expert expert = em.find(Expert.class, username);
//        if (expert == null) {
//            return -3; //devolver exception
//        }
//        if(occurrence.isExpertInOccurrence(expert)){
//            return -4; //devolver exception
//        }
//
//        //verificar que o perito é da mesma seguradora
//        if(expert.getCompany() != occurrence.getInsurance().getCompany()){
//            return -5; //devolver exception
//        }
//
//        occurrence.addExpert(expert);
//        return 0;
//    }
//
//    public int removeExpert(long id, String username){
//        Occurrence occurrence = em.find(Occurrence.class, id);
//        if (occurrence == null) {
//            return -1; //devolver exception
//        }
//        Expert expert = em.find(Expert.class, username);
//        if (expert == null) {
//            return -2; //devolver exception
//        }
//        if(!occurrence.isExpertInOccurrence(expert)){
//            return -3; //devolver exception
//        }
//
//        occurrence.removeExpert(expert);
//        expert.removeOccurrence(occurrence);
//        return 0;
//    }

    public int assignRepairer(long id, String username) {
        Occurrence occurrence = em.find(Occurrence.class, id);
        if (occurrence == null) {
            return -1; //devolver exception
        }
        if (occurrence.getState() != State.APPROVED) {
            return -2; //devolver exception
        }
        Repairer repairer = em.find(Repairer.class, username);
        if (repairer == null) {
            return -3; //devolver exception
        }

        occurrence.setState(State.WAITING_FOR_APPROVAL_OF_REPAIRER_BY_EXPERT);
        occurrence.setRepairer(repairer);
        repairer.addOccurrence(occurrence);
        return 0;
    }

    public int unassignRepairer(long id) {
        Occurrence occurrence = em.find(Occurrence.class, id);
        if (occurrence == null) {
            return -1; //devolver exception
        }

        Repairer repairer = occurrence.getRepairer();
        if (repairer == null) {
            return -2; //devolver exception
        }

        if (occurrence.getState() != State.REPAIRER_WAITING_LIST && occurrence.getState() != State.WAITING_FOR_APPROVAL_OF_REPAIRER_BY_EXPERT) {
            return -3; //devolver exception (pois se estiver no .ACTIVE, não posso fazer unassign de um repairer que já está mesmo a reparar)
        }

        occurrence.setState(State.APPROVED); //volta para o estado anterior
        occurrence.setRepairer(null);
        repairer.removeOccurrence(occurrence);
        return 0;
    }
}
