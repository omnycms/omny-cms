package ca.omny.services.sites.apis;

import ca.omny.request.ApiResponse;
import ca.omny.request.OmnyApi;
import ca.omny.request.RequestResponseManager;
import com.google.gson.Gson;
import ca.omny.sites.mappers.SiteMapBuilder;
import ca.omny.sites.models.SiteMap;
import ca.omny.storage.StorageSystem;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

public class SiteMaps implements OmnyApi {

    Gson gson;
    
    SiteMapBuilder siteMapBuilder;

    public SiteMaps() {
        gson = new Gson();
        siteMapBuilder = new SiteMapBuilder();
    }

    @Override
    public String getBasePath() {
        return SiteApiConstants.base+"/sitemap";
    }

    @Override
    public String[] getVersions() {
        return SiteApiConstants.versions;
    }

    @Override
    public ApiResponse getResponse(RequestResponseManager requestResponseManager) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(SiteMap.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            StringWriter sw = new StringWriter();
            StorageSystem storageSystem = new StorageSystem(requestResponseManager.getStorageDevice());
            jaxbMarshaller.marshal(siteMapBuilder.getSiteMap(requestResponseManager.getRequestHostname(), storageSystem),sw);
            requestResponseManager.getResponse().setHeader("Content-Type", "application/xml");
            return new ApiResponse(sw.toString(),200);
        } catch (JAXBException ex) {
            Logger.getLogger(SiteMaps.class.getName()).log(Level.SEVERE, null, ex);
            return new ApiResponse("", 500);
        }
    }

    @Override
    public ApiResponse postResponse(RequestResponseManager requestResponseManager) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ApiResponse putResponse(RequestResponseManager requestResponseManager) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ApiResponse deleteResponse(RequestResponseManager requestResponseManager) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
   
}
