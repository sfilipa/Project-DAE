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
            clientBean.create("client1", "123", "Client 1", "client1@mail.pt", "Rua Client 1", 912345678, 267503959);
            clientBean.create("client2", "123", "Client 2", "client2@mail.pt", "Rua Client 2", 912345678, 236589547);
            expertBean.create("expert1", "123", "Expert 1", "expert1@mail.pt", "Fidelidade");
            expertBean.create("expert2", "123", "Expert 2", "expert2@mail.pt", "Allianz");
            expertBean.create("expert3", "123", "Expert 3", "expert3@mail.pt", "Allianz");
            repairerBean.create("repairer1", "123", "Repairer 1", "repairer@mail.pt", "Rua Repairer 1");
            occurrenceBean.create("client1", "01/01/2023", State.PENDING, "FIDEL-1298302", CoverageType.ACCIDENTAL_DAMAGE, "Occurrence Description");
        } catch (Exception exception) {
            logger.severe(exception.getMessage());
        }
    }
}
