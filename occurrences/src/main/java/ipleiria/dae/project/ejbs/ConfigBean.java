package ipleiria.dae.project.ejbs;

import ipleiria.dae.project.entities.*;
import ipleiria.dae.project.enumerators.CoverageType;
import ipleiria.dae.project.enumerators.State;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.text.SimpleDateFormat;
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
    AdministratorBean administratorBean;

    @EJB
    RepairerBean repairerBean;

    @PostConstruct
    public void populateDB() {
        System.out.println("Hello Java EE!");

        try {
            //expertBean.create("expert1", "exp", "Expert Joca", "expert@mail.pt", "company1");
            clientBean.create("client2", "client", "Client Isabel", "client2@mail.pt", "Rua dos pinheiros tortos", 912345678, 267503959);
            clientBean.create("client3", "client", "Client Nando", "client3@mail.pt", "Rua dos pinheiros tortos", 912345678,236589547);
            //insuranceBean.create("AL-123", company, "Allianz");
           //expertBean.create("expert2", "exp", "Expert Sofia", "sofia@mail.pt", "Fidelidade");
           repairerBean.create("repairer1", "repairer", "Repairer João", "joao@mail.pt", "Rua dos olivais");
            Occurrence o = occurrenceBean.create("client2", "01/01/2023", State.PENDING, "FIDEL-1298302", CoverageType.ACCIDENTAL_DAMAGE,"lalalalla");
            //occurrenceBean.addExpert(o.getId(),"expert");
            //repairerBean.create("repairer1", "repairer", "Repairer Bob", "bob_o_construtor@mail.pt", "rua dos calduços");
        } catch (Exception exception) {
            logger.severe(exception.getMessage());
        }
    }
}
