package ca.omny.service.client;

import ca.omny.logger.OmnyLogger;
import ca.omny.omny.service.discovery.ServiceLocator;
import com.google.gson.Gson;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import org.apache.commons.io.IOUtils;

@ApplicationScoped
public class ServiceClient {

    ServiceLocator locator;
    
    static Client client = ClientBuilder.newClient();

    @Inject
    OmnyLogger logger;

    public ServiceClient() {
    }

    public void setLogger(OmnyLogger logger) {
        this.logger = logger;
    }
    
    public String get(String path, String hostname, String token, Map<String, String> queryParams, Map<String, String> headers) throws FileNotFoundException {
        return this.getApiResponse("GET", path, hostname, token, queryParams, null, null, headers);
    }
    
    public String put(String path, String hostname, String token, Map<String, String> queryParams, Map<String, Object> formParameters,  Object formBody, Map<String, String> headers) throws FileNotFoundException {
        return this.getApiResponse("PUT", path, hostname, token, queryParams, formParameters, formBody, headers);
    }
    
    public String post(String path, String hostname, String token, Map<String, String> queryParams, Map<String, Object> formParameters, Object formBody, Map<String, String> headers) throws FileNotFoundException {
        return this.getApiResponse("POST", path, hostname, token, queryParams, formParameters, formBody, headers);
    }
    
    public String delete(String path, String hostname, String token, Map<String, String> queryParams, Map<String, String> headers) throws FileNotFoundException {
        return this.getApiResponse("DELETE", path, hostname, token, queryParams, null, null, headers);
    }
      
    public String getApiResponse(String method, String path, String hostname, String token, Map<String,String> queryParameters, Map<String, Object> postParameters, Object formBody, Map<String, String> headers) throws FileNotFoundException {
        UriBuilder builder = UriBuilder.fromUri(path);
        builder.queryParam("access_token", token);
        if(queryParameters!=null) {
            for(String parameter: queryParameters.keySet()) {
                builder.queryParam(parameter, queryParameters.get(parameter));
            }
        }

        URI uri = builder.build();

        Invocation.Builder requestBuilder = client.target(uri).request().accept("application/json");
        if(hostname!=null) {
            requestBuilder.header("X-Origin", hostname);
        }
        if (headers != null) {
            for (String header : headers.keySet()) {
                requestBuilder.header(header, headers.get(header));
            }
        }
        Form postParams = new Form();
        if (postParameters != null) {
            
            for (String key : postParameters.keySet()) {
                postParams.param(key, postParameters.get(key).toString());
            }
        }

        Response response;
        switch(method.toUpperCase()) {
            case "DELETE":
                response = requestBuilder.delete();
                break;
            case "PUT":
                if(formBody!=null) {
                    Gson gson = new Gson();
                    response = requestBuilder.put(Entity.entity(gson.toJson(formBody),MediaType.APPLICATION_JSON));
                } else {
                    response = requestBuilder.put(Entity.form(postParams));
                }
                break;
            case "POST":
                if(formBody!=null) {
                    Gson gson = new Gson();
                    response = requestBuilder.post(Entity.entity(gson.toJson(formBody),MediaType.APPLICATION_JSON));
                } else {
                    response = requestBuilder.post(Entity.form(postParams));
                }
                break;
            default:
                response = requestBuilder.get();
        }
        

        long startTime = System.currentTimeMillis();
        logger.info("sending "+method.toUpperCase()+" request to " + uri.toString());
        Object entity = response.getEntity();
        String data =null;
        try {
            data = response.getEntity()!=null?IOUtils.toString((InputStream)response.getEntity()):null;
        } catch (IOException ex) {
            Logger.getLogger(ServiceClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        long endTime = System.currentTimeMillis();
        logger.info("took " + (endTime - startTime) + " to receive post response from " + uri.toString());
        switch(response.getStatus()) {
            case 404:
                throw new FileNotFoundException(data);
            default:
                return data;
        }
        
    }

    public static String getRequestHostname(HttpServletRequest request) {
        if (request != null) {
            if (request.getHeader("X-Origin") != null) {
                return request.getHeader("X-Origin");
            } else if (request.getHeader("Origin") != null) {
                return request.getHeader("Origin");
            } else {
                return request.getServerName();
            }
        }
        return null;
    }
}
