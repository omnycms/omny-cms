package ca.omny.lambda.wrapper;

import ca.omny.configuration.ConfigurationReader;
import ca.omny.request.OmnyBaseHandler;
import ca.omny.request.RequestInput;
import ca.omny.request.RequestResponseManager;
import ca.omny.request.ResponseOutput;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LambdaWrapper {
    
    OmnyBaseHandler omnyHandler;
    RequestInput input;
    
    public LambdaWrapper(RequestInput input) {
        this(input, new OmnyBaseHandler(new AwsToolsProvider()));
    }
    
    public LambdaWrapper(RequestInput input, OmnyBaseHandler handler) {
        this.omnyHandler = handler;
        this.input = input;
        ConfigurationReader defaultConfigurationReader = ConfigurationReader.getDefaultConfigurationReader();
        defaultConfigurationReader.setKey("OMNY_NO_INJECTION", "true");
        if(input.getConfigOverrides()!=null) {
            for(String key: input.getConfigOverrides().keySet()) {
                String value = input.getConfigOverrides().get(key);
                defaultConfigurationReader.setKey(key, value);
            }
        }
    }
    
    public String handleRequest() {
        try {
            ResponseOutput response = new ResponseOutput();
            RequestResponseManager manager = new RequestResponseManager(input, response, new AwsToolsProvider());
            
            omnyHandler.handle(manager);
            Gson gson = new Gson();
            response.writeBody();
            return gson.toJson(response);
        } catch (IOException ex) {
            Logger.getLogger(LambdaWrapper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
