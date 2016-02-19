package ca.omny.request;

import ca.omny.configuration.ConfigurationReader;
import ca.omny.configuration.IEnvironmentToolsProvider;
import ca.omny.routing.IRoute;
import ca.omny.routing.RoutingTree;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import org.apache.commons.codec.binary.Base64;

public class OmnyBaseHandler {

    RoutingTree<OmnyApi> router;
    ConfigurationReader configurationReader;
    IEnvironmentToolsProvider provider;

    Gson gson = new Gson();

    public OmnyBaseHandler(IEnvironmentToolsProvider provider) {
        Collection<OmnyApi> registeredApis = OmnyApiRegistry.getRegisteredApis();
        init(provider, registeredApis);
    }
    
    public OmnyBaseHandler(IEnvironmentToolsProvider provider, Collection<OmnyApi> apis) {
        init(provider, apis);
    }
    
    private void init(IEnvironmentToolsProvider provider, Collection<OmnyApi> apis) {
        router = new RoutingTree<>("");
        this.provider = provider;
        configurationReader = ConfigurationReader.getDefaultConfigurationReader();
        for (OmnyApi api : apis) {
            for (String version : api.getVersions()) {
                ApiRoute route = new ApiRoute(api, version);
                router.addRoute(route);
            }
        }
    }
    
    public void handle(RequestResponseManager requestResponseManager) throws IOException {
        RequestInput request = requestResponseManager.getRequest();
        ResponseOutput response = requestResponseManager.getResponse();
        IRoute<OmnyApi> route = router.matchPath(request.getUri());
        if (route == null) {
            response.setStatus(404);
            return;
        }
        OmnyApi api = route.getObject();
        injectPathParameters(api, requestResponseManager);
        ApiResponse apiResponse;
        switch (request.getMethod().toLowerCase()) {
            case "post":
                apiResponse = api.postResponse(requestResponseManager);
                break;
            case "put":
                apiResponse = api.putResponse(requestResponseManager);
                break;
            case "delete":
                apiResponse = api.deleteResponse(requestResponseManager);
                break;
            default:
                apiResponse = api.getResponse(requestResponseManager);
        }
        response.setStatus(apiResponse.getStatusCode());

        if (apiResponse.shouldEncodeAsJson()) {
            response.setHeader("Content-Type", "application/json");
            response.getWriter().write(gson.toJson(apiResponse.getResponse()));
        } else {
            Object responseObject = apiResponse.getResponse();
            response.setHeader("Content-Type", apiResponse.getContentType());
            if (responseObject instanceof byte[]) {
                response.getWriter().write(Base64.encodeBase64String((byte[])responseObject));
            } else {
                response.getWriter().write(responseObject.toString());
            }
        }    
    }
        
    private void injectPathParameters(OmnyApi api, RequestResponseManager requestResponseManager) {
        RequestInput request = requestResponseManager.getRequest();
        String requestURI = request.getUri();
        String[] parts = requestURI.split("/");
        // /api/v{{version}} is added before the base path
        int offset = 2;
        String[] apiParts = api.getBasePath().split("/");
        HashMap<String, String> parameters = new HashMap<>();
        for (int i = 0; i < apiParts.length && i + offset < parts.length; i++) {
            if (apiParts[i].startsWith("{")) {
                String parameterName = apiParts[i].substring(1, apiParts[i].length() - 1);
                parameters.put(parameterName, parts[i + offset]);
            }

        }
        requestResponseManager.setPathParameters(parameters);
    }

}
