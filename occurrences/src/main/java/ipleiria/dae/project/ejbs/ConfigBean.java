package ipleiria.dae.project.ejbs;

import ipleiria.dae.project.entities.*;
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

    @EJB
    InsuranceCompanyBean insuranceCompanyBean;

    @PostConstruct
    public void populateDB() {
        System.out.println("Hello Java EE!");
        try {
            //expertBean.create("expert1", "exp", "Expert Joca", "expert@mail.pt", "company1");
            InsuranceCompany insuranceCompany = insuranceCompanyBean.create("Fidelidade");
            clientBean.create("client2", "client", "Client Isabel", "client2@mail.pt", "Rua dos pinheiros tortos", 912345678);
            clientBean.create("client3", "client", "Client Nando", "client3@mail.pt", "Rua dos pinheiros tortos", 912345678);
            Expert e = expertBean.create("expert2", "exp", "Expert Sofia", "sofia@mail.pt", "Fidelidade");
            System.out.println("CRIOU O EXPERT" + e);
            insuranceBean.create("AL-123",insuranceCompany, "Allianz");
            Occurrence o = occurrenceBean.create("client2", new Date(2022, Calendar.JANUARY,27), InsuredAssetType.VEHICLES, State.PENDING, "AL-123", "ola");
            occurrenceBean.addExpert(o.getId(),"expert");
        } catch (Exception exception) {
            logger.severe(exception.getMessage());
        }
    }
}
