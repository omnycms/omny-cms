package ca.omny.request;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class RequestInput {

    String uri;
    String method;
    InputStream content;
    String queryString;
    Map<String, String> headers;
    Map<String, String> cookies;
    Map<String, String> configOverrides;
    Map<String, String> queryStringParameters;
    String hostname;
    boolean secure;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getMethod() {
        return method;
    }

    public Map<String, String> getQueryStringParameters() {
        populateQueryStringParameters();
        return queryStringParameters;
    }

    public String getParameter(String parameter) {
        populateQueryStringParameters();
        if (queryStringParameters.containsKey(parameter)) {
            return queryStringParameters.get(parameter);
        }
        return null;
    }

    private void populateQueryStringParameters() {
        if (queryStringParameters == null) {
            queryStringParameters = new HashMap<>();
            if (queryString != null) {
                String[] groups = queryString.split("&");
                for (String group : groups) {
                    String[] values = group.split("=");
                    if (values.length == 2) {
                        queryStringParameters.put(values[0], values[1]);
                    }
                }
            }
        }
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public InputStream getContent() {
        return content;
    }

    public void setContent(InputStream content) {
        this.content = content;
    }

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public String getHeader(String header) {
        return headers.get(header);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public Map<String, String> getCookies() {
        return cookies;
    }

    public void setCookies(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public Map<String, String> getConfigOverrides() {
        return configOverrides;
    }

    public void setConfigOverrides(Map<String, String> configOverrides) {
        this.configOverrides = configOverrides;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public boolean isSecure() {
        return secure;
    }

    public void setSecure(boolean secure) {
        this.secure = secure;
    }

}
