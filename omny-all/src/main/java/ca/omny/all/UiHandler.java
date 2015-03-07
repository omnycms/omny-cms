package ca.omny.all;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ResourceHandler;

public class UiHandler extends AbstractHandler {
    
    String htmlFileContents;
    String contentRoot;
    ResourceHandler resourceHandler;

    public UiHandler(String contentRoot) {
        this.contentRoot = contentRoot;
        
        String staticFilesDirectory = ".";
        if(System.getenv("omny_static_location")!=null) {
            staticFilesDirectory = System.getenv("omny_static_location");
        }
        resourceHandler = new ResourceHandler();
        
        resourceHandler.setDirectoriesListed(true);
        resourceHandler.setWelcomeFiles(new String[]{ "index.html" });
        resourceHandler.setEtags(true);

        resourceHandler.setResourceBase(staticFilesDirectory);
    }
    
    @Override
    public void handle(String string, Request rqst, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String requestUrl = request.getRequestURI();
        if(requestUrl.equals("/")) {
            response.sendRedirect("/default.html");
            return;
        }
        if(requestUrl.startsWith("/version")) {
            requestUrl = requestUrl.substring(requestUrl.indexOf("/",1));
            requestUrl = requestUrl.substring(requestUrl.indexOf("/",1));
            rqst.setRequestURI(requestUrl);
            rqst.setPathInfo(requestUrl);
            
        }
        boolean ui = false;
        if(requestUrl.startsWith("/partial")) {
            ui = true;
        } else if(requestUrl.startsWith("/template")) {
            ui = true;
        } else if(requestUrl.startsWith("/js")) {
            ui = true;
        } else if (requestUrl.startsWith("/themes")) {
            String file = requestUrl.substring(1);
            String redirect = "/api/v1.0/content";
            String queryString = "file="+file+"&sendFile";
            rqst.setRequestURI(redirect);
            rqst.setQueryString(queryString);
        }  else if (requestUrl.startsWith("/global/themes")) {
            String file = requestUrl.substring(1);
            String redirect = "/api/v1.0/content";
            String queryString = "file="+file+"&sendFile";
            rqst.setRequestURI(redirect);
            rqst.setQueryString(queryString);
        } else if (requestUrl.startsWith("/robots.txt")) {
            String file = requestUrl.substring(1);
            String redirect = "/api/v1.0/sites/robots";
            rqst.setRequestURI(redirect);
        } else if (requestUrl.startsWith("/sitemap.xml")) {
            String file = requestUrl.substring(1);
            String redirect = "/api/v1.0/sites/sitemap";
            rqst.setRequestURI(redirect);
        }
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "OPTIONS, GET, HEAD, PUT, POST, DELETE");
        response.setHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers"));
        if(request.getMethod().toUpperCase().equals("OPTIONS")) {
            rqst.setHandled(true);
            response.setStatus(200);
        }
        if(!ui&&requestUrl.endsWith(".html")) {
            rqst.setHandled(true);
            response.setHeader("Content-Type", "text/html");
            populateHtmlContents();
            response.getWriter().write(htmlFileContents);
        } else {
            resourceHandler.handle(requestUrl, rqst, request, response);
        }
    }
    
    public void populateHtmlContents() {
        if(htmlFileContents!=null) {
            return;
        }
        try {
            String cdn = System.getenv("OMNY_UI_CDN");
            if(cdn == null) {
                cdn = "";
                htmlFileContents = FileUtils.readFileToString(new File(contentRoot+"/index.html"));
            } else {
                URL url = new URL(cdn+"/index.html");
                htmlFileContents = IOUtils.toString(url);
            }
            MustacheFactory mf = new DefaultMustacheFactory();
            Mustache compiledTemplate = mf.compile(new StringReader(htmlFileContents),"test");
            StringWriter writer = new StringWriter();
            
            HashMap<String,String> parameters = new HashMap<String, String>();
            parameters.put("baseUrl",cdn);
            compiledTemplate.execute(writer, parameters);
            htmlFileContents = writer.toString();
        } catch (IOException ex) {
            Logger.getLogger(UiHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
