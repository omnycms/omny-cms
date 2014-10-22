package ca.omny.omny.service.discovery.models;

import java.util.Collection;


public class WebServerInfo {
    /**
     * The hostname of the web server or load balancer.
     * Will be used as part of the URL or as the host header when using IPs 
     */
    String hostname;
    
    String protocol;
    
    String path;
    
    String authToken;
    
    int port;
    
    //Optional parameter. IPs to use to attempt direct connection
    Collection<String> serverIps;

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Collection<String> getServerIps() {
        return serverIps;
    }

    public void setServerIps(Collection<String> serverIps) {
        this.serverIps = serverIps;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public int getPort() {
        if(port==0) {
            if(this.protocol.equals("https")) {
                return 443;
            }
            return 80;
        }
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
