package ca.omny.service.client;

import ca.omny.omny.service.discovery.FixedServiceLocation;

public class UnscopedDiscoverableServiceClient extends BaseServiceClient {

    public UnscopedDiscoverableServiceClient() {
        super.setLocator(new FixedServiceLocation());
    }
}
