package ch.unartig.studioserver.persistence;

import ch.unartig.studioserver.model.Event;
import ch.unartig.studioserver.model.SportsEvent;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

import java.util.Properties;

public class TestHibernateConfiguration {

    static Session session;

    public static void main(String[] args) {
        try
        {


            session = newSessionFactory().getCurrentSession();

            Event event = session.load(SportsEvent.class,1);

            session.getTransaction().commit();
            session.close();
        }
        catch (Throwable t)
        // Rollback
        {
            if (session.getTransaction().isActive())
            {
                System.out.println("Open Transaction: Rolling back!");
                try {
                    session.getTransaction().rollback();
                    System.out.println("Transaction has been rolled back in filter");

                } catch (Throwable e) {
                    System.out.println("Cannot rollback Transaction after exception!");
                    e.printStackTrace();
                }
            }
        }

    }

    protected static SessionFactory newSessionFactory() {
        try {
            Properties properties = new Properties();
            properties.put("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
            //log settings
            properties.put("hibernate.hbm2ddl.auto", "update");
            properties.put("hibernate.show_sql", "true");
            //driver settings
            properties.put("hibernate.connection.driver_class", "org.postgresql.Driver");
            properties.put("hibernate.connection.url", "jdbc:postgresql://localhost:5431/sportrait");
            properties.put("hibernate.connection.username", "sportrait");
            properties.put("hibernate.connection.password", "unartig");

            return new Configuration()
                    .addProperties(properties)
                    .addFile("src/main/resources/hibernate.cfg.xml")
                    .buildSessionFactory(
                            new StandardServiceRegistryBuilder()
                                    .applySettings(properties)
                                    .build()
                    );
        } catch (HibernateException e) {
            e.printStackTrace();
        }

        return null;
    }
}
