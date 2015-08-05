package ca.omny.lambda.wrapper;

import ca.omny.lambda.wrapper.models.LambdaInput;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class LambdaFunction implements RequestHandler<LambdaInput, String> {

    @Override
    public String handleRequest(LambdaInput i, Context cntxt) {
        
        LambdaWrapper lambdaWrapper = new LambdaWrapper(i);
        String handleRequest = lambdaWrapper.handleRequest();
        return handleRequest;
    }
    
}
