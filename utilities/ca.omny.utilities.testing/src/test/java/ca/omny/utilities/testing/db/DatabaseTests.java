package ca.omny.utilities.testing.db;

import ca.omny.db.IDocumentQuerier;
import java.util.Collection;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public abstract class DatabaseTests {
    IDocumentQuerier instance;
    
    public DatabaseTests() {
    }

    @Test
    public void testGetValueAtRoot() {
        String key = "hello";
        Object value = "goodbye";
        Object expResult = value;
        instance.set(key, value);
        Object result = instance.get(key, String.class);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testGetNestedValue() {
        String key = instance.getKey("hello","I","am","test");
        Object value = "goodbye";
        Object expResult = value;
        instance.set(key, value);
        Object result = instance.get(key, String.class);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testConvertObjectValue() {
        String key = instance.getKey("hello","I","am","test");
        SampleV1Object v1 = new SampleV1Object();
        v1.stuff = java.util.Arrays.asList(new String[] {"a","b"});
        v1.hello = "nope";
        
        instance.set(key, v1);
        SampleV2Object result = instance.get(key, SampleV2Object.class);
        for(String a: v1.stuff) {
            assertTrue(result.stuff.contains(a));
        }
        assertEquals(v1.hello, result.hello);
    }
    
    @Test
    public void testDeleteGone() {
        String key = instance.getKey("hello","I","am","test");
        Object value = "goodbye";
        Object expResult = value;
        instance.set(key, value);
        instance.delete(key);
        Object result = instance.get(key, String.class);
        assertNull(result);
    }
    
    @Test
    public void testRangeQueryIsInSortedOrder() {
        String key = instance.getKey("hello","I","am","test");
        Object value = "goodbye";
        Object expResult = value;
        instance.set(key, value);
        Object value2 = "no";
        key = instance.getKey("hello","I","am","not-test");
        instance.set(key, value2);
        String prefix = instance.getKey("hello","I","am");
        Collection<String> range = instance.getRange(prefix, String.class);
        Object[] results = range.toArray();
        //note the second value entered should come first due to sort order
        assertEquals(value, results[1]);
        assertEquals(value2, results[0]);
    }
    
}
