package ca.omny.request.management;

import ca.omny.configuration.IEnvironmentToolsProvider;
import ca.omny.request.RequestInput;
import ca.omny.request.RequestResponseManager;
import ca.omny.request.ResponseOutput;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;

public class RequestResponseBuilder {
    HttpServletRequestToInternalRequest inputTransformer;
    DomainMapper domainMapper = new DomainMapper();
    IEnvironmentToolsProvider provider;
    SiteOwnerMapper siteOwnerMapper;
    
    public RequestResponseBuilder(IEnvironmentToolsProvider provider) {
        inputTransformer = new HttpServletRequestToInternalRequest();
        this.provider = provider;
        siteOwnerMapper = new SiteOwnerMapper();
    }
    
    public RequestResponseManager getRequestResponseManager(HttpServletRequest request) throws IOException {
        RequestInput internalInput = inputTransformer.getInternalInput(request);
        internalInput.setHostname(domainMapper.getHost(request, provider.getDefaultDocumentQuerier()));
        
        RequestResponseManager manager = new RequestResponseManager(internalInput, new ResponseOutput(), provider);
        String securityToken = internalInput.getParameter("access_token");
        String uid = siteOwnerMapper.getUid(securityToken, provider.getDefaultDocumentQuerier());
        manager.setUserId(uid);
        
        return manager;
    }
}
