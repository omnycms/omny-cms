package ca.omny.potent.models;

public class Permission {
    String path;
    String permissionName;
    String resolvedPath;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }

    public String getResolvedPath() {
        return resolvedPath;
    }

    public void setResolvedPath(String resolvedPath) {
        this.resolvedPath = resolvedPath;
    }
}
