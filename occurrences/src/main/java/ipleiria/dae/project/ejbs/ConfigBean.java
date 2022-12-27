package ipleiria.dae.project.ejbs;

import ipleiria.dae.project.entities.Company;
import ipleiria.dae.project.entities.Insurance;
import ipleiria.dae.project.enumerators.InsuredAssetType;
import ipleiria.dae.project.enumerators.State;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
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
        try {
            Company company = new Company();
            //expertBean.create("expert1", "exp", "Expert Joca", "expert@mail.pt", "company1");
            clientBean.create("client2", "client", "Client Isabel", "client2@mail.pt", "Rua dos pinheiros tortos", 912345678);
            clientBean.create("client3", "client", "Client Nando", "client3@mail.pt", "Rua dos pinheiros tortos", 912345678);
            insuranceBean.create("AL-123",company, "Allianz");
            occurrenceBean.create("client2", new Date(2022, Calendar.JANUARY,27), InsuredAssetType.VEHICLES, State.PENDING, "AL-123", "ola");
        } catch (Exception exception) {
            logger.severe(exception.getMessage());
        }
    }
}
