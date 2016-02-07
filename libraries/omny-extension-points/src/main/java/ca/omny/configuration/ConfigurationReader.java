package ca.omny.configuration;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

public class ConfigurationReader {
    
    HashMap<String,String> cache = new HashMap<String, String>();
    HashMap<String,String> keyCache = new HashMap<String, String>();
    
    static ConfigurationReader defaultConfigurationReader;

    public static ConfigurationReader getDefaultConfigurationReader() {
        if(defaultConfigurationReader==null) {
            defaultConfigurationReader = new ConfigurationReader();
        }
        return defaultConfigurationReader;
    }
    
    public ConfigurationReader() {
        File masterConfigDirectory = findRootDirectory("config");
        if(keyCache.size()==0 && masterConfigDirectory!=null) {
            try {
                File configFile = new File(masterConfigDirectory.getAbsolutePath()+"/config.json");
                if(configFile!=null&&configFile.exists()) {
                    String configString = FileUtils.readFileToString(configFile);
                    Gson gson = new Gson();
                    keyCache = gson.fromJson(configString, HashMap.class);
                }
            } catch (IOException ex) {
                Logger.getLogger(ConfigurationReader.class.getName()).log(Level.SEVERE, null, ex);
            }  
        }
    }
    
    public void loadFromFile(String file) throws IOException {
        Gson gson = new Gson();
        String contents = FileUtils.readFileToString(new File(file));
        Map<String,String> parameters = gson.fromJson(contents, Map.class);
        for(String key: parameters.keySet()) {
            this.setKey(key, parameters.get(key).toString());
        }
    }
    
    public void setKey(String key, String value) {
        keyCache.put(key, value);
    }
    
    public String getConfigurationString(String name) {
        if(System.getenv(name)!=null) {
            return System.getenv(name);
        }
        if(keyCache.containsKey(name)) {
            return keyCache.get(name);
        }
        
        String file = "/etc/omny/"+name+".conf";
        String os = System.getProperty("os.name");
        if(os.contains("Windows")) {
            file = System.getenv("SystemDrive")+"/omny/"+name+".conf"; 
        }
        
        return this.readFile(file);
    }
    
    public static File findRootDirectory(String name) {
        File currentFile = new File(".").getAbsoluteFile();
        File searchFolder = new File(currentFile.getAbsolutePath()+"/"+name);
        int maxSearch = 10;
        while(!searchFolder.exists()) {
            maxSearch--;
            currentFile = currentFile.getParentFile();
            if(currentFile==null) { 
                return null;
            }
            searchFolder = new File(currentFile.getAbsolutePath()+"/"+name);
            if(maxSearch<=0) {
                return null;
            }
        }
        return searchFolder.getAbsoluteFile();
    }
    
    public String getSimpleConfigurationString(String name) {
        final String configurationString = this.getConfigurationString(name);
        if(configurationString==null) {
            return null;
        }
        return configurationString.replaceAll("\r", "").replaceAll("\n", "");
    }
    
    public static String readAll(InputStream stream) {
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        return readAll(br);
    }
    
    public static void readAllToBuilder(InputStream stream, StringBuilder sb) {
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        readAllToStringBuilder(br,sb);
    }
    
    private String readFile(String fileName) {
        if(cache.containsKey(fileName)) {
            return cache.get(fileName);
        }
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            final String result = this.readAll(br);
            cache.put(fileName, result);
            return result;
        } catch (FileNotFoundException ex) {
            //Logger.getLogger(ConfigurationReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static String readAll(BufferedReader br) {
        StringBuilder sb = new StringBuilder();
        try {
            
            String next = br.readLine();
            while(next!=null) {
                sb.append(next);
                next = br.readLine();
                if(next!=null) {
                    sb.append("\n");
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ConfigurationReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sb.toString().trim();
    }
    
    public static void readAllToStringBuilder(BufferedReader br, StringBuilder sb) {
        try {   
            String next = br.readLine();
            while(next!=null) {
                sb.append(next);
                next = br.readLine();
                if(next!=null) {
                    sb.append("\n");
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ConfigurationReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
