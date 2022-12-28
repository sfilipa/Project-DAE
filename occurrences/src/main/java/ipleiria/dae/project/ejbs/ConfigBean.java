package ipleiria.dae.project.ejbs;

import ipleiria.dae.project.dtos.OccurrenceDTO;
import ipleiria.dae.project.entities.Company;
import ipleiria.dae.project.entities.Insurance;
import ipleiria.dae.project.enumerators.InsuredAssetType;
import ipleiria.dae.project.enumerators.State;
import ipleiria.dae.project.security.Authenticated;
import org.jboss.resteasy.spi.HttpRequest;

import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.http.HttpResponse;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

@Startup
@Singleton
public class ConfigBean {

    private static final Logger logger = Logger.getLogger("ejbs.ConfigBean");
    @EJB
    ExpertBean expertBean;
    @EJB
    ClientBean clientBean;
    @EJB
    OccurrenceBean occurrenceBean;
    @EJB
    InsuranceBean insuranceBean;
    @EJB
    AdministratorBean administratorBean;

    @PostConstruct
    public void populateDB() {
        System.out.println("Hello Java EE!");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String dateInString = sdf.format(new Date());
        try {
           // getDataAPI();
            //expertBean.create("expert1", "exp", "Expert Joca", "expert@mail.pt", "company1");
            clientBean.create("client2", "client", "Client Isabel", "client2@mail.pt", "Rua dos pinheiros tortos", 912345678);
            clientBean.create("client3", "client", "Client Nando", "client3@mail.pt", "Rua dos pinheiros tortos", 912345678);
            //insuranceBean.create("AL-123", company, "Allianz");
            occurrenceBean.create("client2", dateInString, State.PENDING, "FIDEL-1298302", "ola");
        } catch (Exception exception) {
            logger.severe(exception.getMessage());
        }
    }

    public void getDataAPI() {
        try {
            URL url = new URL("https://63a9db1a594f75dc1dc27d9b.mockapi.io/policies");
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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
