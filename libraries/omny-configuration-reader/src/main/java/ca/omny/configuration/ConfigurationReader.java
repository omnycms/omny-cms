package ca.omny.configuration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigurationReader {
    
    HashMap<String,String> cache = new HashMap<String, String>();
    
    public String getConfigurationString(String name) {
        if(System.getenv(name)!=null) {
            return System.getenv(name);
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
