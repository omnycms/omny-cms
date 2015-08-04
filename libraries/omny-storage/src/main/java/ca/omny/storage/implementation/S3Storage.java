package ca.omny.storage.implementation;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.google.gson.Gson;
import ca.omny.configuration.ConfigurationReader;
import ca.omny.storage.AmazonStorageConfig;
import ca.omny.storage.IStorage;
import ca.omny.storage.MetaData;
import ca.omny.storage.Mimedata;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import org.apache.commons.io.IOUtils;

@Alternative
public class S3Storage implements IStorage {

    ConfigurationReader configurationReader;

    AmazonStorageConfig config;

    AmazonS3Client s3Client;

    Gson gson = new Gson();
    Mimedata mimedata = new Mimedata();

    @Inject
    public S3Storage(ConfigurationReader configurationReader) {
        this.configurationReader = configurationReader;
        config = gson.fromJson(configurationReader.getConfigurationString("awsStorage"), AmazonStorageConfig.class);
        s3Client = new AmazonS3Client(config);
    }

    @Override
    public void copyFile(String source, String destination) {
        s3Client.copyObject(config.getBucket(), source, config.getBucket(), destination);
    }

    @Override
    public void copyFolder(String source, String destination) {
        if (!source.endsWith("/")) {
            source += "/";
        }
        if (!destination.endsWith("/")) {
            destination += "/";
        }
        Collection<String> files = this.getFileList(source, true, null);
        for (String file : files) {
            s3Client.copyObject(config.getBucket(), source + file, config.getBucket(), destination + file);
        }
    }

    @Override
    public <T> Collection<T> fetchManyAsType(Collection<String> files, String basePath, Class<T> type) {
        LinkedList<T> results = new LinkedList<T>();
        if (!basePath.isEmpty() && !basePath.endsWith("/")) {
            basePath += "/";
        }
        for (String key : files) {
            String contents = this.getFileContents(basePath + key);
            results.add(gson.fromJson(contents, type));
        }
        return results;
    }

    @Override
    public String getFileContents(String fullPath) {
        Object result = this.getFileContents(fullPath, false);
        return result == null ? null : result.toString();
    }

    @Override
    public Object getFileContents(String fullPath, boolean isJson) {
        try {
            byte[] bytes = IOUtils.toByteArray(s3Client.getObject(config.getBucket(), fullPath).getObjectContent());
            if (LocalStorage.isValidUTF8(bytes)) {
                return new String(bytes);
            }
            return bytes;
        } catch (IOException ex) {
            Logger.getLogger(LocalStorage.class.getName()).log(Level.INFO, null, ex);
        } catch (AmazonS3Exception ex) {
            Logger.getLogger(LocalStorage.class.getName()).log(Level.INFO, null, ex);
        }
        return null;
    }

    @Override
    public Collection<String> getFileList(String path, boolean recursive, String fileSuffix) {
        ListObjectsRequest listObjectRequest = new ListObjectsRequest().withBucketName(config.getBucket());
        if (!path.endsWith("/")) {
            path += "/";
        }
        listObjectRequest.withPrefix(path);
        if (!recursive) {
            listObjectRequest.withDelimiter("/");
        }
        ObjectListing listObjects = s3Client.listObjects(listObjectRequest);
        
        int prefixIndex = path.length();
        Collection<String> results = new LinkedList<String>();
        for (S3ObjectSummary summary : listObjects.getObjectSummaries()) {
            if (fileSuffix == null || summary.getKey().endsWith(fileSuffix)) {
                results.add(summary.getKey().substring(prefixIndex));
            }
        }
        
        if(!recursive) {
            for(String directory: listObjects.getCommonPrefixes()) {
                results.add(directory.substring(prefixIndex,directory.length()-1));
            }
        }
        

        return results;
    }

    @Override
    public MetaData getMetaData(String path) {
        ObjectMetadata objectMetadata = s3Client.getObjectMetadata(config.getBucket(), path);
        long time = objectMetadata.getLastModified().getTime();
        MetaData metaData = new MetaData();
        metaData.setLastModified(time / 1000);
        return metaData;
    }

    @Override
    public void saveFile(String path, Object contents) {
        this.saveFileRaw(path, contents);
    }

    private ObjectMetadata constructMetaData(String name) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setLastModified(new Date());
        metadata.setContentType(mimedata.getContentTypeFromName(name));
        return metadata;
    }

    public void saveFileRaw(String path, byte[] contents) {
        ObjectMetadata metadata = this.constructMetaData(path);
        InputStream stream = new ByteArrayInputStream((byte[]) contents);
        s3Client.putObject(config.getBucket(), path, stream, metadata);
    }

    @Override
    public void saveFileRaw(String path, Object contents) {
        ObjectMetadata metadata = this.constructMetaData(path);

        if (contents instanceof String) {
            InputStream stream = IOUtils.toInputStream((String) contents);
            s3Client.putObject(config.getBucket(), path, stream, metadata);
        }
        else if (contents instanceof byte[]) {
            this.saveFileRaw(path, (byte[]) contents);
        } else {
            InputStream stream = IOUtils.toInputStream(gson.toJson(contents));
            s3Client.putObject(config.getBucket(), path, stream, metadata);
        }
    }

    @Override
    public void delete(String source) {
        s3Client.deleteObject(config.getBucket(), source);
    }

    @Override
    public void deleteFolder(String source) {
        if (!source.endsWith("/")) {
            source += "/";
        }
        Collection<String> fileList = this.getFileList(source, true, null);
        for(String file: fileList) {
            if(file.endsWith("/")) {
                this.deleteFolder(source+file);
            } else {
                this.delete(source+file);
            }
        }
        s3Client.deleteObject(config.getBucket(), source);
    }

}
