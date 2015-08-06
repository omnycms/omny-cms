package ca.omny.content.apis;

import ca.omny.request.api.ApiResponse;
import ca.omny.request.api.OmnyApi;
import ca.omny.request.management.RequestResponseManager;
import ca.omny.storage.MetaData;
import ca.omny.storage.Mimedata;
import ca.omny.storage.StorageSystem;
import com.google.gson.Gson;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.NotFoundException;

public class ContentLocation implements OmnyApi {
    
    Mimedata mimedata;
    Gson gson;

    public ContentLocation() {
        gson = new Gson();
        mimedata = new Mimedata();
    }

    public String getBasePath() {
        return ContentApiConstants.base;
    }

    public String[] getVersions() {
        return ContentApiConstants.versions;
    }

    public ApiResponse getResponse(RequestResponseManager requestResponseManager) {
        StorageSystem storageSystem = requestResponseManager.getStorageSystem();
        String file = requestResponseManager.getQueryStringParameter("file");
        HttpServletRequest request =  requestResponseManager.getRequest();
        Map<String, String> queryParameters = new HashMap<String, String>();
        String prefix = "published/default/";
        String hostname = requestResponseManager.getRequestHostname();
        if (file.contains("?")) {
            file = file.substring(0, file.indexOf("?"));
        }
        if(file.startsWith("themes/")) {
            prefix = "themes/current/";
            file = file.substring(7);
        }
        if(file.startsWith("global/")) {
            prefix = "site-files/";
            file = file.substring(7);
            if(file.startsWith("themes/")) {
                file = "themes/current"+file.substring("themes".length());
            }
            hostname="www";
        }
        queryParameters.put("file", prefix + file);
        if (request.getHeader("Send-File") != null||request.getQueryString().contains("sendFile")) {
            MetaData metaData = storageSystem.getMetaData(prefix+file,requestResponseManager.getRequestHostname());
            Date lastModified = new Date(metaData.getLastModified()*1000);
            String ifModified = request.getHeader("If-Modified-Since");
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
            if(ifModified!=null) {  
                try {
                    Date date = dateFormat.parse(ifModified);
                    
                    if(lastModified.before(date)||lastModified.equals(date)) {
                        return new ApiResponse("", 304);
                    }
                } catch (ParseException ex) {
                    Logger.getLogger(ContentLocation.class.getName()).log(Level.SEVERE, null, ex);
                } 
            }
            Object fileContents = storageSystem.getFileContents(prefix + file, hostname, file.endsWith(".json"));
            if(fileContents==null) {
                throw new NotFoundException();
            }
            HttpServletResponse response = requestResponseManager.getResponse();
            response.addHeader("Cache-Control", "public; max-age=30");
            response.addHeader("Last-Modified", dateFormat.format(lastModified));
            response.addHeader("Content-Type", mimedata.getContentTypeFromName(file));
            if(fileContents instanceof String) {
                return new ApiResponse(fileContents.toString(), 200);
            }
            return new ApiResponse(fileContents,200);
        }
        return new ApiResponse("", 404);
    }

    public ApiResponse postResponse(RequestResponseManager requestResponseManager) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public ApiResponse putResponse(RequestResponseManager requestResponseManager) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public ApiResponse deleteResponse(RequestResponseManager requestResponseManager) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
