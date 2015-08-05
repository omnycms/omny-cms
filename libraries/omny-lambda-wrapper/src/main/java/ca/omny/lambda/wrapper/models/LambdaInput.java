package ca.omny.lambda.wrapper.models;

import java.util.Map;

public class LambdaInput {
    String uri;
    String method;
    String content;
    String queryString;
    Map<String,String> headers;
    Map<String,String> cookies;
    Map<String,String> configOverrides;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
    
    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
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

}
