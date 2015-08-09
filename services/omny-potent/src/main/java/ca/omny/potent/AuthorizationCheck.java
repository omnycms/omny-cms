package ca.omny.potent;

import ca.omny.documentdb.QuerierFactory;
import ca.omny.potent.mappers.OmnyRouteMapper;
import ca.omny.extension.proxy.AccessRule;
import ca.omny.potent.models.SiteConfiguration;
import ca.omny.extension.proxy.IPermissionCheck;
import ca.omny.potent.permissions.roles.RoleBasedPermissionChecker;
import ca.omny.potent.site.SiteConfigurationLoader;
import java.io.IOException;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

public class AuthorizationCheck {
    
    IPermissionCheck permissionChecker = new RoleBasedPermissionChecker(QuerierFactory.getDefaultQuerier());
    PermissionResolver permissionResolver = new PermissionResolver();
    SiteConfigurationLoader siteConfigurationLoader = new SiteConfigurationLoader();
    SiteOwnerMapper siteOwnerMapper = new SiteOwnerMapper();
    
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
