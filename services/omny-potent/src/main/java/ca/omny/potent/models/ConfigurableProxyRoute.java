package ca.omny.potent.models;

import ca.omny.routing.IRoute;
import java.util.Map;

public class ConfigurableProxyRoute  implements IRoute<Map> {

    String route;
    Map obj;

    public ConfigurableProxyRoute(String route, Map obj) {
        this.route = route;
        this.obj = obj;
    }
    
    @Override
    public String getPath() {
        return route;
    }

    @Override
    public Map getObject() {
        return obj;
    }
    
}
