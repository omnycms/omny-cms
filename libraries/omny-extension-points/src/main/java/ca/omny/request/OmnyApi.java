package ca.omny.request;

import ca.omny.request.RequestResponseManager;

public interface OmnyApi {
    
    public String getBasePath();
    public String[] getVersions();
    public ApiResponse getResponse(RequestResponseManager requestResponseManager); 
    public ApiResponse postResponse(RequestResponseManager requestResponseManager); 
    public ApiResponse putResponse(RequestResponseManager requestResponseManager); 
    public ApiResponse deleteResponse(RequestResponseManager requestResponseManager); 
}
