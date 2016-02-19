package ca.omny.potent;

import ca.omny.configuration.IEnvironmentToolsProvider;
import ca.omny.request.management.SiteOwnerMapper;
import ca.omny.extension.proxy.IOmnyProxyService;
import ca.omny.extension.proxy.IPermissionCheck;
import ca.omny.potent.site.SiteConfigurationLoader;
import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ca.omny.potent.mappers.OmnyRouteMapper;
import ca.omny.potent.models.ProxyAndConfiguration;
import ca.omny.potent.models.ProxyRoute;
import ca.omny.potent.permissions.roles.RoleBasedPermissionChecker;
import ca.omny.request.RequestResponseManager;
import ca.omny.request.ResponseOutput;
import ca.omny.routing.IRoute;
import ca.omny.routing.RoutingTree;
import java.util.Collection;
import java.util.LinkedList;
import ca.omny.request.management.HttpServletRequestToInternalRequest;
import ca.omny.request.management.InternalResponseWriter;
import ca.omny.request.management.RequestResponseBuilder;

public class PowerServlet extends HttpServlet {
    
    AuthorizationCheck authChecker = new AuthorizationCheck();
    PermissionResolver permissionResolver = new PermissionResolver();
    SiteConfigurationLoader siteConfigurationLoader = new SiteConfigurationLoader();
    SiteOwnerMapper siteOwnerMapper = new SiteOwnerMapper();
    PassThroughProxy proxy = new PassThroughProxy();
    OmnyRouteMapper routeMapper = new OmnyRouteMapper();
    IEnvironmentToolsProvider toolsProvider;
    InternalResponseWriter responseWriter;
    HttpServletRequestToInternalRequest requestConverter;
    IPermissionCheck permissionChecker;
    
    static Collection<IOmnyProxyService> proxyServices = new LinkedList<IOmnyProxyService>();
    
    RoutingTree<IOmnyProxyService> router;
    
    public static void addProxyService(IOmnyProxyService proxyService) {
        proxyServices.add(proxyService);
    }

    public static Collection<IOmnyProxyService> getProxyServices() {
        return proxyServices;
    }
    
    public PowerServlet(IEnvironmentToolsProvider toolsProvider) {
        responseWriter = new InternalResponseWriter();
        this.toolsProvider = toolsProvider;
        this.permissionChecker = new RoleBasedPermissionChecker(toolsProvider.getDefaultDocumentQuerier());
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

    public void setPermissionChecker(IPermissionCheck permissionChecker) {
        this.permissionChecker = permissionChecker;
    }
    
    public void setSiteConfigurationLoader(SiteConfigurationLoader siteConfigurationLoader) {
        this.siteConfigurationLoader = siteConfigurationLoader;
    }
    
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {         
        RequestResponseManager requestResponseManager = new RequestResponseBuilder(toolsProvider).getRequestResponseManager(request);
        String uid = requestResponseManager.getUserId();
        ResponseOutput responseOutput = requestResponseManager.getResponse();
        
        String route = OmnyRouteMapper.getProxyRoute(requestResponseManager);
        System.out.println("processing request for "+requestResponseManager.getRequestHostname());
        boolean permitted = authChecker.isAuthorized(requestResponseManager);
        if(permitted) {
            initializeRouter();
            ProxyAndConfiguration dynamicProxy = routeMapper.getAppropriateProxy(request.getRequestURI(), requestResponseManager.getDatabaseQuerier());
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
}
