package ipleiria.dae.project.ejbs;

import org.jboss.resteasy.spi.HttpRequest;
import org.json.JSONObject;

import javax.ejb.Stateless;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;

@Stateless
public class MockAPIBean {

    public String getAllDataAPI() {
        JSONObject jsonObject = null;
        try {
            var client = HttpClient.newHttpClient();

            var request = java.net.http.HttpRequest.newBuilder()
                    .uri(URI.create("https://63a9db1a594f75dc1dc27d9b.mockapi.io/insurances"))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            int statusCode = response.statusCode();
            String responseBody = response.body();
            System.out.println("RESPONSSSSSSSSSSSSSSSSSSSSSEEEEEEEEEEEEEEE"+ responseBody + " " + statusCode);


            jsonObject = new JSONObject(responseBody);

            return responseBody;

           /* URL url = new URL("https://63a9db1a594f75dc1dc27d9b.mockapi.io/policies");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            if (conn.getResponseCode() < 200 || conn.getResponseCode() > 299) {
                return null;
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

            String output;
            StringBuilder response = new StringBuilder();
            while ((output = br.readLine()) != null) {
                response.append(output);
            }
            System.out.println(response);

            return response.toString();*/


        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getDataAPICode(String code) {
        try {
            URL url = new URL("https://63a9db1a594f75dc1dc27d9b.mockapi.io/insurances/" + code);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            if (conn.getResponseCode() < 200 || conn.getResponseCode() > 299) {
                return null;
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

            String output;
            StringBuilder response = new StringBuilder();
            while ((output = br.readLine()) != null) {
                response.append(output);
            }
            System.out.println(response);

            return response.toString();


        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
