package ca.omny.potent.models;

import java.util.Collection;

public class OmnyRouteConfiguration {
    Collection<OmnyEndpoint> endpoints;

    public Collection<OmnyEndpoint> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(Collection<OmnyEndpoint> endpoints) {
        this.endpoints = endpoints;
    }
}
