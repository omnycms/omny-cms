package ca.omny.db.extended;

import com.amazonaws.auth.AWSCredentials;

public class DynamoDbConfig implements AWSCredentials {
    String accessKey;
    String secretKey;
    String table;
    
    @Override
    public String getAWSAccessKeyId() {
        return accessKey;
    }

    @Override
    public String getAWSSecretKey() {
        return secretKey;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }
}
