package ca.omny.utilities.providers;

import ca.omny.configuration.ConfigurationReader;
import ca.omny.configuration.IEnvironmentToolsProvider;
import ca.omny.db.IDocumentQuerier;
import ca.omny.db.extended.DynamoDb;
import ca.omny.db.extended.StorageBasedDatabase;
import ca.omny.documentdb.CouchFactory;
import ca.omny.request.RequestInput;
import ca.omny.storage.IStorage;
import ca.omny.storage.implementation.DocumentDatabaseStorage;
import ca.omny.storage.implementation.LocalStorage;
import ca.omny.storage.implementation.S3Storage;
import java.io.File;

public class FlexibleToolsProvider implements IEnvironmentToolsProvider {

    IStorage defaultStorage;
    ConfigurationReader configurationReader;
    IDocumentQuerier querier;

    public FlexibleToolsProvider(ConfigurationReader configurationReader) {
        this.configurationReader = configurationReader;
    }

    @Override
    public IStorage getDefaultStorage() {
        if (defaultStorage == null) {
            String storageSystemName = configurationReader.getSimpleConfigurationString("OMNY_STORAGE_SYSTEM");
            if(storageSystemName==null) {
                storageSystemName = "";
            }
            switch (storageSystemName) {
                case "DATABASE":
                    DocumentDatabaseStorage documentDbStorage = new DocumentDatabaseStorage();
                    documentDbStorage.setConfigurationReader(configurationReader);
                    documentDbStorage.setQuerier(this.getDefaultDocumentQuerier());
                    defaultStorage = documentDbStorage;
                    break;
                case "S3":
                    defaultStorage = new S3Storage(configurationReader);
                    break;
                default:
                    String rootFolder = configurationReader.getSimpleConfigurationString("OMNY_LOCAL_FOLDER");
                    if (rootFolder == null || rootFolder.equals(".")) {
                        File findRootDirectory = ConfigurationReader.findRootDirectory("storage");
                        if (findRootDirectory != null) {
                            rootFolder = findRootDirectory.getAbsolutePath();
                        } else {
                            rootFolder = ".";
                        }
                    }   LocalStorage localStorage = new LocalStorage(rootFolder);
                    defaultStorage = localStorage;
                    break;
            }
        }
        return defaultStorage;
    }

    @Override
    public IStorage getStorage(RequestInput request) {
        return getDefaultStorage();
    }

    @Override
    public IDocumentQuerier getDefaultDocumentQuerier() {
        if (querier == null) {
            String querierName = configurationReader.getSimpleConfigurationString("OMNY_DB_TYPE");
            if (querierName == null) {
                querierName = "";
            }
            switch (querierName) {
                case "COUCHBASE":
                    querier = CouchFactory.getQuerier();
                    break;
                case "DYNAMODB":
                    querier = new DynamoDb(configurationReader);
                    break;
                default:
                    querier = new StorageBasedDatabase(getDefaultStorage());
                    break;
            }
        }
        return querier;
    }

    @Override
    public IDocumentQuerier getDocumentQuerier(RequestInput request) {
        return getDefaultDocumentQuerier();
    }

}
