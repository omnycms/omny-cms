package ca.omny.routing;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import static org.junit.Assert.*;

public class PathRoutingCacheTest {
    
    public PathRoutingCacheTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of matchPath method, of class PathRoutingCache.
     */
    @org.junit.Test
    public void testMatchPath_String() {
        System.out.println("matchPath");
        RoutingTree<String> instance = new RoutingTree<String>("");
        IRoute<String> testroute = new Route("/home/*/test");
        instance.addRoute(testroute);
        IRoute<String> wildRoute = new Route("/home/*/{aloha}");
        instance.addRoute(wildRoute);
        IRoute<String> wildStart2 = new Route("/*/*/*/{aloha}");
        instance.addRoute(wildStart2);
        IRoute<String> optional2 = new Route("/{aloha}");
        instance.addRoute(optional2);
        IRoute<String> optional1 = new Route("/alamarre/{aloha}");
        instance.addRoute(optional1);
        IRoute<String> wildStart1 = new Route("/*/home/*/{aloha}");
        instance.addRoute(wildStart1);
        
        Route catchA1 = new Route("/a/{b}/{c}");
        instance.addRoute(catchA1);
        
        Route catchA = new Route("/a/*");
        instance.addRoute(catchA);
        
        IRoute result = null;
        result = instance.matchPath("/home/aloha/test");
        assertEquals(testroute, result);
        
        result = instance.matchPath("/home/hey/hey");
        assertEquals(wildRoute, result);
        
        result = instance.matchPath("/x/home/hey/hey");
        assertEquals(wildStart1, result);
        
        result = instance.matchPath("/alamarre");
        //assertEquals(optional1.getPath(),result.getPath());
        assertEquals(optional1, result);
//        
        result = instance.matchPath("/a/a/b/c");
        assertEquals(catchA, result);
        
        result = instance.matchPath("/a/x/y");
        assertEquals(catchA1, result);
        
        //{c} will be null but match on /a/{b}/{c}
        result = instance.matchPath("/a/x");
        assertEquals(catchA1, result);
    }



}