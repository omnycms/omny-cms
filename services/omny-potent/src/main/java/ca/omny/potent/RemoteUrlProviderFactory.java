package ca.omny.potent;

import ca.omny.configuration.ConfigurationReader;
import ca.omny.documentdb.IDocumentQuerier;
import ca.omny.extension.proxy.IRemoteUrlProvider;
import ca.omny.extension.proxy.IRemoteUrlProviderFactory;
import ca.omny.potent.mappers.OmnyRouteMapper;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

@ApplicationScoped
public class RemoteUrlProviderFactory {
    
    @Inject
    ConfigurationReader configurationReader;
    
    @Inject
    IDocumentQuerier db;
    
    @Inject
    OmnyRouteMapper routeMapper;
      
    @Produces
    public IRemoteUrlProvider getProvider(@Any Instance<IRemoteUrlProviderFactory> remoteUrlProviderFactories, InjectionPoint ip) {
        String name = System.getenv("OMNY_REMOTE_URL_PROVIDER");
        if(name==null) {
            RemoteUrlProvider remoteProvider = new RemoteUrlProvider(configurationReader, db, routeMapper);
            return remoteProvider;
        }

        for(IRemoteUrlProviderFactory factory: remoteUrlProviderFactories) {
            IRemoteUrlProvider instance = factory.getInstance(name);
            if(instance!=null) {
                return instance;
            }
        }
        return null;
    }
}
