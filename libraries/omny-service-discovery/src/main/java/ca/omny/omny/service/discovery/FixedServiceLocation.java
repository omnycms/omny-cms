package ca.omny.omny.service.discovery;

import com.google.gson.Gson;
import ca.omny.configuration.ConfigurationReader;
import ca.omny.omny.service.discovery.models.WebServerInfo;
import java.net.URI;
import java.util.Collection;
import java.util.LinkedList;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.UriBuilder;

public class FixedServiceLocation implements ServiceLocator {

    WebServerInfo serverInfo;
    
    @Inject
    ConfigurationReader configurationReader;

    public void setConfigurationReader(ConfigurationReader configurationReader) {
        this.configurationReader = configurationReader;
    }

    public void setServerInfo(WebServerInfo serverInfo) {
        this.serverInfo = serverInfo;
        
    }

    public Collection<String> findService(String serviceName) {
        Client client = ClientBuilder.newClient();
        Gson g = new Gson();
        if (serverInfo == null) {
            String configurationString = configurationReader.getConfigurationString("service-location");
            this.serverInfo = g.fromJson(configurationString, WebServerInfo.class);
        }
        UriBuilder builder = UriBuilder.fromUri(serverInfo.getProtocol() + "://" + serverInfo.getHostname());
        URI uri = builder.port(serverInfo.getPort()).build();

        Collection<String> serviceUrls = new LinkedList<String>();

        serviceUrls.add(uri.toString());
        
        return serviceUrls;

    }
}
