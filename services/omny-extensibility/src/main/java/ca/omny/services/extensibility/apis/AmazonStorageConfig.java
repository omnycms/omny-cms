package ca.omny.services.extensibility.apis;

import com.amazonaws.auth.AWSCredentials;

public class AmazonStorageConfig implements AWSCredentials {
    String accessKey;
    String secretKey;
    String bucket;
    
    @Override
    public String getAWSAccessKeyId() {
        return accessKey;
    }

    @Override
    public String getAWSSecretKey() {
        return secretKey;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }
}
