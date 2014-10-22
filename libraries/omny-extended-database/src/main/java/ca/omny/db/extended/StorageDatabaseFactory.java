package ca.omny.db.extended;

import ca.omny.documentdb.IDocumentQuerier;
import ca.omny.documentdb.IDocumentQuerierFactory;
import ca.omny.storage.IStorage;
import javax.inject.Inject;

public class StorageDatabaseFactory implements IDocumentQuerierFactory {

    StorageBasedDatabase db;
    
    @Inject
    IStorage storage;
    
    @Override
    public IDocumentQuerier getInstance() {
        String dbType = System.getenv("omny_db_type");
        if(dbType!=null && dbType.equals("storage")) {
            if(db==null) {
                db = new StorageBasedDatabase();
                db.setStorage(storage);
            }
            return db;
        }
        return null;
    }
    
}
