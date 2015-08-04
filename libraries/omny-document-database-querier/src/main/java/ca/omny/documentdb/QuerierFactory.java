package ca.omny.documentdb;

import com.google.gson.Gson;
import ca.omny.documentdb.models.PostgresConnection;
import ca.omny.logger.OmnyLogger;
import ca.omny.logger.SimpleLogger;
import java.util.Collection;
import java.util.LinkedList;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

@ApplicationScoped
public class QuerierFactory {

    Postgres postgres = new Postgres();

    IDocumentQuerier overrideInstance;
    
    static Collection<IDocumentQuerierFactory> querierFactories = new LinkedList<IDocumentQuerierFactory>();
    
    static IDocumentQuerier defaultQuerier;
    static OmnyLogger logger = new SimpleLogger(QuerierFactory.class.getName());     
    static Gson gson = new Gson();
            
    public static void registerFactory(IDocumentQuerierFactory factory) {
        querierFactories.add(factory);
    }

    public static IDocumentQuerier getDefaultQuerier() {
        if(defaultQuerier==null) {
            for (IDocumentQuerierFactory factory : querierFactories) {
                defaultQuerier = factory.getInstance();
                if(defaultQuerier!=null) {
                    break;
                }
            }
            if(defaultQuerier==null) {
                Postgres postgres = new Postgres();

                String postgresConfig = System.getenv("postgres");
                if (postgresConfig != null) {
                    postgres.setConnectionInfo(gson.fromJson(postgresConfig, PostgresConnection.class));
                    postgres.setLogger(logger);
                    defaultQuerier = postgres;
                } else {
                    defaultQuerier = CouchFactory.getQuerier();
                }

            }
        }
        return defaultQuerier;
    }

    @Inject
    public QuerierFactory(OmnyLogger logger, Instance<IDocumentQuerierFactory> factories) {
        String postgresConfig = System.getenv("postgres");
        if (postgresConfig != null) {
            postgres.setConnectionInfo(gson.fromJson(postgresConfig, PostgresConnection.class));
            postgres.setLogger(logger);
        }
        for (IDocumentQuerierFactory factory : factories) {
            overrideInstance = factory.getInstance();
            if (overrideInstance != null) {
                break;
            }
        }
    }

    public void setOverrideInstance(IDocumentQuerier overrideInstance) {
        this.overrideInstance = overrideInstance;
    }


    @Produces
    public IDocumentQuerier getDocumentQuerier() {
        if (overrideInstance != null) {
            return overrideInstance;
        }
        if (postgres.getConnectionInfo() != null) {
            return postgres;
        }

        return CouchFactory.getQuerier();

    }
}
