package ca.omny.themes.apis;

import ca.omny.request.ApiResponse;
import ca.omny.request.OmnyApi;
import ca.omny.request.RequestResponseManager;
import ca.omny.storage.StorageSystem;
import com.google.gson.Gson;
import ca.omny.themes.mappers.SampleMapper;
import ca.omny.themes.mappers.ThemeMapper;
import ca.omny.themes.models.Sample;
import java.util.Collection;

public class Samples implements OmnyApi {
    
    ThemeMapper themeMapper;
    
    SampleMapper sampleMapper;
    
    Gson gson = new Gson();

    @Override
    public String getBasePath() {
        return ThemeApiConstants.base+"/{theme}/samples";
    }

    @Override
    public String[] getVersions() {
        return ThemeApiConstants.versions;
    }

    public ApiResponse getResponse(RequestResponseManager requestResponseManager) {
        String theme = requestResponseManager.getPathParameter("theme");
        if(theme.equals("default")) {
            theme = themeMapper.getDefaultTheme(requestResponseManager.getRequestHostname(), requestResponseManager.getDatabaseQuerier());
        }
        StorageSystem storageSystem = new StorageSystem(requestResponseManager.getStorageDevice());
        Collection<Sample> samples = sampleMapper.getSamples(theme,requestResponseManager.getRequestHostname(), storageSystem);
        return new ApiResponse(samples,200);
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
