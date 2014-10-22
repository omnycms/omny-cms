package ca.omny.request.api;

import ca.omny.request.management.RequestResponseManager;

public interface OmnyApi {
    
    public String getBasePath();
    public String[] getVersions();
    public ApiResponse getResponse(RequestResponseManager requestResponseManager); 
    public ApiResponse postResponse(RequestResponseManager requestResponseManager); 
    public ApiResponse putResponse(RequestResponseManager requestResponseManager); 
    public ApiResponse deleteResponse(RequestResponseManager requestResponseManager); 
}
