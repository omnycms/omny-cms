package ca.omny.service.client;

import com.google.gson.Gson;
import ca.omny.auth.token.AuthTokenParser;
import ca.omny.omny.service.discovery.ServiceLocator;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.Map;

public class BaseServiceClient {
    
    ServiceClient serviceClient;
    
    AuthTokenParser authTokenParser;
    
    ServiceLocator locator;

    public BaseServiceClient() {
        if(serviceClient==null) {
            serviceClient = new ServiceClient();
        }
    }
    
    public <T extends Object> T getApiResponse(String method, String serviceName, String path, String hostname, String token, Map<String, String> queryParameters, Object formBody, Map<String, String> headers, Class<T> type) throws FileNotFoundException {
        return this.getApiResponse(method, serviceName, path, hostname, token, queryParameters, null, formBody, headers, type);
    }
    
    public <T extends Object> T getApiResponse(String method, String serviceName, String path, String hostname, String token, Map<String, String> queryParameters, Map<String, Object> formParameters, Map<String, String> headers, Class<T> type) throws FileNotFoundException {
        return this.getApiResponse(method, serviceName, path, hostname, token, queryParameters, formParameters, null, headers, type);
    }

    public <T extends Object> T getApiResponse(String method, String serviceName, String path, String hostname, String token, Map<String, String> queryParameters, Map<String, Object> formParameters, Object formBody,  Map<String, String> headers, Class<T> type) throws FileNotFoundException {
        Gson g = new Gson();
        Collection<String> serviceUrls = locator.findService(serviceName);
        if (serviceUrls != null) {
            for(String server: serviceUrls) {
  
                String apiResponse = serviceClient.getApiResponse(method, server+path, hostname, token, queryParameters, formParameters, formBody, headers);
                if (type.equals(String.class)) {
                    return (T) apiResponse;
                }
                return g.fromJson(apiResponse, type);
            }
        }
        return null;
    }
    
    public <T extends Object> T get(String serviceName, String path, String hostname, String token, Map<String, String> queryParams, Map<String, String> headers, Class<T> type) throws FileNotFoundException {
        return this.getApiResponse("GET", serviceName, path, hostname, token, queryParams, null, headers, type);
    }
    
    public <T extends Object> T put(String serviceName, String path, String hostname, String token, Map<String, String> queryParams, Object formBody, Map<String, String> headers, Class<T> type) throws FileNotFoundException {
        return this.getApiResponse("PUT", serviceName, path, hostname, token, queryParams, formBody, headers, type);
    }
    
    public <T extends Object> T post(String serviceName, String path, String hostname, String token, Map<String, String> queryParams, Object formBody, Map<String, String> headers, Class<T> type) throws FileNotFoundException {
        return this.getApiResponse("POST", serviceName, path, hostname, token, queryParams, formBody, headers, type);
    }
    
    public <T extends Object> T delete(String serviceName, String path, String hostname, String token, Map<String, String> queryParams, Map<String, String> headers, Class<T> type) throws FileNotFoundException {
        return this.getApiResponse("DELETE", serviceName, path, hostname, token, queryParams, null, headers, type);
    }

    public ServiceLocator getLocator() {
        return locator;
    }
    
    public void setLocator(ServiceLocator locator) {
        this.locator = locator;
    }
}
