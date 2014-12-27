package ca.omny.db.extended;

import ca.omny.configuration.ConfigurationReader;
import ca.omny.documentdb.IDocumentQuerier;
import ca.omny.documentdb.IDocumentQuerierFactory;
import ca.omny.storage.IStorage;
import javax.inject.Inject;

public class ExtendedDatabaseFactory implements IDocumentQuerierFactory {

    IDocumentQuerier db;

    @Inject
    IStorage storage;

    @Inject
    ConfigurationReader configurationReader;
    
    @Override
    public IDocumentQuerier getInstance() {
        String dbType = System.getenv("omny_db_type");
        if (dbType != null) {
            if (dbType.equals("storage")) {
                if (db == null) {
                    StorageBasedDatabase sdb = new StorageBasedDatabase();
                    sdb.setStorage(storage);
                    db = sdb;
                }
                return db;
            } else if (dbType.equals("dynamodb")) {
                if (db == null) {
                    db = new DynamoDb(configurationReader);
                }
                return db;
            }
        }
        return null;
    }

}
