package ca.omny.potent;

import ca.omny.documentdb.IDocumentQuerier;
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
import ca.omny.potent.models.ProxyRoute;
import ca.omny.routing.IRoute;
import ca.omny.routing.RoutingTree;
import javax.enterprise.inject.Instance;

@ApplicationScoped
public class PowerServlet extends HttpServlet {
    
    @Inject
    IPermissionCheck permissionChecker;
    
    @Inject
    AuthorizationCheck authChecker;
    
    @Inject
    PermissionResolver permissionResolver;
    
    @Inject
    SiteConfigurationLoader siteConfigurationLoader;
    
    @Inject 
    SiteOwnerMapper siteOwnerMapper;
    
    @Inject
    PassThroughProxy proxy;
    
    @Inject
    IDocumentQuerier querier;
    
    @Inject
    Instance<IOmnyProxyService> proxyServices;
    
    RoutingTree<IOmnyProxyService> router;
    
    public void initializeRouter() {
        if(router==null) {
            router = new RoutingTree<>("");
            for(IOmnyProxyService service: proxyServices) {
                ProxyRoute route = new ProxyRoute(service);
                router.addRoute(route);
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
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException { 
        String host = getHost(req);
        System.out.println("processing request for "+host);
        
        String route = OmnyRouteMapper.getProxyRoute(req);
        Map<String, String> queryStringParameters = this.getQueryStringParameters(req.getQueryString());
        String securityToken = queryStringParameters.get("access_token");
        
        String uid = siteOwnerMapper.getUid(securityToken);
  
        boolean permitted = authChecker.isAuthorized(host, uid, req, queryStringParameters);
        if(permitted) {
            initializeRouter();
            IRoute<IOmnyProxyService> r = router.matchPath(req.getRequestURI());
            IOmnyProxyService proxyService = r.getObject();
            proxyService.proxyRequest(host,uid,req, resp);          
        } else {
            if(uid==null) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            } else {
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            }
            resp.getWriter().write("Not Permitted");
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
