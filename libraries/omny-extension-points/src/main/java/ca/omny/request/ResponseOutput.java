package ca.omny.request;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public class ResponseOutput {

    transient StringWriter sw = new StringWriter();
    transient PrintWriter writer = new PrintWriter(sw);
    Map<String,String> headers = new HashMap<>();
    Map<String,String> cookies = new HashMap<>();
    int status = 0;
    String body;

    public String getBody() {
        return body;
    }
    
    public void writeBody() {
        body = sw.toString();
    }
    
    public void addCookie(String cookie, String value) {
        cookies.put(cookie, value);
    }

    public boolean containsHeader(String string) {
        return headers.containsKey(string);
    }

    public void setHeader(String header, String value) {
        headers.put(header, value);
    }

    public void setStatus(int i) {
        this.status = i;
    }

    public PrintWriter getWriter() throws IOException {
        return writer;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Map<String, String> getCookies() {
        return cookies;
    }

    public int getStatus() {
        return status;
    }
}
