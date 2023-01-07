package ipleiria.dae.project.ejbs;

import ipleiria.dae.project.entities.Administrator;
import ipleiria.dae.project.entities.Insurance;
import ipleiria.dae.project.entities.Repairer;
import ipleiria.dae.project.enumerators.CoverageType;
import ipleiria.dae.project.enumerators.InsuredAssetType;
import ipleiria.dae.project.exceptions.APIBadResponseException;
import ipleiria.dae.project.exceptions.MyEntityNotFoundException;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

@Stateless
public class MockAPIBean {

    @EJB
    private RepairerBean repairerBean;

    public JSONArray getDataAPI(String resource, String attribute, String attributeToGet) {
        JSONArray jsonArray = new JSONArray();
        try {
            URL url = null;
            if (!attributeToGet.trim().isEmpty() && !attribute.trim().isEmpty()) {
                url = new URL("https://63a9db1a594f75dc1dc27d9b.mockapi.io/" + resource + "?" + attribute + "=" + attributeToGet);
            } else {
                url = new URL("https://63a9db1a594f75dc1dc27d9b.mockapi.io/" + resource);
            }

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            if (conn.getResponseCode() < 200 || conn.getResponseCode() > 299) {
                throw new APIBadResponseException("Couldn't get data from API");
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

            String output;
            StringBuilder response = new StringBuilder();
            while ((output = br.readLine()) != null) {
                response.append(output);
            }
            jsonArray = new JSONArray(response.toString());

            return jsonArray;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    public List<String> getAttributeFromSpecificInsuranceCompany(String resource, String attribute, String attributeToGet, String attributeArrayInApiName) {
        JSONArray jsonArray = getDataAPI(resource, attribute, attributeToGet);
        /**
         * Example: we got this object from API (attribute = name; attributeToGet = "Allianz"; attributeArrayInApiName = "repairers"):
         * {
         *     "name": "Allianz",
         *     "repairers": [ "repairer1", "repairer2" ]
         * }
         */

        JSONObject jsonObject = jsonArray.getJSONObject(0);
        if (jsonObject == null) {
            throw new MyEntityNotFoundException("Insurance not found");
        }

         /**
         * Now we only want to get "repaired1" and "repairer2" values in a json array
         */

        JSONArray attributeArrayInApi = jsonObject.getJSONArray(attributeArrayInApiName);

        /**
         * Now we want to check if "repairer1" and "repairer2" are in our database
         * */

        return parseAPIObjectsToMatchDatabaseObjects(attributeArrayInApiName, attributeArrayInApi);
    }

    private List<String> parseAPIObjectsToMatchDatabaseObjects(String objectName, JSONArray apiObjects) {
        List<String> matchedStrings = new LinkedList<>();
        if (objectName.equals("repairers")) {
            //Check if repairer from insurance company exists in our database
            List<Repairer> repairersInDB = repairerBean.getAllRepairers();
            for (int i = 0; i < apiObjects.length(); i++) {
                String repairerStringInAPI = apiObjects.getString(i);
                for (Repairer repairer : repairersInDB) {
                    if (repairer.getUsername().equals(repairerStringInAPI)) {
                        matchedStrings.add(repairerStringInAPI);
                    }
                }
            }
        }
        return matchedStrings;
    }

    public static Administrator getAdministrator(String username) throws MyEntityNotFoundException, APIBadResponseException {
        // Receive Administrator in a JSONArray format
        JSONArray jsonArray = get("administrators", "username", username);
        if (jsonArray.length() == 0) {
            throw new MyEntityNotFoundException("Administrator " + username + " not found");
        }

        // If there is more than one administrator with the same username
        if (jsonArray.length() != 1) {
            throw new APIBadResponseException("More than one administrator with the same username");
        }

        // Get the first element of the JSONArray
        JSONObject jsonObject = jsonArray.getJSONObject(0);

        // Create an Administrator Entity
        return new Administrator(
                jsonObject.getString("username"),
                jsonObject.getString("password"),
                jsonObject.getString("name"),
                jsonObject.getString("email")
        );
    }

    public static String getInsuranceCompany(String companyUsername) {
        try {
            // Receive Insurance Company in a JSONArray format
            JSONArray jsonArray = get("insuranceCompanies", "name", companyUsername);

            // Get the first element of the JSONArray
            JSONObject jsonObject = jsonArray.getJSONObject(0);

            // Return the name of the Insurance Company
            return jsonObject.getString("name");
        } catch (Exception e) {
            throw new IllegalArgumentException("Insurance Company not found");
        }
    }

    public static List<Insurance> getInsurances(long nifNipc) throws APIBadResponseException {
        try {
            // Create Insurance List
            List<Insurance> insurances = new LinkedList<>();

            // Receive Insurance in a JSONArray format
            JSONArray jsonArray = get("insurances", "clientNif", String.valueOf(nifNipc));
            if (jsonArray.length() == 0) {
                return insurances;
            }

            // For each JSONObject in the JSONArray add to the Insurance List
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                // Set Insurance Asset Type
                InsuredAssetType insuredAssetTypeName = InsuredAssetType.valueOf(jsonObject.getString("insuredAssetType"));

                // Set Insurance Covers
                List<CoverageType> covers = new LinkedList<>();
                JSONArray insuranceCoversAPI = jsonObject.getJSONArray("covers");
                for (int j = 0; j < insuranceCoversAPI.length(); j++) {
                    covers.add(CoverageType.valueOf(insuranceCoversAPI.getString(j)));
                }

                // Add Insurance to the List
                insurances.add(new Insurance(
                        jsonObject.getString("code"),
                        jsonObject.getLong("policyNumber"),
                        jsonObject.getString("insuranceCompany"),
                        jsonObject.getLong("clientNif"),
                        jsonObject.getString("clientName"),
                        jsonObject.getString("initialDate"),
                        jsonObject.getString("validUntil"),
                        jsonObject.getString("object"),
                        insuredAssetTypeName,
                        jsonObject.getString("description"),
                        covers
                ));
            }

            // Return the Insurance List
            return insurances;
        } catch (APIBadResponseException e) {
            throw new APIBadResponseException("Couldn't get insurances from API");
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public static JSONArray get(String resource, String attribute, String name) throws APIBadResponseException {
        try {
            // Set up the API endpoint URL
            URL url = new URL("https://63a9db1a594f75dc1dc27d9b.mockapi.io/" + resource + "?" + attribute + "=" + name);

            // Open a connection to the API endpoint
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set the request method to GET
            connection.setRequestMethod("GET");

            // Set the request properties
            connection.setRequestProperty("Content-Type", "application/json");

            // Send the request to the API server
            connection.connect();

            // Read the response from the API server
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Transform the response into a String
            String jsonString = response.toString();

            // Transform response String to JSONArray
            return new JSONArray(jsonString);

        } catch (Exception e) {
            throw new APIBadResponseException("Couldn't get data from API");
        }
    }
}
