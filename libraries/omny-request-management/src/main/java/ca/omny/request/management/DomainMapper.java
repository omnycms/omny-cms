package ca.omny.request.management;

import ca.omny.db.IDocumentQuerier;
import javax.servlet.http.HttpServletRequest;

public class DomainMapper {
    
    public String getHost(HttpServletRequest req, IDocumentQuerier querier) {
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
}
