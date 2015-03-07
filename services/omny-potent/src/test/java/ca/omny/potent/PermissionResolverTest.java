/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.omny.potent;

import ca.omny.extension.proxy.AccessRule;
import ca.omny.potent.models.SiteConfiguration;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author al
 */
public class PermissionResolverTest {
    
    public PermissionResolverTest() {
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
     * Test of matchesPattern method, of class PermissionResolver.
     */
    @Test
    public void testMatchesPattern() {
        System.out.println("matchesPattern");
        String route = "/test";
        AccessRule rule = new AccessRule();
        rule.setPattern("/test/*");
        
        PermissionResolver instance = new PermissionResolver();
        boolean expResult = true;
        boolean result = instance.matchesPattern(route, rule);
        assertEquals(expResult, result);
    }
    
}
