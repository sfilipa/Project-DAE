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
    AdministratorBean administratorBean;

    @PostConstruct
    public void populateDB() {
        System.out.println("Hello Java EE!");
        try {
           // expertBean.create("ivoafonsobispo", "password", "Ivo Afonso Bispo", "ivoafobispo@gmail.com", );
            //studentBean.create("anamartin", "password", "Log of Error", "anamartin@gmail.com", 9119);
        } catch (Exception exception) {
            logger.severe(exception.getMessage());
        }


    }
}
