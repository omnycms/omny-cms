package ca.omny.potent;

import ca.omny.potent.mappers.OmnyRouteMapper;
import ca.omny.extension.proxy.AccessRule;
import ca.omny.potent.models.SiteConfiguration;
import ca.omny.extension.proxy.IPermissionCheck;
import ca.omny.potent.site.SiteConfigurationLoader;
import java.io.IOException;
import java.util.Map;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

public class AuthorizationCheck {
    
    @Inject
    IPermissionCheck permissionChecker;
    
    @Inject
    PermissionResolver permissionResolver;
    
    @Inject
    SiteConfigurationLoader siteConfigurationLoader;
    
    @Inject 
    SiteOwnerMapper siteOwnerMapper;
    
    public boolean isAuthorized(String host, String uid, HttpServletRequest req, Map<String, String> queryStringParameters) throws IOException {
        String skipCheck = System.getenv("omny_no_auth");
        if(skipCheck!=null && skipCheck.equals("true")) {
            return true;
        }
        SiteConfiguration configuration = siteConfigurationLoader.getConfiguration(host);
        
        String route = OmnyRouteMapper.getProxyRoute(req);
        AccessRule rule = permissionResolver.getMostRelevantAccessRule(route, req.getMethod(), configuration);
        permissionResolver.injectParameters(host, req.getRequestURI(), queryStringParameters, rule);
        String securityToken = req.getParameter("access_token");
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
        
        return permitted;
    }
}
