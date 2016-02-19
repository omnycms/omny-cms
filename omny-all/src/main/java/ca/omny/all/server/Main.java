package ca.omny.all.server;

import ca.omny.request.ApiCollector;
import ca.omny.all.OmnyAllInOneServer;
import ca.omny.all.configuration.DatabaseConfigurationValues;
import ca.omny.all.configuration.DatabaseConfigurer;
import ca.omny.auth.apis.AuthApiRegistrar;
import ca.omny.configuration.ConfigurationReader;
import ca.omny.configuration.IEnvironmentToolsProvider;
import ca.omny.content.apis.ContentApiRegistrar;
import ca.omny.services.extensibility.apis.ExtensibilityApiRegistrar;
import ca.omny.services.menus.apis.MenuApiRegistrar;
import ca.omny.services.pages.apis.PageApiRegistrar;
import ca.omny.services.sites.apis.registration.SiteApiRegistrar;
import ca.omny.themes.apis.ThemeApiRegistrar;
import com.google.gson.Gson;
import java.io.File;
import java.io.FileInputStream;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.io.IOUtils;
import ca.omny.utilities.providers.FlexibleToolsProvider;
import ca.omny.services.ui.apis.UiApiRegistrar;

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
        
        IEnvironmentToolsProvider provider = new FlexibleToolsProvider(configurationReader);
        if (cmd.hasOption("c")) {
            //read from JSON file
            String configString = IOUtils.toString(new FileInputStream(new File(cmd.getOptionValue("c"))));
            Gson gson = new Gson();
            DatabaseConfigurationValues configurationValues = gson.fromJson(configString, DatabaseConfigurationValues.class);
            DatabaseConfigurer configurer = new DatabaseConfigurer(provider.getDefaultDocumentQuerier());
            configurer.configure(configurationValues);
        }
        ApiCollector apiCollector = new ApiCollector(
                new SiteApiRegistrar(),
                new AuthApiRegistrar(),
                new ContentApiRegistrar(),
                new ExtensibilityApiRegistrar(),
                new PageApiRegistrar(),
                new ThemeApiRegistrar(),
                new MenuApiRegistrar(),
                new UiApiRegistrar()
        );
        OmnyAllInOneServer server = new OmnyAllInOneServer(provider, apiCollector.getApis());
        server.start();
    }
}
