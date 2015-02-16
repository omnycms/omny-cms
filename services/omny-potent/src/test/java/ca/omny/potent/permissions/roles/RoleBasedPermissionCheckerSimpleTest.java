package ca.omny.potent.permissions.roles;

import ca.omny.potent.permissions.roles.RoleBasedPermissionChecker;
import ca.omny.potent.models.AssignedRole;
import ca.omny.extension.proxy.Permission;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class RoleBasedPermissionCheckerSimpleTest {
    
    Collection<Permission> permissions;
    LinkedList<AssignedRole> roles;
    public RoleBasedPermissionCheckerSimpleTest() {
        Permission mainPermission = new Permission();
        mainPermission.setResolvedPath("/test");
        mainPermission.setPermissionName("doesn't matter");
        
        Permission subPermission = new Permission();
        subPermission.setResolvedPath("/test/hi");
        
        permissions = new LinkedList<Permission>();
        permissions.add(mainPermission);
        permissions.add(subPermission);
        
        roles = new LinkedList<AssignedRole>();
        AssignedRole mainRole = new AssignedRole();
        mainRole.setRoleId("main");
        mainRole.setBasePath("/");
        roles.add(mainRole);
        
        AssignedRole goodExactMatch = new AssignedRole();
        goodExactMatch.setRoleId("goodexact");
        goodExactMatch.setBasePath("/test");
        roles.add(goodExactMatch);
        
        AssignedRole secondaryRole = new AssignedRole();
        secondaryRole.setRoleId("secondary");
        secondaryRole.setBasePath("/wrong");
        roles.add(secondaryRole);
        
        AssignedRole badRole = new AssignedRole();
        badRole.setRoleId("bad");
        badRole.setBasePath("/te");
        roles.add(badRole);
        
        
    }
    
    @Before
    public void setUp() {
        
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getUniqueRoles method, of class RoleBasedPermissionChecker.
     */
    @Test
    public void testGetUniqueRoles() {
        System.out.println("getUniqueRoles");
        Collection<AssignedRole> roles = new LinkedList<AssignedRole>();
        roles.addAll(this.roles);
        roles.addAll(this.roles);
        RoleBasedPermissionChecker instance = new RoleBasedPermissionChecker();
       
        Collection<String> result = instance.getUniqueRoles(roles);
        assertEquals(result.size(), this.roles.size());
        assertNotSame(result.size(), roles.size());
    }

    /**
     * Test of getAppropriateRoles method, of class RoleBasedPermissionChecker.
     */
    @Test
    public void testGetAppropriateRoles() {
        System.out.println("getAppropriateRoles");
        
        RoleBasedPermissionChecker instance = new RoleBasedPermissionChecker();
        Collection<AssignedRole> expResult = null;
        Collection<AssignedRole> result = instance.getAppropriateRoles(permissions, roles);
        assertEquals(2, result.size());
        Iterator<AssignedRole> roleIterator = roles.iterator();
        Iterator<AssignedRole> resultIterator = result.iterator();
        assertEquals(roleIterator.next(),resultIterator.next());
        assertEquals(roleIterator.next(),resultIterator.next());
    }
    
}
