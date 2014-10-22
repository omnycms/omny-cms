package com.omny.services.faststorage.apis;

import ca.omny.documentdb.IDocumentQuerier;
import ca.omny.request.api.ApiResponse;
import ca.omny.request.api.OmnyApi;
import ca.omny.request.management.RequestResponseManager;
import ca.omny.storage.IStorage;
import ca.omny.storage.StorageSystem;
import com.omny.services.faststorage.mappers.StorageMapper;
import java.util.Collection;
import javax.inject.Inject;

public class Folders implements OmnyApi {

    @Inject
    IDocumentQuerier querier;

    @Inject
    StorageSystem storageSystem;
    
    @Inject
    IStorage storage;
    
    @Inject
    StorageMapper storageMapper;

    public Folders() {
    }

    @Override
    public String getBasePath() {
        return StorageApiConstants.base+"/files";
    }

    @Override
    public String[] getVersions() {
        return StorageApiConstants.versions;
    }

    public ApiResponse getResponse(RequestResponseManager requestResponseManager) {
        String path = requestResponseManager.getQueryStringParameter("path");
        boolean recursive = Boolean.valueOf(requestResponseManager.getQueryStringParameter("recursive"));
        String fileSuffix = requestResponseManager.getQueryStringParameter("suffix");
        if (!path.endsWith("/") && !path.isEmpty()) {
            path = path + "/";
        }
        
        Collection<String> files = storageSystem.listFiles(path, recursive, fileSuffix, requestResponseManager.getRequestHostname());
        return new ApiResponse(files, 200);
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
