package ca.omny.storage;

import ca.omny.configuration.ConfigurationReader;
import ca.omny.logger.OmnyLogger;
import ca.omny.logger.SimpleLogger;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

public class StorageSystem {

    static OmnyLogger logger = new SimpleLogger(StorageSystem.class.getName());
    
    IStorage storageImplementation;
    
    public StorageSystem() {
        storageImplementation = StorageFactory.getDefaultStorage();
    }

    @Inject
    public void injectServiceClient(IStorage storageImplementation) {
        this.storageImplementation = storageImplementation;
    }

    public void setLogger(OmnyLogger logger) {
        this.logger = logger;
    }

    public String getExtension(String filename) {
        int start = filename.lastIndexOf("\\.");
        if (start > -1) {
            String result = filename.substring(start);
        }
        return filename;
    }

    public String getFileContents(String location, String hostname) {
        return storageImplementation.getFileContents(hostname + "/" + location);
    }

    public Object getFileContents(String location, String hostname, boolean isJson) {
        return storageImplementation.getFileContents(hostname + "/" + location, isJson);
    }

    public InputStream getFileStream(String location, String hostname) {
        return new ByteArrayInputStream(this.getFileContents(hostname + "/" + location, hostname).getBytes());
    }

    public <T extends Object> Collection<T> fetchManyAsType(Collection<String> files, String hostname, Class<T> type) {
        return storageImplementation.fetchManyAsType(files, hostname, type);
    }

    public void saveFile(String path, InputStream contents, String hostname) {
        this.saveFile(path, ConfigurationReader.readAll(contents), hostname);
    }

    public MetaData getMetaData(String path, String host) {
        return storageImplementation.getMetaData(host + "/" + path);
    }

    private long getUnixTime() {
        long unixTime = System.currentTimeMillis() / 1000L;
        return unixTime;
    }

    public void saveFileRaw(String path, byte[] contents, String hostname) {
        storageImplementation.saveFileRaw(hostname + "/" + path, contents);
    }

    public void saveFile(String path, Object contents, String hostname) {
        storageImplementation.saveFile(hostname + "/" + path, contents);
    }

    public void copyFile(String oldPath, String newPath, String hostname) {
        Object contents = storageImplementation.getFileContents(hostname + "/" + oldPath);
        storageImplementation.saveFile(hostname + "/" + newPath, contents);
    }

    public Collection<String> listFiles(String location, String hostname) {
        return this.listFiles(location, false, hostname);
    }

    public Collection<String> listFiles(String location, boolean recursive, String hostname) {
        return this.listFiles(location, recursive, "", hostname);
    }

    public Collection<String> listFiles(String location, boolean recursive, String suffix, String host) {
        return storageImplementation.getFileList(host + "/" + location, recursive, suffix);
    }

    public Map<String, List<String>> getHeadersFromUrl(String url) throws IOException {
        HttpURLConnection connection
                = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("HEAD");
        return connection.getHeaderFields();
    }

}
