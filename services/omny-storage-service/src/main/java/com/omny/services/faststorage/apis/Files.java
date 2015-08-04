package com.omny.services.faststorage.apis;

import ca.omny.documentdb.IDocumentQuerier;
import ca.omny.request.api.ApiResponse;
import ca.omny.request.api.OmnyApi;
import ca.omny.request.management.RequestResponseManager;
import ca.omny.storage.IStorage;
import ca.omny.storage.StorageSystem;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.google.gson.Gson;
import com.omny.services.faststorage.mappers.StorageMapper;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Context;
import org.apache.commons.codec.binary.Base64;

public class Files implements OmnyApi {

    Gson gson;
    
    @Inject
    IDocumentQuerier querier;
    
    @Inject
    StorageMapper storageMapper;

    @Inject
    StorageSystem storageSystem;
    
    @Inject
    IStorage storage;

    public Files() {
        gson = new Gson();
    }

    private String getExtension(String file) {
        String[] parts = file.split("\\.");
        if (parts.length == 0) {
            return file;
        }
        return parts[parts.length - 1];
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
        String file = requestResponseManager.getQueryStringParameter("file");
        if (file == null) {
            return null;
        }
        String path = file;
        int split = path.indexOf("?");
        if (split > -1) {
            path = path.substring(0, split);
        }
        String hostname = requestResponseManager.getRequestHostname();
        path =  hostname + "/" + path;

        ObjectMetadata metadata = storageMapper.getMetadata(file);
        String extension = this.getExtension(path);
        
        Object fileContents = storageSystem.getFileContents(path, hostname);
        if (fileContents == null) {
            throw new NotFoundException();
        }
        if (fileContents instanceof String) {
            try {
                fileContents = new Gson().fromJson(fileContents.toString(), String.class);
            } catch (com.google.gson.JsonSyntaxException syntaxException) {
            }
        }

        requestResponseManager.getResponse().addHeader("Content-Type", metadata.getContentType());
        return new ApiResponse(fileContents, 200);

    }

    public ApiResponse postResponse(RequestResponseManager requestResponseManager) {
        String fileContents = requestResponseManager.getRequest().getParameter("file");
        boolean base64Encoded = Boolean.valueOf(requestResponseManager.getRequest().getParameter("base64"));
        String path = requestResponseManager.getRequest().getParameter("path");
        Object contents = fileContents;
        if (base64Encoded) {
            contents = Base64.decodeBase64(fileContents.toString());
        }
        

        String[] parts = path.split("/");
        String fileName = parts[parts.length - 1];
        ObjectMetadata metadata = storageMapper.getMetadata(fileName);

        storageSystem.saveFile(path, contents, requestResponseManager.getRequestHostname());

        return new ApiResponse("", 200);
    }

    public ApiResponse putResponse(RequestResponseManager requestResponseManager) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public ApiResponse deleteResponse(RequestResponseManager requestResponseManager) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
