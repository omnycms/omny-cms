package ca.omny.server;

import ca.omny.auth.sessions.SessionMapper;
import ca.omny.configuration.ConfigurationReader;
import ca.omny.documentdb.QuerierFactory;
import ca.omny.request.ApiResponse;
import ca.omny.request.RequestResponseManager;
import ca.omny.request.OmnyApi;
import ca.omny.request.OmnyApiRegistry;
import ca.omny.request.management.HttpServletRequestToInternalRequest;
import ca.omny.request.management.InternalResponseWriter;
import ca.omny.request.RequestInput;
import ca.omny.request.ResponseOutput;
import ca.omny.routing.IRoute;
import ca.omny.routing.RoutingTree;
import ca.omny.storage.StorageFactory;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.HashMap;
import javax.enterprise.inject.Instance;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class OmnyHandler extends AbstractHandler {

    Instance<OmnyApi> apis;
    RoutingTree<OmnyApi> router;
    ConfigurationReader configurationReader;
    InternalResponseWriter responseWriter;
    HttpServletRequestToInternalRequest requestConverter;

    Gson gson = new Gson();

    public OmnyHandler() {
        router = new RoutingTree<>("");
        configurationReader = ConfigurationReader.getDefaultConfigurationReader();
        OmnyClassRegister register = new OmnyClassRegister();
        register.loadClass("ca.omny.db.extended.ExtendedDatabaseFactory");
        register.loadFromEnvironment();
        requestConverter = new HttpServletRequestToInternalRequest();
        responseWriter = new InternalResponseWriter();
        for (OmnyApi api : OmnyApiRegistry.getRegisteredApis()) {
            for (String version : api.getVersions()) {
                ApiRoute route = new ApiRoute(api, version);
                router.addRoute(route);
            }
        }
    }

    private SessionMapper getSessionMapper() {
        return new SessionMapper();
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
            if (responseObject instanceof byte[]) {
                response.getWriter().write(responseObject.toString());
            } else {
                response.getWriter().write(responseObject.toString());
            }
        }
    }

    @Override
    public void handle(String s, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (!request.getRequestURI().startsWith("/api")) {
            return;
        }
        if (baseRequest != null) {
            baseRequest.setHandled(true);
        }
        RequestResponseManager requestResponseManager = new RequestResponseManager();
        requestResponseManager.setUserId(getSessionMapper().getUserId(request));

        RequestInput requestInput = requestConverter.getInternalInput(request);
        requestResponseManager.setRequest(requestInput);
        requestResponseManager.setDatabaseQuerier(QuerierFactory.getDefaultQuerier());
        requestResponseManager.setStorageDevice(StorageFactory.getDefaultStorage());
        ResponseOutput responseOutput = new ResponseOutput();
        requestResponseManager.setResponse(responseOutput);
        
        handle(requestResponseManager);
        responseWriter.write(responseOutput, response);
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
