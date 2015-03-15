package ca.omny.all;

import ca.omny.documentdb.IDocumentQuerier;
import ca.omny.request.management.RequestResponseManager;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
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
    IDocumentQuerier querier;

    public UiHandler(String contentRoot, IDocumentQuerier querier) {
        this.contentRoot = contentRoot;
        this.querier = querier;
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
            RequestResponseManager manager = new RequestResponseManager();
            manager.setRequest(request);
            String pageContents = this.getHtml(manager.getRequestHostname(), request.getRequestURI());
            response.getWriter().write(pageContents);
        } else {
            resourceHandler.handle(requestUrl, rqst, request, response);
        }
    }
    
    public String getHtml(String site, String page) {
        site = this.getRealSite(site);
        page = page.substring(1,page.lastIndexOf(".html"));
        Map pageHtml = getPageHtml(site,page);
        String body = pageHtml.get("body").toString();
        String head = pageHtml.get("head").toString();
        
        //make api call to /api/v1.0/pages/pagehtml
        
        String formatted = htmlFileContents.replace("</head>", head+"</head>");
        formatted = formatted.replace("</body>", body+"</body>");
        return formatted;
    }
    
    private String getRealSite(String site) {
        String key = querier.getKey("domain_aliases", site);
        String alias = querier.get(key, String.class);
        if (alias != null) {
            site=alias;
        }
        return site;
    }
    
    private Map getPageHtml(String site, String page) {
        Gson gson = new Gson();
        String result = this.getPageContents(site, page);
        return gson.fromJson(result, Map.class);
    }
    
    private String getPageContents(String site, String page) {
        try {
            int port = 8077;
            if(System.getenv("omny_all_port")!=null) {
                port = Integer.parseInt(System.getenv("omny_all_port"));
            }
            URL url = new URL("http://localhost:"+port+"/api/v1.0/pages/basehtml?page="+page);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.addRequestProperty("X-Origin", site);
            
            return IOUtils.toString(connection.getInputStream());
        } catch (MalformedURLException ex) {
            Logger.getLogger(UiHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ProtocolException ex) {
            Logger.getLogger(UiHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(UiHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
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
