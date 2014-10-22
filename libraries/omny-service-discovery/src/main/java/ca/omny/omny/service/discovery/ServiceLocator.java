package ca.omny.omny.service.discovery;

import java.util.Collection;

public interface ServiceLocator {
    
    /**
     * Returns the base URLs of providers of a service after discovering them by name.
     * @param serviceName The human friendly name of the service
     * @return The base URLs of the service providers for the service requested
     */
    public Collection<String> findService(String serviceName);
}
