package ca.omny.storage;

import java.util.Collection;

public interface IStorage {
    
    public void delete(String source);
    
    public void deleteFolder(String source);

    void copyFile(String source, String destination);

    void copyFolder(String source, String destination);

    <T extends Object> Collection<T> fetchManyAsType(Collection<String> files, String basePath, Class<T> type);
    
    String getFileContents(String fullPath);

    Object getFileContents(String fullPath, boolean isJson);

    Collection<String> getFileList(String path, boolean recursive, String fileSuffix);
    
    Collection<String> listFolders(String path);
    
    Collection<String> listFolders(String path, boolean recursive);

    MetaData getMetaData(String path);

    void saveFile(String path, Object contents);

    void saveFileRaw(String path, byte[] contents);
    
    void saveFileRaw(String path, Object contents);
    
}
