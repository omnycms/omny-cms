package ca.omny.server;

import ca.omny.auth.sessions.SessionMapper;
import ca.omny.configuration.ConfigurationReader;
import ca.omny.request.api.ApiResponse;
import ca.omny.request.management.RequestResponseManager;
import ca.omny.request.api.OmnyApi;
import ca.omny.request.api.OmnyApiRegistry;
import ca.omny.request.management.HttpServletRequestToInternalRequest;
import ca.omny.request.management.InternalResponseWriter;
import ca.omny.request.management.RequestInput;
import ca.omny.request.management.ResponseOutput;
import ca.omny.routing.IRoute;
import ca.omny.routing.RoutingTree;
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
        for (OmnyApi api : OmnyApiRegistry.getRegisteredApis()) {
            for (String version : api.getVersions()) {
                ApiRoute route = new ApiRoute(api, version);
                router.addRoute(route);
            }
        }
    }

    private boolean useInjection() {
        configurationReader = ConfigurationReader.getDefaultConfigurationReader();
        return configurationReader.getSimpleConfigurationString("OMNY_NO_INJECTION") == null;
    }

    private SessionMapper getSessionMapper() {
        return new SessionMapper();
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
        requestResponseManager.setSessionMapper(getSessionMapper());
        RequestInput requestInput = requestConverter.getInternalInput(request);
        requestResponseManager.setRequest(requestInput);
        ResponseOutput responseOutput = new ResponseOutput();
        requestResponseManager.setResponse(responseOutput);
        IRoute<OmnyApi> route = router.matchPath(request.getRequestURI());
        if (route == null) {
            response.setStatus(404);
            return;
        }
        OmnyApi api = route.getObject();
        injectPathParameters(request, api, requestResponseManager);
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
                IOUtils.write((byte[]) responseObject, response.getOutputStream());
            } else {
                response.getWriter().write(responseObject.toString());
            }

        }
    }

    private void injectPathParameters(HttpServletRequest request, OmnyApi api, RequestResponseManager requestResponseManager) {
        String requestURI = request.getRequestURI();
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
