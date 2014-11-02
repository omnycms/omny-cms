package com.omny.services.extensibility.apis;

import java.util.HashMap;
import java.util.Map;

public class Mimedata {
    Map<String, String> mimeTypes;
    
    public String getContentType(String extension) {
        if (mimeTypes == null) {
            initializeMimeTypes();
        }
        if (mimeTypes.containsKey(extension)) {
            return mimeTypes.get(extension);
        }

        return "";
    }
    
    public String getContentTypeFromName(String fileName) {
        String[] parts = fileName.split("\\.");

        if (parts.length == 0) {
            return fileName;
        }
        String extension = parts[parts.length - 1];
        return getContentType(extension);

    }

    private void initializeMimeTypes() {
        mimeTypes = new HashMap<String, String>();
        mimeTypes.put("html", "text/html");
        mimeTypes.put("css", "text/css");
        mimeTypes.put("js", "application/javascript");
        mimeTypes.put("json", "application/json");
        mimeTypes.put("png", "image/png");
    }
}
