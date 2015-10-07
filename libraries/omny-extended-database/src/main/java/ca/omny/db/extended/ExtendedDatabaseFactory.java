package ca.omny.db.extended;

import ca.omny.configuration.ConfigurationReader;
import ca.omny.documentdb.IDocumentQuerier;
import ca.omny.documentdb.IDocumentQuerierFactory;
import ca.omny.documentdb.QuerierFactory;
import ca.omny.storage.IStorage;
import ca.omny.storage.StorageFactory;
import javax.inject.Inject;

public class ExtendedDatabaseFactory implements IDocumentQuerierFactory {

    IDocumentQuerier db;

    @Inject
    IStorage storage;

    @Inject
    ConfigurationReader configurationReader;

    static {
        QuerierFactory.registerFactory(new ExtendedDatabaseFactory());
    }
    
    public static void register() {
        
    }
    
    public ExtendedDatabaseFactory() {
        this(StorageFactory.getDefaultStorage(), ConfigurationReader.getDefaultConfigurationReader());
    }
    
    public ExtendedDatabaseFactory(IStorage storage, ConfigurationReader configurationReader) {
        this.storage = storage;
        this.configurationReader = configurationReader;
    }
        
    
    @Override
    public IDocumentQuerier getInstance() {
        if(db!=null) {
            return db;
        }
        String dbType = configurationReader.getSimpleConfigurationString("OMNY_DB_TYPE");
        if (dbType != null) {
            if(dbType.equals("couchbase")) {
                return null;
            } else if (dbType.equals("dynamodb")) {
                db = new DynamoDb(configurationReader);
                return db;
            }
        }
        StorageBasedDatabase sdb = new StorageBasedDatabase();
        sdb.setStorage(storage);
        db = sdb;
        return db;
    }

}
