package ca.omny.lambda.wrapper;

import ca.omny.configuration.ConfigurationReader;
import ca.omny.lambda.wrapper.models.FakeHttpRequest;
import ca.omny.lambda.wrapper.models.FakeHttpResponse;
import ca.omny.lambda.wrapper.models.LambdaInput;
import ca.omny.server.OmnyHandler;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.jetty.server.Request;

public class LambdaWrapper {
    
    OmnyHandler omnyHandler;
    LambdaInput input;
    
    public LambdaWrapper(LambdaInput input) {
        this.input = input;
        ConfigurationReader defaultConfigurationReader = ConfigurationReader.getDefaultConfigurationReader();
        defaultConfigurationReader.setKey("OMNY_NO_INJECTION", "true");
        if(input.getConfigOverrides()!=null) {
            for(String key: input.getConfigOverrides().keySet()) {
                String value = input.getConfigOverrides().get(key);
                defaultConfigurationReader.setKey(key, value);
            }
        }
        omnyHandler = new OmnyHandler();
    }
    
    public String handleRequest() {
        try {
            Request r = null;
            HttpServletRequest fakeServletRequest = new FakeHttpRequest(input);
            FakeHttpResponse fakeServletResponse = new FakeHttpResponse();
            
            omnyHandler.handle(null, r, fakeServletRequest, fakeServletResponse);
            Gson gson = new Gson();
            fakeServletResponse.writeBody();
            return gson.toJson(fakeServletResponse);
        } catch (IOException ex) {
            Logger.getLogger(LambdaWrapper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ServletException ex) {
            Logger.getLogger(LambdaWrapper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
