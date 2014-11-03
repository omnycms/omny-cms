package ca.omny.potent;

import ca.omny.documentdb.IDocumentQuerier;
import ca.omny.potent.models.AccessRule;
import ca.omny.potent.models.SiteConfiguration;
import ca.omny.potent.permissions.roles.IPermissionCheck;
import ca.omny.potent.site.SiteConfigurationLoader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ca.omny.potent.ext.ExtensibleProxy;
import ca.omny.potent.mappers.OmnyRouteMapper;
import java.net.URL;

@ApplicationScoped
public class PowerServlet extends HttpServlet {
    
    @Inject
    IPermissionCheck permissionChecker;
    
    @Inject
    PermissionResolver permissionResolver;
    
    @Inject
    SiteConfigurationLoader siteConfigurationLoader;
    
    @Inject 
    SiteOwnerMapper siteOwnerMapper;
    
    @Inject
    PassThroughProxy proxy;
    
    @Inject
    ExtensibleProxy extProxy;
    
    @Inject
    IDocumentQuerier querier;

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
        } else {
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
        Map<String, String> queryStringParameters = this.getQueryStringParameters(req.getQueryString());
        String securityToken = queryStringParameters.get("access_token");
        
        SiteConfiguration configuration = siteConfigurationLoader.getConfiguration(host);
        
        String route = OmnyRouteMapper.getProxyRoute(req);
        AccessRule rule = permissionResolver.getMostRelevantAccessRule(route, req.getMethod(), configuration);
        permissionResolver.injectParameters(host, req.getRequestURI(), queryStringParameters, rule);
        
        String uid = siteOwnerMapper.getUid(securityToken);
        boolean isSiteOwner = siteOwnerMapper.isSiteOwner(host, uid);
        boolean isSiteUser = siteOwnerMapper.isSiteUser(host, uid);
        
        boolean permitted = false;
        switch(rule.getAuthorizationLevel()) {
            case AccessRule.UNAUTHENTICATED:
                permitted = true;
                break;
            case AccessRule.AUTHENTICATED:
                if(isSiteOwner) {
                    permitted = true;
                    break;
                }
                if(securityToken==null) {
                    permitted = false;
                    break;
                }
                if(!isSiteUser && !host.equals("admin.special")) {
                    permitted = false;
                    break;
                }
                permitted = rule.getPermissions()==null||permissionChecker.hasPermissions(host, rule.getPermissions(), securityToken);
                break;
            case AccessRule.OWNER:
                permitted = isSiteOwner;
                break;  
        }
        if(permitted) {
            if(route.startsWith("/api/ext/")) {
                extProxy.proxyRequest(uid, req, resp);
            } else {
                proxy.proxyRequest(host,uid,req, resp);
            }
            //resp.getWriter().write(req.getMethod());
        } else {
            if(uid==null) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            } else {
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            }
            resp.getWriter().write("Not Permitted");
        }   
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
