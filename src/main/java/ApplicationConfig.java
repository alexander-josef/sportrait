import ch.unartig.sportrait.imgRecognition.StartnumberProcessor;
import ch.unartig.studioserver.Registry;
import ch.unartig.studioserver.businesslogic.PhotoOrderService;
import ch.unartig.studioserver.persistence.util.HibernateUtil;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;


import javax.annotation.PreDestroy;
import javax.ws.rs.ApplicationPath;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.GeneralSecurityException;
// import java.security.SecureRandom;


@ApplicationPath("/api/import")
public class ApplicationConfig extends ResourceConfig {
    private StartnumberProcessor startnumberProcessor;


    public ApplicationConfig() {
        packages("com.sportrait.importrs");
        register(MultiPartFeature.class);


        // alternative - probably better - solution would be to register a servletcontextlistener

        // this didn't work as soon as mulitpart request came into play:
        //     public class InitJersey extends Application implements ApplicationEventListener
        // (class "InitJersey" - deleted)
        try {
            // BTW : log4j2 not initialized yet - use stdout
            System.out.println("***** Calling Registry.init() from Jersey Application Config ******");
            Registry.init();
/*          init startnumber processor server here (used to be in the unartig action servlet - needs to be deactived / deleted there)
 */

            startnumberProcessor = new StartnumberProcessor();
            Thread startnumberProcessorServer = new Thread(startnumberProcessor);
            startnumberProcessorServer.start();
            System.out.println("***** Initialized Startnumber Processor from Jersey Application Config ******");
//            TODO : how can one make sure this process / server doesn't die ?

            // from old Struts Actionservlet init:
            // SecureRandom prng = SecureRandom.getInstance("SHA1PRNG");
            // is this somewhere needed?

        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | GeneralSecurityException | IOException | NoSuchMethodException | InvocationTargetException e) {
            System.out.println("**************************");
            System.out.println("****** Exception during Jersey Application Config *******");
            System.out.println("**************************");
            e.printStackTrace();
        }

    }

    /**
     * call shutdown activities before Jersey Servlet is destroyed (on tomcat shutdown)
     */
    @PreDestroy
    public void preDestroy() {
        System.out.println("destroying unartig action servlet!");
        System.out.println("Going to stop order service ......");
        PhotoOrderService.getInstance().stopService();
        System.out.println("..... Order Service stopped!");
        System.out.println("Going to stop startnumber processing server ......");
        if (startnumberProcessor!=null) {
            startnumberProcessor.shutdown();
            System.out.println("..... StartnumberProcessor stopped!");
        } else {
            System.out.println("StartnumberProcessor = NULL !");
            throw new RuntimeException("startnumberProcess not running during shutdown ...");
        }
        System.out.println("Going to stop Hibernate......");
        HibernateUtil.destroy();
        System.out.println("..... Service stopped!");
        System.out.println("Jersey pre-destroy complete  ....");
    }

}

