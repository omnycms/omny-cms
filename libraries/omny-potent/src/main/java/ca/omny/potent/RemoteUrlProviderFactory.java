package ca.omny.potent;

import ca.omny.configuration.ConfigurationReader;
import ca.omny.db.IDocumentQuerier;
import ca.omny.extension.proxy.IRemoteUrlProvider;
import ca.omny.extension.proxy.IRemoteUrlProviderFactory;
import ca.omny.potent.mappers.OmnyRouteMapper;
import java.util.Collection;
import java.util.LinkedList;

public class RemoteUrlProviderFactory {
    
    static ConfigurationReader configurationReader = ConfigurationReader.getDefaultConfigurationReader();
    static IDocumentQuerier db;
    static OmnyRouteMapper routeMapper = new OmnyRouteMapper();
    
    static IRemoteUrlProvider defaultRemoteUrlProvider;
    static Collection<IRemoteUrlProviderFactory> remoteUrlProviderFactories = new LinkedList<>();

    public RemoteUrlProviderFactory(IDocumentQuerier querier) {
        db = querier;
    }
    
    public static void addRemoteUrlProviderFactory(IRemoteUrlProviderFactory remoteUrlProviderFactory) {
        remoteUrlProviderFactories.add(remoteUrlProviderFactory);
    }
    
    static IRemoteUrlProvider getDefaultProvider() {
        if(defaultRemoteUrlProvider==null) {
            defaultRemoteUrlProvider = getProvider(remoteUrlProviderFactories);
        }
        return defaultRemoteUrlProvider;
    }
    
    public static IRemoteUrlProvider getProvider(Collection<IRemoteUrlProviderFactory> remoteUrlProviderFactories) {
        String name = configurationReader.getSimpleConfigurationString("OMNY_REMOTE_URL_PROVIDER");
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
        return new RemoteUrlProvider(db);
    }
}
