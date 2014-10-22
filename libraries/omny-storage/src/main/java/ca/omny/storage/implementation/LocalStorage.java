package ca.omny.storage.implementation;

import com.google.gson.Gson;
import ca.omny.storage.IStorage;
import ca.omny.storage.MetaData;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.inject.Alternative;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

@Alternative
public class LocalStorage implements IStorage {

    String rootFolder;
    Gson gson = new Gson();

    public LocalStorage(String rootFolder) {
        this.rootFolder = rootFolder;
    }

    @Override
    public void copyFile(String source, String destination) {
        try {
            FileUtils.copyFile(this.getFile(source), this.getFile(destination));
        } catch (IOException ex) {
            Logger.getLogger(LocalStorage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void copyFolder(String source, String destination) {
        try {
            FileUtils.copyDirectory(this.getFile(source), this.getFile(destination));
        } catch (IOException ex) {
            Logger.getLogger(LocalStorage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public <T> Collection<T> fetchManyAsType(Collection<String> files, String basePath, Class<T> type) {
        LinkedList<T> results = new LinkedList<T>();
        if (!basePath.isEmpty() && !basePath.endsWith("/")) {
            basePath += "/";
        }
        for (String key : files) {
            File f = new File(basePath + key);
            if (!f.isDirectory()) {
                String contents = this.getFileContents(basePath + key);
                if (contents != null) {
                    results.add(gson.fromJson(contents, type));
                }
            }
        }
        return results;
    }

    @Override
    public String getFileContents(String fullPath) {
        if (fullPath == null) {
            return null;
        }
        Object contents = this.getFileContents(fullPath, fullPath.endsWith(".json"));
        if (contents == null) {
            return null;
        }
        return contents.toString();
    }

    public static boolean isValidUTF8(byte[] input) {
        CharsetDecoder cs = Charset.forName("UTF-8").newDecoder();
        try {
            cs.decode(ByteBuffer.wrap(input));
            return true;
        } catch (CharacterCodingException e) {
            return false;
        }
    }

    @Override
    public Object getFileContents(String fullPath, boolean isJson) {
        if (!isJson) {
            try {
                byte[] bytes = FileUtils.readFileToByteArray(this.getFile(fullPath));
                if (isValidUTF8(bytes)) {
                    return new String(bytes);
                }
                return bytes;
            } catch (IOException ex) {
                Logger.getLogger(LocalStorage.class.getName()).log(Level.INFO, null, ex);
            }
        }
        try {
            return IOUtils.toString(new FileInputStream(rootFolder + "/" + fullPath));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(LocalStorage.class.getName()).log(Level.INFO, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(LocalStorage.class.getName()).log(Level.INFO, null, ex);
        }
        return null;
    }

    @Override
    public Collection<String> getFileList(String path, boolean recursive, String fileSuffix) {
        return this.getFileList(path, path, recursive, fileSuffix);
    }

    private Collection<String> getFileList(String rootPath, String path, boolean recursive, String fileSuffix) {
        if (!rootPath.endsWith("/")) {
            rootPath += "/";
        }
        if (!path.endsWith("/")) {
            path += "/";
        }
        File file = this.getFile(path);

        String relativePath = "";
        if (path.length() > rootPath.length()) {
            relativePath = path.substring(rootPath.length());
        }
        Collection<String> files = new LinkedList<String>();
        if (!file.isDirectory()) {
            return files;
        }
        for (File subfile : file.listFiles()) {
            if (subfile.getName().equals(".DS_Store")) {
                continue;
            }
            if (fileSuffix == null || subfile.getName().endsWith(fileSuffix)) {
                files.add(relativePath + subfile.getName());
            }
            if (subfile.isDirectory() && recursive) {
                files.addAll(this.getFileList(rootPath, path + subfile.getName() + "/", recursive, fileSuffix));
            }
        }
        return files;
    }

    @Override
    public MetaData getMetaData(String path) {
        MetaData metadata = new MetaData();
        long lastModified = this.getFile(path).lastModified();
        metadata.setLastModified(lastModified);

        return metadata;
    }

    @Override
    public void saveFile(String path, Object contents) {
        String output = null;
        if (contents instanceof String) {
            output = contents.toString();
        } else {
            output = gson.toJson(contents);
        }
        try {
            FileUtils.writeStringToFile(this.getFile(path), output);
        } catch (IOException ex) {
            Logger.getLogger(LocalStorage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private File getFile(String path) {
        return new File(rootFolder + "/" + path);

    }

    @Override
    public void saveFileRaw(String path, byte[] contents) {
        try {
            FileUtils.writeByteArrayToFile(this.getFile(path), contents);
        } catch (IOException ex) {
            Logger.getLogger(LocalStorage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void saveFileRaw(String path, Object contents) {
        this.saveFile(path, contents);
    }

    @Override
    public void delete(String source) {
        File file = this.getFile(source);
        file.delete();
    }

    @Override
    public void deleteFolder(String source) {
        File file = this.getFile(source);
        try {
            FileUtils.deleteDirectory(file);
        } catch (IOException ex) {
            Logger.getLogger(LocalStorage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
