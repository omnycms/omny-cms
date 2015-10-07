package ca.omny.documentdb;

import ca.omny.configuration.ConfigurationReader;
import com.couchbase.client.CouchbaseClient;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CouchFactory {

    public static CouchQuerier querier;
    public static CouchbaseClient client;
    static ConfigurationReader configurationReader = ConfigurationReader.getDefaultConfigurationReader();

    public static CouchQuerier getQuerier() {
        if (querier != null) {
            return querier;
        }
        querier = new CouchQuerier(getClient());
        return querier;
    }

    public static CouchbaseClient getClient() {
        if (client != null) {
            return client;
        }
        try {
            List<URI> hosts = new LinkedList<URI>();

            String connections = configurationReader.getSimpleConfigurationString("OMNY_COUCHBASE_CONFIG");
            if (connections != null) {
                addHostsFromString(connections, hosts);
            } else {
                hosts.add(new URI("http://127.0.0.1:8091/pools"));
            }
         
            // Name of the Bucket to connect to
            String bucket = "default";

            // Password of the bucket (empty) string if none
            String password = "";

            // Connect to the Cluster
            client = new CouchbaseClient(hosts, bucket, password);
            return client;

        } catch (URISyntaxException ex) {
            Logger.getLogger(CouchFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CouchFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private static void addHostsFromString(Object x, List<URI> hosts) throws URISyntaxException, JsonSyntaxException {
        List<String> cStrings = new Gson().fromJson(x.toString(), LinkedList.class);
        for (String cString : cStrings) {
            hosts.add(new URI(cString));
        }
    }
}
