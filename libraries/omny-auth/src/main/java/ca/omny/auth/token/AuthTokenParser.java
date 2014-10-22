package ca.omny.auth.token;

import javax.servlet.http.HttpServletRequest;

public class AuthTokenParser {

    public String getToken(HttpServletRequest request) {
        if (request.getHeader("Authorization") != null) {
            String auth = request.getHeader("Authorization");
            String[] split = auth.split(" ");
            return split[1];
        }
        String token = getTokenFromQueryString(request.getQueryString());
        if (token != null) {
            return token;
        }
        return null;
    }

    private String getTokenFromQueryString(String queryString) {
        if (queryString == null) {
            return null;
        }
        String[] parts = queryString.split("&");
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].startsWith("access_token")) {
                String[] components = parts[i].split("=");
                if(components.length>1) {
                    return components[1];
                }
            }
        }

        return null;
    }
}
