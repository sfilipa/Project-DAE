package ipleiria.dae.project.ejbs;

import ipleiria.dae.project.exceptions.MyEntityNotFoundException;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ejb.Stateless;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Stateless
public class MockAPIBean {

    public JSONArray getDataAPI(String resource, String code) throws MyEntityNotFoundException {
        JSONArray jsonArray = new JSONArray();
        try {
            URL url = null;
            if (!code.trim().isEmpty()) {
                url = new URL("https://63a9db1a594f75dc1dc27d9b.mockapi.io/" + resource + "?code=" + code);
                System.out.println("ENTROUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUU"+url);
            } else {
                url = new URL("https://63a9db1a594f75dc1dc27d9b.mockapi.io/" + resource);
            }

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            if (conn.getResponseCode() < 200 || conn.getResponseCode() > 299) {
                throw new MyEntityNotFoundException("Erro da API");//TODO: MUDAR ESTAS EXCEÇOES
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
