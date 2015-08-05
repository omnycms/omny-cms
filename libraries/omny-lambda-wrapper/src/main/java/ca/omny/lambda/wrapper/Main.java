package ca.omny.lambda.wrapper;

import ca.omny.lambda.wrapper.models.LambdaInput;

public class Main {
    public static void main(String[] args) {
        LambdaInput sampleInput = new LambdaInput();
        sampleInput.setMethod("GET");
        sampleInput.setUri("/api/v1.0/blogs/health");
        
        LambdaWrapper lambdaWrapper = new LambdaWrapper(sampleInput);
        
        String handleRequest = lambdaWrapper.handleRequest();
        System.out.println(handleRequest);
    }
}
