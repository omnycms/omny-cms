package ca.omny.documentdb;

import com.couchbase.client.CouchbaseClient;
import com.couchbase.client.protocol.views.Query;
import com.couchbase.client.protocol.views.View;
import com.google.gson.Gson;
import ca.omny.documentdb.models.PostgresConnection;
import ca.omny.logger.OmnyLogger;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.inject.Alternative;
import net.spy.memcached.util.StringUtils;
import org.postgresql.util.PGobject;

@Alternative
public class Postgres implements IDocumentQuerier {

    PostgresConnection connectionInfo;
    
    Gson gson = new Gson();
    
    OmnyLogger logger;

    public Postgres() {

    }

    public PostgresConnection getConnectionInfo() {
        return connectionInfo;
    }

    public Gson getGson() {
        return gson;
    }

    public void setGson(Gson gson) {
        this.gson = gson;
    }

    public OmnyLogger getLogger() {
        return logger;
    }

    public void setLogger(OmnyLogger logger) {
        this.logger = logger;
    }

    public void setConnectionInfo(PostgresConnection connectionInfo) {
        this.connectionInfo = connectionInfo;
    }
    
    @Override
    public Future<Boolean> delete(String key) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void deleteAll(String prefix) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> T get(String key, Class<T> type) {
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rs = null;

        try {
            Class.forName("org.postgresql.Driver");
            con = DriverManager.getConnection(connectionInfo.getUri(), connectionInfo.getUsername(), connectionInfo.getPassword());
            statement = con.prepareStatement("SELECT value FROM public.all WHERE id=?");
            statement.setString(1, key);
            rs = statement.executeQuery();

            if (rs.next()) {
                String result = rs.getString(1);
                rs.close();
                statement.close();
                con.close();
                return gson.fromJson(result, type);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Postgres.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (con != null) {
                    con.close();
                }

            } catch (SQLException ex) {
            }
        }
        return null;
    }   

    @Override
    public <T> List<T> getRange(String prefix, Class<T> type) {
        return this.getRange(prefix,type,true);
    }

    @Override
    public <T> List<T> getRange(String prefix, Class<T> type, boolean allowStale) {
        return this.getRange("all", "all", prefix, type, allowStale);
    }

    private <T> List<T> getRange(String designDocument, String viewName, String prefix, Class<T> type) {
        return this.getRange(designDocument, viewName, prefix, type, true);
    }

    private <T> List<T> getRange(String designDocument, String viewName, String prefix, Class<T> type, boolean allowStale) {
        long startTime = System.currentTimeMillis();
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rs = null;

        try {
            Class.forName("org.postgresql.Driver");
            String table = "public.all";
            if(!designDocument.equals("all")) {
                table = "public."+designDocument+"_"+viewName;
            }
            con = DriverManager.getConnection(connectionInfo.getUri(), connectionInfo.getUsername(), connectionInfo.getPassword());
            statement = con.prepareStatement("SELECT value FROM "+table+" WHERE id BETWEEN ? AND ?");
            statement.setString(1, prefix + "/");
            statement.setString(2, prefix + "0");
            rs = statement.executeQuery();
            List<T> keys = new LinkedList<T>();
            while (rs.next()) {
                String result = rs.getString(1);
                keys.add(gson.fromJson(result, type));
            }
            rs.close();
            statement.close();
            con.close();
            long endTime = System.currentTimeMillis();
            logger.info("took " + (endTime - startTime) + "ms total to get range results for " + prefix);
            return keys;

        } catch (SQLException ex) {

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Postgres.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (con != null) {
                    con.close();
                }

            } catch (SQLException ex) {
            }
        }
        return null;
    }
    
    public List<String> getKeysInRange(String prefix, boolean allowStale) {
        return this.getKeysInRange(prefix + "/", prefix + "0", allowStale);
    }
    
    public List<String> getKeysInRange(String start, String end, boolean allowStale) {
        return this.getKeysInRange("all","all",start, end, allowStale);
    }

    private List<String> getKeysInRange(String designDocument, String viewName, String start, String end, boolean allowStale) {
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rs = null;

        try {
            Class.forName("org.postgresql.Driver");
            con = DriverManager.getConnection(connectionInfo.getUri(), connectionInfo.getUsername(), connectionInfo.getPassword());
            statement = con.prepareStatement("SELECT id FROM public.all WHERE id BETWEEN ? AND ?");
            statement.setString(1, start);
            statement.setString(2, end);
            rs = statement.executeQuery();
            List<String> keys = new LinkedList<String>();
            while (rs.next()) {
                String result = rs.getString(1);
                keys.add(result);
            }
            rs.close();
            statement.close();
            con.close();
            return keys;

        } catch (SQLException ex) {

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Postgres.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (con != null) {
                    con.close();
                }

            } catch (SQLException ex) {
            }
        }
        return null;
    }

    @Override
    public Object getRaw(String key) {
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rs = null;

        try {
            Class.forName("org.postgresql.Driver");
            con = DriverManager.getConnection(connectionInfo.getUri(), connectionInfo.getUsername(), connectionInfo.getPassword());
            statement = con.prepareStatement("SELECT value, raw FROM public.all WHERE id=?");
            statement.setString(1, key);
            rs = statement.executeQuery();

            if (rs.next()) {
                byte[] result = rs.getBytes(2);
                String res = rs.getString(1);
                rs.close();
                statement.close();
                con.close();
                if(res!=null) {
                    return res;
                }
                return deserialize(result);
            }

        } catch (SQLException ex) {

        } catch (IOException ex) {
            Logger.getLogger(Postgres.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Postgres.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (con != null) {
                    con.close();
                }

            } catch (SQLException ex) {
            }
        }
        return null;
    }

    @Override
    public <T> Collection<KeyValuePair<T>> multiGet(Collection<String> keys, Class<T> type) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> Collection<T> multiGetCollection(Collection<String> keys, Class<T> type) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public String getKey(String... keyParts) {
        String key = StringUtils.join(Arrays.asList(keyParts), "/");
        return key;
    }

    @Override
    public void set(String key, Object value) {
        Connection con = null;
        PreparedStatement statement = null;
        PreparedStatement updateStatement = null;

        try {
            Class.forName("org.postgresql.Driver");
            con = DriverManager.getConnection(connectionInfo.getUri(), connectionInfo.getUsername(), connectionInfo.getPassword());
            updateStatement = con.prepareStatement("UPDATE public.all SET value=? WHERE id=?");
            
            statement = con.prepareStatement("INSERT INTO public.all (id,value) VALUES (?,?)");
            statement.setString(1, key);
            String val = value instanceof String ? value.toString() : gson.toJson(value);
            PGobject jsonObject = new PGobject();
            jsonObject.setType("json");
            jsonObject.setValue(val);
            
            updateStatement.setObject(1, jsonObject);
            updateStatement.setString(2, key);
            updateStatement.executeUpdate();
            updateStatement.close();
            
            statement.setObject(2, jsonObject);
            statement.executeUpdate();
            statement.close();
            con.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Postgres.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (updateStatement != null) {
                    updateStatement.close();
                }
                if (con != null) {
                    con.close();
                }

            } catch (SQLException ex) {
            }
        }
    }

    @Override
    public void set(String key, Object value, int expires) {
        this.set(key, value);
    }

    public static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        ObjectOutputStream o = new ObjectOutputStream(b);
        o.writeObject(obj);
        return b.toByteArray();
    }

    public static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream b = new ByteArrayInputStream(bytes);
        ObjectInputStream o = new ObjectInputStream(b);
        return o.readObject();
    }

    @Override
    public void setRaw(String key, Object value) {       
        if (key.endsWith(".json")) {
            this.set(key, value);
            return;
        }
        Connection con = null;
        PreparedStatement statement = null;
        PreparedStatement updateStatement = null;

        try {
            Class.forName("org.postgresql.Driver");
            con = DriverManager.getConnection(connectionInfo.getUri(), connectionInfo.getUsername(), connectionInfo.getPassword());
            updateStatement = con.prepareStatement("UPDATE public.all SET raw=? WHERE id=?");
            byte[] bytes= serialize(value);
            
            updateStatement.setObject(1, bytes);
            updateStatement.setString(2, key);
            updateStatement.executeUpdate();
            updateStatement.close();
            
            statement = con.prepareStatement("INSERT INTO public.all (id,raw) VALUES (?,?)");
            statement.setString(1, key);
            statement.setBytes(2, bytes);
            statement.executeUpdate();
            statement.close();
            con.close();

        } catch (SQLException ex) {

        } catch (IOException ex) {
            Logger.getLogger(Postgres.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Postgres.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (updateStatement != null) {
                    updateStatement.close();
                }
                if (con != null) {
                    con.close();
                }

            } catch (SQLException ex) {
            }
        }
    }

}
