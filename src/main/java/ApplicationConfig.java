import ch.unartig.sportrait.imgRecognition.StartnumberProcessor;
import ch.unartig.studioserver.Registry;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;


import javax.ws.rs.ApplicationPath;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.GeneralSecurityException;


@ApplicationPath("/api/import")
public class ApplicationConfig extends ResourceConfig {

    public ApplicationConfig() {
        packages("com.sportrait.importrs");
        register(MultiPartFeature.class);


        // alternative - probably better - solution would be to register a servletcontextlistener

        // this didn't work as soon as mulitpart request came into play:
        //     public class InitJersey extends Application implements ApplicationEventListener
        // (class "InitJersey" - deleted)
        try {
            System.out.println("***** Calling Registry.init() from Jersey Application Config ******");
            Registry.init();
/*          init startnumber processor server here (used to be in the unartig action servlet - needs to be deactived / deleted there)

            startnumberProcessor = new StartnumberProcessor();
            startnumberProcessorServer = new Thread(startnumberProcessor);
            startnumberProcessorServer.start();
            logger.info("Startnumber Processor started up");
*/
//            TODO : how can one make sure this process / server doesn't die ?

        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | GeneralSecurityException | IOException | NoSuchMethodException | InvocationTargetException e) {
            System.out.println("**************************");
            System.out.println("****** Exception during Jersey Application Config *******");
            System.out.println("**************************");
            e.printStackTrace();
        }

    }

}

