package ca.omny.services.extensibility.apis;

import ca.omny.configuration.ConfigurationReader;
import ca.omny.request.api.ApiResponse;
import ca.omny.request.api.OmnyApi;
import ca.omny.request.management.RequestResponseManager;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.google.gson.Gson;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.inject.Inject;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;

public class UiModules implements OmnyApi {

    ConfigurationReader configurationReader;
    AmazonStorageConfig config;
    AmazonS3Client s3Client;
    Mimedata mimeData;

    Gson gson = new Gson();

    public UiModules() {
        configurationReader = ConfigurationReader.getDefaultConfigurationReader();
    }

    @Override
    public String getBasePath() {
        return ExtensibilityApiConstants.base + "/ui/modules/{module}/{version}";
    }

    @Override
    public String[] getVersions() {
        return ExtensibilityApiConstants.versions;
    }

    @Override
    public ApiResponse getResponse(RequestResponseManager requestResponseManager) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ApiResponse postResponse(RequestResponseManager requestResponseManager) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ApiResponse putResponse(RequestResponseManager requestResponseManager) {
        if (config == null) {
            String configurationString = configurationReader.getConfigurationString("UI_MODULE_CDN");
            config = gson.fromJson(configurationString, AmazonStorageConfig.class);
            s3Client = new AmazonS3Client(config);
            mimeData = new Mimedata();
        }
        try {
            DiskFileItemFactory factory = new DiskFileItemFactory();

            File repository = new File(System.getProperty("java.io.tmpdir"));
            factory.setRepository(repository);

            ServletFileUpload servletFileUpload = new ServletFileUpload(factory);
            List<FileItem> files = servletFileUpload.parseRequest(requestResponseManager.getRequest());
            for (FileItem file : files) {
                if (file.getFieldName().equals("file")) {
                    String provider = requestResponseManager.getRequestHostname();
                    String module = requestResponseManager.getPathParameter("module");
                    String version = requestResponseManager.getPathParameter("version");
                    final String format = "%s/%s/%s/%s";
                    ZipInputStream zis = new ZipInputStream(file.getInputStream());
                    //get the zipped file list entry
                    for (ZipEntry ze = zis.getNextEntry(); ze != null; ze = zis.getNextEntry()) {
                        if(!ze.isDirectory()) {
                            String fileName = ze.getName();
                            String key = String.format(format, provider, module, version, fileName);
                            File tempFile = File.createTempFile("uitemp", fileName);
                            IOUtils.copy(zis, new FileOutputStream(tempFile));
                            s3Client.putObject(config.getBucket(), key, tempFile);
                            tempFile.delete();
                        }
                    }
                    return new ApiResponse("", 200);
                }
            }
        } catch (FileUploadException ex) {
            Logger.getLogger(UiModules.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(UiModules.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ApiResponse("", 500);
    }

    @Override
    public ApiResponse deleteResponse(RequestResponseManager requestResponseManager) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private ObjectMetadata constructMetaData(String name) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setLastModified(new Date());
        metadata.setContentType(mimeData.getContentTypeFromName(name));
        return metadata;
    }

}
