package ca.omny.lambda.wrapper;

import ca.omny.configuration.ConfigurationReader;
import ca.omny.configuration.IEnvironmentToolsProvider;
import ca.omny.db.IDocumentQuerier;
import ca.omny.db.extended.DynamoDb;
import ca.omny.db.extended.StorageBasedDatabase;
import ca.omny.request.RequestInput;
import ca.omny.storage.IStorage;
import ca.omny.storage.implementation.S3Storage;

public class AwsToolsProvider implements IEnvironmentToolsProvider {

    IStorage defaultStorage;
    ConfigurationReader configurationReader;
    IDocumentQuerier querier;

    public AwsToolsProvider() {
        configurationReader = ConfigurationReader.getDefaultConfigurationReader();
    }

    @Override
    public IStorage getDefaultStorage() {
        if (defaultStorage == null) {
            defaultStorage = new S3Storage(configurationReader);
        }
        return defaultStorage;
    }

    @Override
    public IStorage getStorage(RequestInput request) {
        return getDefaultStorage();
    }

    @Override
    public IDocumentQuerier getDefaultDocumentQuerier() {
        if(querier == null) {
            String querierName = configurationReader.getSimpleConfigurationString("OMNY_DB_TYPE");
            if(querierName == null) {
                querierName = "";
            }
            switch (querierName) {
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
