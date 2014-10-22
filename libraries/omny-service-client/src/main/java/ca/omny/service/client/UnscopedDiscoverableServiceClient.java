package ca.omny.service.client;

import ca.omny.omny.service.discovery.FixedServiceLocation;
import javax.enterprise.inject.Alternative;

@Alternative
public class UnscopedDiscoverableServiceClient extends BaseServiceClient {

    public UnscopedDiscoverableServiceClient() {
        super.setLocator(new FixedServiceLocation());
    }
}
