package ca.omny.routing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RoutingTree<T> {

    boolean pageMatchesPath;
    String path;
    Map<String, RoutingTree<T>> childrenCaches;
    Map<String, IRoute<T>> childrenPages;

    public RoutingTree(String path) {
        this.path = path;
        childrenCaches = new HashMap<String, RoutingTree<T>>();
        childrenPages = new HashMap<String, IRoute<T>>();
    }

    public IRoute<T> matchPath(String url) {
        String[] parts = url.split("/");
        ArrayList<String> p = new ArrayList<String>(parts.length);
        for (String part : parts) {
            p.add(part);
        }
        return matchPath(p);
    }

    IRoute<T> matchPath(ArrayList<String> parts) {
        if (parts.size() == 0) {
            return this.getBestRouteByWildcard(0);
        }
        if (parts.get(0).equals("")) {
            parts.remove(0);
        }

        String part = parts.get(0);
        if (parts.size() == 1) {
            if (childrenPages.containsKey(part)) {
                return childrenPages.get(part);
            }

        }

        ArrayList<String> partsCopy = (ArrayList<String>) parts.clone();
        partsCopy.remove(0);

        if (childrenCaches.containsKey(part)) {
            //look for the most accurate page cache
            IRoute<T> result = childrenCaches.get(part).matchPath(partsCopy);
            if (result != null) {
                return result;
            }
        }

        if (childrenCaches.containsKey("*")) {
            IRoute<T> result = childrenCaches.get("*").matchPath(partsCopy);
            if (result != null) {
                return result;
            }
        }

        if (childrenPages.containsKey("*")) {
            IRoute<T> result = childrenPages.get("*");
            if(this.endsInTrueWildCard(result)||parts.size()==1) {
                return result;
            }
        }

        return null;
    }

    /**
     * Must use optional parameters
     *
     * @return
     */
    private IRoute<T> getBestRouteByWildcard(int offset) {
        if (childrenPages.containsKey("*")) {
            IRoute<T> route = childrenPages.get("*");
            if (this.hasWildcardParameters(route.getPath(), offset + 1)) {
                return route;
            }
        }

        if (childrenCaches.containsKey("*")) {
            IRoute<T> result = childrenCaches.get("*").getBestRouteByWildcard(offset + 1);
            if (this.endsInTrueWildCard(result)) {
                return result;
            }
        }

        return null;
    }

    private boolean endsInTrueWildCard(IRoute<T> r) {
        if (r != null) {
            String[] parts = r.getPath().split("/");
            if (parts[parts.length - 1].equals("*")) {
                return true;
            }
        }
        return false;
    }

    private String getLastPieceOfRoute(IRoute<T> r) {
        String[] parts = r.getPath().split("/");
        return parts[parts.length - 1];
    }

    private boolean hasWildcardParameters(String input, int numberOfWildcardsExpected) {
        if (input.startsWith("/")) {
            input = input.substring(1);
        }
        String[] parts = input.split("/");
        for (int i = 0; i < numberOfWildcardsExpected; i++) {
            if (i >= parts.length) {
                return false;
            }
            if (!isWildcardParameter(parts[parts.length - i - 1])) {
                return false;
            }
        }

        return true;
    }

    private boolean isWildcardParameter(String input) {
        return input.startsWith("{");
    }

    private boolean isWildcard(String input) {
        return (input.startsWith("{") || input.equals("*"));
    }

    public void addRoute(IRoute<T> route) {
        String path = route.getPath();
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        String[] partsOfRoute = route.getPath().split("/");
        //assume for efficiency sake that this belongs under us
        int start = this.path.split("/").length;
        String[] subParts = new String[partsOfRoute.length - start];
        for (int i = 0; i < subParts.length; i++) {
            subParts[i] = partsOfRoute[i + start];
        }
        String part = subParts[0];
        if (this.isWildcard(part)) {
            part = "*";
        }
        if (subParts.length == 1) {
            childrenPages.put(part, route);
        } else {
            if (!childrenCaches.containsKey(part)) {
                RoutingTree cache = new RoutingTree(this.path + "/" + part);
                childrenCaches.put(part, cache);
            }
            childrenCaches.get(part).addRoute(route);
        }
    }
}
