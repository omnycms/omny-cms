package ca.omny.configuration;

import ca.omny.db.IDocumentQuerier;
import ca.omny.request.RequestInput;
import ca.omny.storage.IStorage;

public interface IEnvironmentToolsProvider {
    
    public IStorage getDefaultStorage();
    public IStorage getStorage(RequestInput request);
    
    public IDocumentQuerier getDefaultDocumentQuerier();
    public IDocumentQuerier getDocumentQuerier(RequestInput request);
}
