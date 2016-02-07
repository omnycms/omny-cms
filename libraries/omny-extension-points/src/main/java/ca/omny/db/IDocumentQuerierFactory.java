package ca.omny.db;

import ca.omny.db.IDocumentQuerier;

public interface IDocumentQuerierFactory {
    public IDocumentQuerier getInstance();
}
