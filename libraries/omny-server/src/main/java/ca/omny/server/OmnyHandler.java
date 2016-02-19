package ca.omny.server;

import ca.omny.configuration.ConfigurationReader;
import ca.omny.configuration.IEnvironmentToolsProvider;
import ca.omny.request.RequestResponseManager;
import ca.omny.request.OmnyApi;
import ca.omny.request.OmnyApiRegistry;
import ca.omny.request.OmnyBaseHandler;
import ca.omny.request.management.InternalResponseWriter;
import ca.omny.request.management.RequestResponseBuilder;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.Collection;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class OmnyHandler extends AbstractHandler {

    OmnyBaseHandler baseHandler;
    ConfigurationReader configurationReader;
    InternalResponseWriter responseWriter = new InternalResponseWriter();
    IEnvironmentToolsProvider provider;

    public OmnyHandler(IEnvironmentToolsProvider provider) {
        Collection<OmnyApi> registeredApis = OmnyApiRegistry.getRegisteredApis();
        this.provider = provider;
        baseHandler = new OmnyBaseHandler(provider, registeredApis);
    }
    
    public OmnyHandler(IEnvironmentToolsProvider provider, Collection<OmnyApi> apis) {
        this.provider = provider;
        baseHandler = new OmnyBaseHandler(provider, apis);
    }

    @Override
    public void handle(String s, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (!request.getRequestURI().startsWith("/api")) {
            return;
        }
        if (baseRequest != null) {
            baseRequest.setHandled(true);
        }
        RequestResponseManager requestResponseManager = new RequestResponseBuilder(provider).getRequestResponseManager(request);
        
        baseHandler.handle(requestResponseManager);
        responseWriter.write(requestResponseManager.getResponse(), response);
    }

}
