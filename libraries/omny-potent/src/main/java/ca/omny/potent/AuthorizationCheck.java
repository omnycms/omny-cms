package ca.omny.potent;

import ca.omny.request.management.SiteOwnerMapper;
import ca.omny.configuration.ConfigurationReader;
import ca.omny.potent.mappers.OmnyRouteMapper;
import ca.omny.extension.proxy.AccessRule;
import ca.omny.potent.models.SiteConfiguration;
import ca.omny.extension.proxy.IPermissionCheck;
import ca.omny.potent.permissions.roles.RoleBasedPermissionChecker;
import ca.omny.potent.site.SiteConfigurationLoader;
import ca.omny.request.RequestResponseManager;
import java.io.IOException;

public class AuthorizationCheck {
    
    PermissionResolver permissionResolver = new PermissionResolver();
    SiteConfigurationLoader siteConfigurationLoader = new SiteConfigurationLoader();
    SiteOwnerMapper siteOwnerMapper = new SiteOwnerMapper();
    
    public boolean isAuthorized(RequestResponseManager requestResponseManager) throws IOException {
        IPermissionCheck permissionChecker = new RoleBasedPermissionChecker(requestResponseManager.getDatabaseQuerier());
        String skipCheck = ConfigurationReader.getDefaultConfigurationReader().getConfigurationString("OMNY_NO_AUTH");
        if(skipCheck!=null && skipCheck.equals("true")) {
            return true;
        }
        String host = requestResponseManager.getRequestHostname();
        String uid = requestResponseManager.getUserId();
        SiteConfiguration configuration = siteConfigurationLoader.getConfiguration(host, requestResponseManager.getDatabaseQuerier());
        
        String route = OmnyRouteMapper.getProxyRoute(requestResponseManager);
        AccessRule rule = permissionResolver.getMostRelevantAccessRule(route, requestResponseManager.getRequest().getMethod(), configuration);
        permissionResolver.injectParameters(host, requestResponseManager.getRequest().getUri(), requestResponseManager.getRequest().getQueryStringParameters(), rule);
        String securityToken = requestResponseManager.getRequest().getParameter("access_token");
        boolean isSiteOwner = siteOwnerMapper.isSiteOwner(host, uid, requestResponseManager.getDatabaseQuerier());
        boolean isSiteUser = siteOwnerMapper.isSiteUser(host, uid, requestResponseManager.getDatabaseQuerier());
        
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
