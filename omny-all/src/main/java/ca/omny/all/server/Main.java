package ca.omny.all.server;

import ca.omny.all.OmnyAllInOneServer;
import ca.omny.all.configuration.DatabaseConfigurationValues;
import ca.omny.all.configuration.DatabaseConfigurer;
import ca.omny.configuration.ConfigurationReader;
import ca.omny.server.OmnyClassRegister;
import com.google.gson.Gson;
import java.io.File;
import java.io.FileInputStream;
import org.apache.commons.io.IOUtils;

public class Main {

    private static ConfigurationReader configurationReader = ConfigurationReader.getDefaultConfigurationReader();

    public static void main(String[] args) throws Exception {
        configurationReader.setKey("OMNY_LOAD_CLASSES", "[\"ca.omny.db.extended.ExtendedDatabaseFactory\",\"ca.omny.services.sites.apis.RegisterApis\",\"ca.omny.potent.RegisterApis\",\"ca.omny.content.apis.RegisterApis\",\"ca.omny.services.extensibility.apis.RegisterApis\",\"ca.omny.services.menus.apis.RegisterApis\",\"ca.omny.services.pages.apis.RegisterApis\",\"ca.omny.themes.apis.RegisterApis\"]");
        OmnyClassRegister classRegister = new OmnyClassRegister();
        classRegister.loadFromEnvironment();
        if (args.length > 1 && args[0].equals("configure")) {
            //read from JSON file
            String configString = IOUtils.toString(new FileInputStream(new File(args[1])));
            Gson gson = new Gson();
            DatabaseConfigurationValues configurationValues = gson.fromJson(configString, DatabaseConfigurationValues.class);
            DatabaseConfigurer configurer = new DatabaseConfigurer();
            configurer.configure(configurationValues);
        }

        OmnyAllInOneServer server = new OmnyAllInOneServer();
        server.start();
    }
}
