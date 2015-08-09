
package ca.omny.potent.permissions.roles;

import ca.omny.documentdb.IDocumentQuerier;
import ca.omny.potent.models.AssignedRole;
import ca.omny.extension.proxy.Permission;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class RoleBasedPermissionCheckerTest {
    
    Collection<Permission> permissions;
    Collection<AssignedRole> roles;
    Collection<Map<String,Boolean>> permissionsGrantedToRoles;
    String roleIdWithPermission;
    boolean shouldHavePermission;

    public RoleBasedPermissionCheckerTest(Collection<Permission> permissions, Collection<AssignedRole> roles, Collection<Map<String, Boolean>> permissionsGrantedToRoles, boolean shouldHavePermission) {
        this.permissions = permissions;
        this.roles = roles;
        this.permissionsGrantedToRoles = permissionsGrantedToRoles;
        this.shouldHavePermission = shouldHavePermission;
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
    
    
    @Parameters
    public static Collection<Object[]> configs() {
        Permission mainPermission = new Permission();
        mainPermission.setResolvedPath("/test");
        mainPermission.setPermissionName("doesn't matter");
        
        Permission subPermission = new Permission();
        subPermission.setResolvedPath("/test/hi");
        
        LinkedList<Permission> permissions = new LinkedList<Permission>();
        permissions.add(mainPermission);
        permissions.add(subPermission);
        
        LinkedList<AssignedRole> roles = new LinkedList<AssignedRole>();
        AssignedRole mainRole = new AssignedRole();
        mainRole.setRoleId("main");
        mainRole.setBasePath("/");
        roles.add(mainRole);
        
        Map<String,Boolean> allPermissions = new HashMap<String, Boolean>();
        allPermissions.put(mainPermission.getPermissionName(), Boolean.TRUE);
        allPermissions.put(subPermission.getPermissionName(), Boolean.TRUE);
        
        AssignedRole secondaryRole = new AssignedRole();
        secondaryRole.setRoleId("secondary");
        secondaryRole.setBasePath("/wrongpath");
        roles.add(secondaryRole);
        
        Map<String,Boolean> insufficientPermissions = new HashMap<String, Boolean>();
        insufficientPermissions.put(mainPermission.getPermissionName(), Boolean.TRUE);
        
        Collection<Map<String,Boolean>> goodPermissionsSet = new LinkedList<Map<String, Boolean>>();
        goodPermissionsSet.add(insufficientPermissions);
        goodPermissionsSet.add(allPermissions);
        
        Collection<Map<String,Boolean>> badPermissionsSet = new LinkedList<Map<String, Boolean>>();
        badPermissionsSet.add(insufficientPermissions);
        
        LinkedList<AssignedRole> insufficientRoles = new LinkedList<AssignedRole>();
        insufficientRoles.add(secondaryRole);
        
        return Arrays.asList(new Object[][] {
                { permissions, roles, goodPermissionsSet, true },
                { permissions, roles, badPermissionsSet, false },
                { permissions, insufficientRoles, goodPermissionsSet, false }
        });
    }

    /**
     * Test of hasPermissions method, of class RoleBasedPermissionChecker.
     */
    @org.junit.Test
    public void testHasPermissions() {
        System.out.println("hasPermissions");
        String hostname = "";
      
        String token = "";
        RoleMapper mockRoleMapper = mock(RoleMapper.class);
        when(mockRoleMapper.getUid(token,null)).thenReturn(token);
        
        when(mockRoleMapper.getRoles(hostname, token, null)).thenReturn(roles);
        when(mockRoleMapper.getRolePermissions(any(Collection.class),any(IDocumentQuerier.class))).thenReturn(permissionsGrantedToRoles);
        //when(mockRoleMapper.roleHasAllPermissions(any(String.class), any(Collection.class))).thenReturn(Boolean.FALSE);
        //when(mockRoleMapper.roleHasAllPermissions(roleIdWithPermission, permissions)).thenReturn(Boolean.TRUE);
        RoleBasedPermissionChecker instance = new RoleBasedPermissionChecker(null);
        instance.setRoleMapper(mockRoleMapper);
        boolean result = instance.hasPermissions(hostname, permissions, token);
        assertEquals(this.shouldHavePermission, result);
    }   
}
