package com.omny.services.faststorage.mappers;

import ca.omny.documentdb.IDocumentQuerier;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.omny.credentials.CredentialManager;
import com.omny.services.faststorage.models.MimeType;
import java.net.URLConnection;
import java.util.Date;
import javax.inject.Inject;

public class StorageMapper {
    
    @Inject
    IDocumentQuerier querier;
    
    @Inject
    CredentialManager cm;
    
    public ObjectMetadata getMetadata(String fileName) {
        ObjectMetadata md = new ObjectMetadata();
        
        String contentType=URLConnection.guessContentTypeFromName(fileName);
        String extension = fileName.substring(fileName.lastIndexOf(".")+1);
        if(extension.contains("?")) {
            extension = extension.substring(0,extension.indexOf("?"));
        }
        if(contentType==null) {
            try {
                contentType = querier.get("mimetypes::"+extension, MimeType.class).getMimeType();
            } catch(Exception e) { }
        }
        md.setContentType(contentType);
        return md;
    }
    
    public Date getExpirationDate(long expiration) {
        Date date = new Date();
        date = new Date(date.getTime() + expiration);
        return date;
    }
}
