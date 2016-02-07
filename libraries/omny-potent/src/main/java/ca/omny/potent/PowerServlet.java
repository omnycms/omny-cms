package ca.omny.potent;

import ca.omny.db.IDocumentQuerier;
import ca.omny.documentdb.QuerierFactory;
import ca.omny.extension.proxy.IOmnyProxyService;
import ca.omny.extension.proxy.IPermissionCheck;
import ca.omny.potent.site.SiteConfigurationLoader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ca.omny.potent.mappers.OmnyRouteMapper;
import ca.omny.potent.models.ProxyAndConfiguration;
import ca.omny.potent.models.ProxyRoute;
import ca.omny.potent.permissions.roles.RoleBasedPermissionChecker;
import ca.omny.request.RequestInput;
import ca.omny.request.RequestResponseManager;
import ca.omny.request.ResponseOutput;
import ca.omny.routing.IRoute;
import ca.omny.routing.RoutingTree;
import ca.omny.storage.StorageFactory;
import java.util.Collection;
import java.util.LinkedList;
import ca.omny.request.management.HttpServletRequestToInternalRequest;
import ca.omny.request.management.InternalResponseWriter;

public class PowerServlet extends HttpServlet {
    
    AuthorizationCheck authChecker = new AuthorizationCheck();
    PermissionResolver permissionResolver = new PermissionResolver();
    SiteConfigurationLoader siteConfigurationLoader = new SiteConfigurationLoader();
    SiteOwnerMapper siteOwnerMapper = new SiteOwnerMapper();
    PassThroughProxy proxy = new PassThroughProxy();
    IDocumentQuerier querier = QuerierFactory.getDefaultQuerier();
    IPermissionCheck permissionChecker = new RoleBasedPermissionChecker(querier);
    OmnyRouteMapper routeMapper = new OmnyRouteMapper();
    InternalResponseWriter responseWriter;
    HttpServletRequestToInternalRequest requestConverter;
    
    static Collection<IOmnyProxyService> proxyServices = new LinkedList<IOmnyProxyService>();
    
    RoutingTree<IOmnyProxyService> router;
    
    public static void addProxyService(IOmnyProxyService proxyService) {
        proxyServices.add(proxyService);
    }

    public static Collection<IOmnyProxyService> getProxyServices() {
        return proxyServices;
    }
    
    public PowerServlet() {
        requestConverter = new HttpServletRequestToInternalRequest();
        responseWriter = new InternalResponseWriter();
    }
    
    public void initializeRouter() {
        if(router==null) {
            router = new RoutingTree<>("");
            for(IOmnyProxyService service: proxyServices) {
                ProxyRoute route = new ProxyRoute(service);
                if(route.getPath()!=null) {
                    router.addRoute(route);
                }
            }
        }
    }

    @Inject
    public void setPermissionChecker(IPermissionCheck permissionChecker) {
        this.permissionChecker = permissionChecker;
    }

    @Inject
    public void setSiteConfigurationLoader(SiteConfigurationLoader siteConfigurationLoader) {
        this.siteConfigurationLoader = siteConfigurationLoader;
    }
    
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException { 
        String host = getHost(request);
        System.out.println("processing request for "+host);
        
       
        Map<String, String> queryStringParameters = this.getQueryStringParameters(request.getQueryString());
        String securityToken = queryStringParameters.get("access_token");
        
        String uid = siteOwnerMapper.getUid(securityToken);
        RequestResponseManager requestResponseManager = new RequestResponseManager();
        requestResponseManager.setUserId(uid);

        RequestInput requestInput = requestConverter.getInternalInput(request);
        requestResponseManager.setRequest(requestInput);
        requestResponseManager.setDatabaseQuerier(QuerierFactory.getDefaultQuerier());
        requestResponseManager.setStorageDevice(StorageFactory.getDefaultStorage());
        ResponseOutput responseOutput = new ResponseOutput();
        requestResponseManager.setResponse(responseOutput);
        requestResponseManager.getRequest().setHostname(host);
        
        String route = OmnyRouteMapper.getProxyRoute(requestResponseManager);
        boolean permitted = authChecker.isAuthorized(requestResponseManager);
        if(permitted) {
            initializeRouter();
            ProxyAndConfiguration dynamicProxy = routeMapper.getAppropriateProxy(request.getRequestURI());
            if(dynamicProxy!=null) {
                dynamicProxy.getProxy().proxyRequest(requestResponseManager, dynamicProxy.getConfiguration());
            } else {
                IRoute<IOmnyProxyService> r = router.matchPath(request.getRequestURI());
                IOmnyProxyService proxyService = r.getObject();
                proxyService.proxyRequest(requestResponseManager, null);          
            }
            responseWriter.write(responseOutput, response);
        } else {
            if(uid==null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            } else {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            }
            response.getWriter().write("Not Permitted");
        }   
    }

    private String getHost(HttpServletRequest req) {
        String host = req.getHeader("X-Origin");
        if(host==null) {
            host = req.getHeader("Hostname");
        }
        if(host==null && req.getHeader("Host")!=null) {
            int end = req.getHeader("Host").indexOf(":");
            if(end==-1) {
                end = req.getHeader("Host").length();
            }
            host = req.getHeader("Host").substring(0,end);
        }
        if(host==null) {
            host = req.getServerName();
        }
        String rootDomain = host.substring(host.indexOf(".")+1);
        String key = querier.getKey("mapped_domains",rootDomain);
        String match = querier.get(key, String.class);
        if(match!=null) {
            host = host.substring(0,host.indexOf("."));
        }
        key = querier.getKey("domain_aliases", host);
        String alias = querier.get(key, String.class);
        if (alias != null) {
            host=alias;
        }
        return host;   
    }
     
    public Map<String,String> getQueryStringParameters(String queryString) {
        HashMap<String,String> params = new HashMap<String, String>();
        if(queryString==null) {
            return params;
        }
        for(String group: queryString.split("&")) {
            String[] parts = group.split("=");
            if(parts.length==1) {
                params.put(group, "true");
            } else {
                params.put(parts[0], parts[1]);
            }
        }
        
        return params;
    }
}
