package ipleiria.dae.project.ejbs;

import ipleiria.dae.project.entities.Administrator;
import ipleiria.dae.project.entities.Insurance;
import ipleiria.dae.project.enumerators.CoverageType;
import ipleiria.dae.project.enumerators.InsuredAssetType;
import ipleiria.dae.project.exceptions.MyEntityNotFoundException;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ejb.Stateless;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

@Stateless
public class MockAPIBean {

    public JSONArray getDataAPI(String resource, String attribute, String attributeToGet) throws MyEntityNotFoundException {
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
                throw new MyEntityNotFoundException("Couldn't get data from API");//TODO: MUDAR ESTAS EXCEÇOES
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

    public static Administrator getAdministrator(String username) throws MyEntityNotFoundException {
        try {
            // Receive Insurance Company in a JSONArray format
            JSONArray jsonArray = get("administrators", "username", username);
            if (jsonArray.length() == 0) {
                throw new MyEntityNotFoundException("Administrator " + username + " not found");
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
        } catch (MyEntityNotFoundException e) {
            throw new MyEntityNotFoundException(e.getMessage());
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
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

    public static List<Insurance> getInsurances(long nifNipc) {
        try {
            // Create Insurance List
            List<Insurance> insurances = new LinkedList<>();

            // Receive Insurance in a JSONArray format
            JSONArray jsonArray = get("insurances", "clientNif", String.valueOf(nifNipc));
            if (jsonArray.length() == 0) {
                throw new IllegalArgumentException("No insurances found");
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
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public static JSONArray get(String resource, String attribute, String name) {
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
            throw new IllegalArgumentException("Couldn't get data from API");
        }
    }
}
