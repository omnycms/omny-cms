package ca.omny.documentdb;

import com.google.gson.Gson;
import ca.omny.documentdb.models.PostgresConnection;
import ca.omny.logger.OmnyLogger;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

@ApplicationScoped
public class QuerierFactory {

    Postgres postgres = new Postgres();

    IDocumentQuerier overrideInstance;

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
    
    Gson gson = new Gson();

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
