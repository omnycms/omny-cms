package ca.omny.request;

import ca.omny.configuration.IEnvironmentToolsProvider;
import ca.omny.db.IDocumentQuerier;
import ca.omny.storage.IStorage;
import com.google.gson.Gson;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;

public class RequestResponseManager {
    Gson gson = new Gson();
    RequestInput request;
    ResponseOutput response;
    String userId;
    ByteArrayOutputStream bodyStream;
    Map<String,String> queryStringCache;
    Map<String,String> pathParameters;
    IStorage storageDevice;
    IDocumentQuerier database;
    IEnvironmentToolsProvider provider;
    
    public RequestResponseManager() {
    }

    public RequestResponseManager(RequestInput request, ResponseOutput response, IEnvironmentToolsProvider provider) {
        this.request = request;
        this.response = response;
        this.provider = provider;
        this.storageDevice = provider.getStorage(request);
        this.database = provider.getDocumentQuerier(request);
    }
   
    public RequestInput getRequest() {
        return request;
        
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getQueryStringParameter(String parameterName) {
        return request.getParameter(parameterName);
    }

    public void setRequest(RequestInput request) {
        this.request = request;
        this.bodyStream = null;
        this.queryStringCache = null;
        this.pathParameters = null;
    }
    
    public <T extends Object> T getEntity(Class<T> type) {
        try {
            String body = IOUtils.toString(this.getBody(true));
            return gson.fromJson(body, type);
        } catch (IOException ex) {
            Logger.getLogger(RequestResponseManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public ResponseOutput getResponse() {
        return response;
    }

    public void setResponse(ResponseOutput response) {
        this.response = response;
    }

    /**
     * 
     * @param cache Enable retrieving the body multiple times at the cost of memory
     * @return
     * @throws IOException 
     */
    public InputStream getBody(boolean cache) throws IOException {
        if(!cache) {
            return request.getContent();
        }
        if(bodyStream==null) {
            bodyStream = new ByteArrayOutputStream();
            IOUtils.copy(request.getContent(),bodyStream);
        }
        return new ByteArrayInputStream(bodyStream.toByteArray());
    }
    
    public String getRequestHostname() {
        if(request.getHostname() != null) {
            return request.getHostname();
        }
        if (request != null) {
            if (request.getHeader("X-Origin") != null) {
                return request.getHeader("X-Origin");
            } else if (request.getHeader("Origin") != null) {
                return request.getHeader("Origin");
            }
        }
        return null;
    }

    public Map<String, String> getPathParameters() {
        return pathParameters;
    }
    
    public String getPathParameter(String parameter) {
        return pathParameters.get(parameter);
    }

    public void setPathParameters(Map<String, String> pathParameters) {
        this.pathParameters = pathParameters;
    }
    
    /**
     * Provides a context specific storage instance. Can use request information, such as hostname to provide unique storage
     * @return A context specific storage instance
     */
    public IStorage getStorageDevice() {
        return storageDevice;
    }
    
    public void setStorageDevice(IStorage storage) {
        storageDevice = storage;
    }
    
    /**
     * Provides a context specific database instance. Can use request information, such as hostname to provide unique DBs
     * @return A context specific database instance
     */
    public IDocumentQuerier getDatabaseQuerier() {
        return database;
    }
    
    public void setDatabaseQuerier(IDocumentQuerier documentQuerier) {
        database = documentQuerier;
    }
}
