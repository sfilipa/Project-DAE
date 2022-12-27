package ipleiria.dae.project.ejbs;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
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
    AdministratorBean administratorBean;

    @PostConstruct
    public void populateDB() {
        System.out.println("Hello Java EE!");
        try {
            //expertBean.create("expert1", "exp", "Expert Joca", "expert@mail.pt", "company1");
            clientBean.create("client2", "client", "Client Isabel", "client2@mail.pt", "Rua dos pinheiros tortos", 912345678);
        } catch (Exception exception) {
            logger.severe(exception.getMessage());
        }
    }
}
