package com.omny.services.faststorage.mappers;

import ca.omny.documentdb.IDocumentQuerier;
import javax.inject.Inject;

public class FileMapper {
    
    @Inject
    IDocumentQuerier querier;
    
    public String getFileContents(String site,String fileName) {
        String key = querier.getKey("files",site,fileName);
        return querier.get(key, String.class);
    }
}
