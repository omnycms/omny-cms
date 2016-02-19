package ca.omny.storage;

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

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}
