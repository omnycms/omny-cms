package ca.omny.service.client;

import ca.omny.omny.service.discovery.FixedServiceLocation;

public class DiscoverableServiceClient extends BaseServiceClient {
    
    FixedServiceLocation serviceLocation;
    
    public DiscoverableServiceClient() {
        if(serviceLocation==null) {
            serviceLocation = new FixedServiceLocation();
        }
        super.setLocator(serviceLocation);
    }
}
