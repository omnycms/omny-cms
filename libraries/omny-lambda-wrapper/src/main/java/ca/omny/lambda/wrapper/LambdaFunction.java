package ca.omny.lambda.wrapper;

import ca.omny.request.RequestInput;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class LambdaFunction implements RequestHandler<RequestInput, String> {

    @Override
    public String handleRequest(RequestInput i, Context cntxt) {
        LambdaWrapper lambdaWrapper = new LambdaWrapper(i);
        String handleRequest = lambdaWrapper.handleRequest();
        return handleRequest;
    }
    
}
