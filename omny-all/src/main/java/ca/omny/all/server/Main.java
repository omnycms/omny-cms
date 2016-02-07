package ca.omny.all.server;

import ca.omny.all.OmnyAllInOneServer;
import ca.omny.all.configuration.DatabaseConfigurationValues;
import ca.omny.all.configuration.DatabaseConfigurer;
import ca.omny.configuration.ConfigurationReader;
import ca.omny.server.OmnyClassRegister;
import com.google.gson.Gson;
import java.io.File;
import java.io.FileInputStream;
import java.util.Map;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class Main {

    private static ConfigurationReader configurationReader = ConfigurationReader.getDefaultConfigurationReader();

    public static void main(String[] args) throws Exception {
        Options options = new Options();
        options.addOption("c","configure",true,"Configure the database");
        options.addOption("e","environment-configuration-file",true,"JSON file to configure the environment");
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse( options, args);
        if(cmd.hasOption("e")) {
            String file = cmd.getOptionValue("e");
            configurationReader.loadFromFile(file);
        }
        configurationReader.setKey("OMNY_LOAD_CLASSES", "[\"ca.omny.db.extended.ExtendedDatabaseFactory\",\"ca.omny.services.sites.apis.RegisterApis\",\"ca.omny.potent.RegisterApis\",\"ca.omny.content.apis.RegisterApis\",\"ca.omny.services.extensibility.apis.RegisterApis\",\"ca.omny.services.menus.apis.RegisterApis\",\"ca.omny.services.pages.apis.RegisterApis\",\"ca.omny.themes.apis.RegisterApis\"]");
        OmnyClassRegister classRegister = new OmnyClassRegister();
        classRegister.loadFromEnvironment();
        
        if (cmd.hasOption("c")) {
            //read from JSON file
            String configString = IOUtils.toString(new FileInputStream(new File(cmd.getOptionValue("c"))));
            Gson gson = new Gson();
            DatabaseConfigurationValues configurationValues = gson.fromJson(configString, DatabaseConfigurationValues.class);
            DatabaseConfigurer configurer = new DatabaseConfigurer();
            configurer.configure(configurationValues);
        }
        
        OmnyAllInOneServer server = new OmnyAllInOneServer();
        server.start();
    }
}
