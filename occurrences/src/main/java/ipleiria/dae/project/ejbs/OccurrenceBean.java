package ipleiria.dae.project.ejbs;

import ipleiria.dae.project.entities.*;
import ipleiria.dae.project.enumerators.InsuredAssetType;
import ipleiria.dae.project.enumerators.State;
import ipleiria.dae.project.exceptions.MyEntityNotFoundException;
import org.hibernate.Hibernate;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.List;

@Stateless
public class OccurrenceBean {
    @PersistenceContext
    private EntityManager em;

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

    public Occurrence create(String usernameClient, String date, State state, String insuranceCode, String description) throws MyEntityNotFoundException {
        JSONObject jsonObject = getDataAPI(insuranceCode);
        if(jsonObject == null){
            throw new MyEntityNotFoundException("Insurance not found");
        }
        String insuranceCodeAPI = jsonObject.getString("code");
        String insuranceNameAPI = jsonObject.getJSONObject("insurance").getString("name");
        String clientUsernameAPI = jsonObject.getJSONObject("client").getString("username");
        String insuranceTypeAPI = jsonObject.getString("type").toUpperCase();
        String insuranceObjectAPI = jsonObject.getString("object");
        JSONArray insuranceCoversAPI = jsonObject.getJSONArray("covers");

        if (!clientUsernameAPI.equals(usernameClient)){
            throw new MyEntityNotFoundException("Client is not the owner of the insurance");
        }

        Client client = em.find(Client.class, usernameClient);
        InsuredAssetType insuredAssetType = null;
        InsuranceCompany insuranceCompany = new InsuranceCompany(insuranceNameAPI);
        Insurance insurance = new Insurance(insuranceCode, insuranceCompany, insuranceNameAPI);

        try {
            insuredAssetType = InsuredAssetType.valueOf(insuranceTypeAPI);
        }catch (IllegalArgumentException e){
            throw new MyEntityNotFoundException("Insurance type not found");
        }

        Occurrence occurrence = new Occurrence(client, date, state, insuredAssetType, insurance, description, insuranceObjectAPI, null);
        em.persist(occurrence);
        return occurrence;
    }

    public void delete(long id) {
        Occurrence occurrence = find(id);
        em.remove(occurrence);
    }

    public Occurrence update(long id, String usernameClient, String date, State state, String insuranceCode) {//TODO : Exceptions
        Occurrence occurrence = em.find(Occurrence.class, id);
        Client client = em.find(Client.class, usernameClient);
        Insurance insurance = em.find(Insurance.class, insuranceCode);
        occurrence.setClient(client);
        occurrence.setDate(date);
        occurrence.setState(state);
        occurrence.setInsurance(insurance);
        return null;
    }

    public int disapproveOccurrence(long id) {
        Occurrence occurrence = em.find(Occurrence.class, id);
        if (occurrence == null) {
            return -1;
        }
        if(occurrence.getExperts().isEmpty()){
            return -2;
        }
        //TODO: Verificar se o expert logged está na lista de experts da occurrence

        occurrence.setState(State.FAILED);
        return 0;
    }

    public int approveOccurrence(long id) {
        Occurrence occurrence = em.find(Occurrence.class, id);
        if (occurrence == null) {
            return -1;
        }
        if(occurrence.getExperts().isEmpty()){
            return -2;
        }
        //TODO: Verificar se o expert logged está na lista de experts da occurrence
//        if(!occurrence.isExpertInOccurrence(expertLoggedIn)){
//            return -3;
//        }

        occurrence.setState(State.ACTIVE);
        return 0;
    }

    public int addExpert(long id, String username) {
        Occurrence occurrence = em.find(Occurrence.class, id);
        if (occurrence == null) {
            return -1; //devolver exception
        }
        if(occurrence.getState() != State.PENDING) {
            return -2; //devolver exception
        }
        Expert expert = em.find(Expert.class, username);
        if (expert == null) {
            return -3; //devolver exception
        }
        if(occurrence.isExpertInOccurrence(expert)){
            return -4; //devolver exception
        }

        //verificar que o perito é da mesma seguradora
        if(expert.getCompany() != occurrence.getInsurance().getCompany()){
            return -5; //devolver exception
        }

        occurrence.addExpert(expert);
        return 0;
    }

    public int removeExpert(long id, String username){
        Occurrence occurrence = em.find(Occurrence.class, id);
        if (occurrence == null) {
            return -1; //devolver exception
        }
        Expert expert = em.find(Expert.class, username);
        if (expert == null) {
            return -2; //devolver exception
        }
        if(!occurrence.isExpertInOccurrence(expert)){
            return -3; //devolver exception
        }

        occurrence.removeExpert(expert);
        expert.removeOccurrence(occurrence);
        return 0;
    }

    public int assignRepairer(long id, String username) {
        Occurrence occurrence = em.find(Occurrence.class, id);
        if (occurrence == null) {
            return -1; //devolver exception
        }
        if(occurrence.getState() != State.ACTIVE){
            return -2; //devolver exception
        }
        Repairer repairer = em.find(Repairer.class, username);
        if (repairer == null) {
            return -3; //devolver exception
        }

        occurrence.setRepairer(repairer);
        repairer.addOccurrence(occurrence);
        return 0;
    }

    public int unassignRepairer(long id){
        Occurrence occurrence = em.find(Occurrence.class, id);
        if (occurrence == null) {
            return -1; //devolver exception
        }

        Repairer repairer = occurrence.getRepairer();
        if (repairer == null) {
            return -2; //devolver exception
        }

        occurrence.setRepairer(null);
        repairer.removeOccurrence(occurrence);
        return 0;
    }

    public JSONObject getDataAPI(String code) {
        JSONObject jsonObject = null;
        try {
            URL url = new URL("https://63a9db1a594f75dc1dc27d9b.mockapi.io/policies/" + code);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            if (conn.getResponseCode() < 200 || conn.getResponseCode() > 299) {
                // throw an exception or handle the error
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

            String output;
            StringBuilder response = new StringBuilder();
            while ((output = br.readLine()) != null) {
                response.append(output);
            }
            System.out.println(response);

            jsonObject = new JSONObject(response.toString());



        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }


}
