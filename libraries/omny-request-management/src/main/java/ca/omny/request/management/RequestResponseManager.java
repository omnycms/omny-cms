package ca.omny.request.management;

import ca.omny.auth.sessions.SessionMapper;
import ca.omny.documentdb.IDocumentQuerier;
import ca.omny.documentdb.QuerierFactory;
import ca.omny.storage.IStorage;
import ca.omny.storage.StorageFactory;
import ca.omny.storage.StorageSystem;
import com.google.gson.Gson;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;

@RequestScoped
public class RequestResponseManager {
    Gson gson = new Gson();
    HttpServletRequest request;
    HttpServletResponse response;
    ByteArrayOutputStream bodyStream;
    Map<String,String> queryStringCache;
    Map<String,String> pathParameters;
    
    SessionMapper sessionMapper;

    public void setSessionMapper(SessionMapper sessionMapper) {
        this.sessionMapper = sessionMapper;
    }

    public HttpServletRequest getRequest() {
        return request;
        
    }
    
    public String getUserId() {
        return sessionMapper.getUserId(request);
    }
    
    public String getQueryStringParameter(String parameterName) {
        return request.getParameter(parameterName);
    }

    public void setRequest(HttpServletRequest request) {
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

    public HttpServletResponse getResponse() {
        return response;
    }

    public void setResponse(HttpServletResponse response) {
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
            return request.getInputStream();
        }
        if(bodyStream==null) {
            bodyStream = new ByteArrayOutputStream();
            IOUtils.copy(request.getInputStream(),bodyStream);
        }
        return new ByteArrayInputStream(bodyStream.toByteArray());
    }
    
    public String getRequestHostname() {
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
        return StorageFactory.getDefaultStorage();
    }
    
    public StorageSystem getStorageSystem() {
        StorageSystem storageSystem = new StorageSystem(this.getStorageDevice());
        return storageSystem;
    }
    
    /**
     * Provides a context specific database instance. Can use request information, such as hostname to provide unique DBs
     * @return A context specific database instance
     */
    public IDocumentQuerier getDatabaseQuerier() {
        return QuerierFactory.getDefaultQuerier();
    }
}
