package ca.omny.server;

import ca.omny.configuration.ConfigurationReader;
import com.google.gson.Gson;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OmnyClassRegister {
    
    public void loadFromEnvironment() {
        Gson gson = new Gson();
        String loadClassesConfig = ConfigurationReader.getDefaultConfigurationReader().getSimpleConfigurationString("OMNY_LOAD_CLASSES");
            if(loadClassesConfig!=null) {
                Collection<String> classesToLoad = gson.fromJson(loadClassesConfig, Collection.class);
                for(String className: classesToLoad) {
                    loadClass(className);
                }
            }
    }
    
    public void loadClass(String className) {
        try {
            ClassLoader classLoader = this.getClass().getClassLoader();
            Class<?> loadClass = classLoader.loadClass(className);
            if (loadClass != null) {
                loadClass.newInstance();
            }
        } catch (InstantiationException ex) {
            Logger.getLogger(OmnyHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(OmnyHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(OmnyHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
