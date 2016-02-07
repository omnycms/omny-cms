package ca.omny.utilities.testing.db;

import ca.omny.utilities.testing.db.MemoryDatabase;
import org.junit.After;
import org.junit.Before;

public class MemoryDatabaseTest extends DatabaseTests {
        
    @Before
    public void setUp() {
        instance = new MemoryDatabase();
    }
    
    @After
    public void tearDown() {
    }
}
