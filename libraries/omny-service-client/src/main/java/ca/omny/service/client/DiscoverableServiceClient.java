package ca.omny.service.client;

import ca.omny.omny.service.discovery.FixedServiceLocation;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class DiscoverableServiceClient extends BaseServiceClient {
    
    @Inject
    FixedServiceLocation serviceLocation;
    
    public DiscoverableServiceClient() {
        if(serviceLocation==null) {
            serviceLocation = new FixedServiceLocation();
        }
        super.setLocator(serviceLocation);
    }
}
