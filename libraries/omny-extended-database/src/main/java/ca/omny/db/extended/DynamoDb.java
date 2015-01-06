package ca.omny.db.extended;

import ca.omny.configuration.ConfigurationReader;
import ca.omny.documentdb.IDocumentQuerier;
import ca.omny.documentdb.KeyValuePair;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.BatchGetItemRequest;
import com.amazonaws.services.dynamodbv2.model.BatchGetItemResult;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.DeleteItemRequest;
import com.amazonaws.services.dynamodbv2.model.KeysAndAttributes;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;
import com.google.gson.Gson;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import net.spy.memcached.util.StringUtils;

@Alternative
public class DynamoDb implements IDocumentQuerier {

    ConfigurationReader configurationReader;
    DynamoDbConfig config;
    Gson gson = new Gson();
    AmazonDynamoDBClient client;

    private final static String DYNAMO_HASH_KEY_NAME = "table";
    private final static String DYNAMO_RANGE_KEY_NAME = "key";
    private final static char DB_SEPARATOR = '/';
    private final static char DB_NEXT_CHARACTER = '/' + 1;
    String table;

    @Inject
    public DynamoDb(ConfigurationReader configurationReader) {
        this.configurationReader = configurationReader;
        config = gson.fromJson(configurationReader.getConfigurationString("dynamo_config"), DynamoDbConfig.class);
        table = config.getTable();
        client = new AmazonDynamoDBClient(config);
    }

    private String getHashKey(String key) {
        int index = key.indexOf("/");
        if (index == -1) {
            index = key.length();
        }
        return key.substring(0, index);
    }

    private String getSubkey(String key) {
        int index = key.indexOf("/");
        return key.substring(index + 1);
    }

    private String getStartKey(String prefix) {
        return prefix + DB_SEPARATOR;
    }

    private String getEndKey(String prefix) {
        return prefix + DB_NEXT_CHARACTER;
    }

    private Map<String, AttributeValue> getKeys(String key) {
        HashMap<String, AttributeValue> keys = new HashMap<>();
        String hashKey = getHashKey(key);
        String rangeKey = getSubkey(key);
        keys.put(DYNAMO_HASH_KEY_NAME, new AttributeValue(hashKey));
        keys.put(DYNAMO_RANGE_KEY_NAME, new AttributeValue(rangeKey));

        return keys;
    }

    @Override
    public Future<Boolean> delete(String key) {
        DeleteItemRequest request = new DeleteItemRequest();
        request.setTableName(table);
        request.setKey(this.getKeys(key));
        client.deleteItem(request);
        return null;
    }

    @Override
    public void deleteAll(String prefix) {
        Collection<String> keys = this.getKeysInRange(prefix, true);
        for (String key : keys) {
            this.delete(key);
        }
    }

    @Override
    public <T> T get(String key, Class<T> type) {
        Object o = this.getRaw(key);
        if (o == null) {
            return null;
        }
        String stringValue = o.toString();
        if (type.equals(String.class)) {
            return (T) stringValue;
        }
        return gson.fromJson(stringValue, type);
    }

    @Override
    public <T> Collection<T> getRange(String prefix, Class<T> type) {
        return this.getRange(prefix, type, true);
    }

    @Override
    public <T> Collection<T> getRange(String prefix, Class<T> type, boolean allowStale) {
        String startKey = this.getStartKey(prefix);
        String endKey = this.getEndKey(prefix);
        return this.getRange(startKey, endKey, type, allowStale);
    }

    private <T> Collection<T> getRange(String start, String end, Class<T> type, boolean allowStale) {
        String hashKey = this.getHashKey(start);
        String actualStart = this.getSubkey(start);
        String actualEnd = this.getSubkey(end);
        QueryRequest request = new QueryRequest(hashKey);
        request.withTableName(table);
        Condition primaryKeyCondition = new Condition().withComparisonOperator(ComparisonOperator.EQ)
                .withAttributeValueList(new AttributeValue().withS(hashKey));
        request.addKeyConditionsEntry(DYNAMO_HASH_KEY_NAME, primaryKeyCondition);
        Condition rangeCondition = new Condition()
                .withComparisonOperator(ComparisonOperator.BETWEEN)
                .withAttributeValueList(new AttributeValue().withS(actualStart), new AttributeValue().withS(actualEnd));
        request.addKeyConditionsEntry(DYNAMO_RANGE_KEY_NAME, rangeCondition);
        QueryResult result = client.query(request);
        Collection<T> results = new LinkedList<>();

        for (Map<String, AttributeValue> map : result.getItems()) {
            String value = map.get("value").getS();
            results.add(gson.fromJson(value, type));
        }
        return results;
    }

    @Override
    public Collection<String> getKeysInRange(String prefix, boolean allowStale) {
        String startKey = this.getStartKey(prefix);
        String endKey = this.getEndKey(prefix);
        return this.getKeysInRange(startKey, endKey, allowStale);
    }

    @Override
    public Collection<String> getKeysInRange(String start, String end, boolean allowStale) {
        String hashKey = this.getHashKey(start);
        QueryRequest request = new QueryRequest(hashKey);
        request.setTableName(table);
        Condition primaryKeyCondition = new Condition().withComparisonOperator(ComparisonOperator.EQ)
                .withAttributeValueList(new AttributeValue().withS(hashKey));
        request.addKeyConditionsEntry(DYNAMO_HASH_KEY_NAME, primaryKeyCondition);
        Condition rangeCondition = new Condition()
                .withComparisonOperator(ComparisonOperator.BETWEEN)
                .withAttributeValueList(new AttributeValue().withS(start).withS(end));
        request.addKeyConditionsEntry(DYNAMO_RANGE_KEY_NAME, rangeCondition);
        QueryResult result = client.query(request);
        Collection<String> keys = new LinkedList<>();

        for (Map<String, AttributeValue> map : result.getItems()) {
            keys.add(hashKey + "/" + map.get(DYNAMO_RANGE_KEY_NAME).getS());
        }
        return keys;
    }

    @Override
    public Object getRaw(String key) {
        Map<String, AttributeValue> keys = this.getKeys(key);
        Map<String, AttributeValue> item = client.getItem(table, keys).getItem();
        if (item == null) {
            return null;
        }
        AttributeValue value = item.get("value");

        String stringValue = value.getS();
        return stringValue;
    }

    @Override
    public <T> Collection<KeyValuePair<T>> multiGet(Collection<String> keys, Class<T> type) {
        BatchGetItemRequest request = new BatchGetItemRequest();
        Collection<Map<String, AttributeValue>> keyList = new LinkedList<>();
        for (String key : keys) {
            Map<String, AttributeValue> idKeys = this.getKeys(key);
            keyList.add(idKeys);
        }
        request.addRequestItemsEntry(table,
                new KeysAndAttributes()
                .withKeys(keyList)
        );
        BatchGetItemResult response = client.batchGetItem(request);
        List<Map<String, AttributeValue>> values = response.getResponses().get(table);
        Collection<KeyValuePair<T>> results = new LinkedList<>();
        for (Map<String, AttributeValue> result : values) {
            String stringValue = result.get("value").getS();
            String fullKey = result.get(DYNAMO_HASH_KEY_NAME).getS() + "/" + result.get(DYNAMO_RANGE_KEY_NAME).getS();
            KeyValuePair<T> pair = new KeyValuePair<>(fullKey, gson.fromJson(stringValue, type));
            results.add(pair);
        }
        return results;
    }

    @Override
    public <T> Collection<T> multiGetCollection(Collection<String> keys, Class<T> type) {
        BatchGetItemRequest request = new BatchGetItemRequest();
        Collection<Map<String, AttributeValue>> keyList = new LinkedList<>();
        for (String key : keys) {
            Map<String, AttributeValue> idKeys = this.getKeys(key);
            keyList.add(idKeys);
        }
        request.addRequestItemsEntry(table,
                new KeysAndAttributes()
                .withKeys(keyList)
        );

        BatchGetItemResult response = client.batchGetItem(request);
        List<Map<String, AttributeValue>> values = response.getResponses().get(table);
        Collection<T> results = new LinkedList<>();
        for (Map<String, AttributeValue> result : values) {
            String stringValue = result.get("value").getS();
            results.add(gson.fromJson(stringValue, type));
        }
        return results;
    }

    @Override
    public String getKey(String... keyParts) {
        String key = StringUtils.join(Arrays.asList(keyParts), "/");
        return key;
    }

    @Override
    public void set(String key, Object value) {
        Map<String, AttributeValue> keys = this.getKeys(key);
        String val = null;
        if(value instanceof String) {
            keys.put("value", new AttributeValue(value.toString()));
        } else {
            keys.put("value", new AttributeValue(gson.toJson(value)));
        }
        client.putItem(table, keys);
    }

    @Override
    public void set(String key, Object value, int expires) {
        this.set(key, value);
    }

    @Override
    public void setRaw(String key, Object value) {
        this.set(key, value);
    }

}
