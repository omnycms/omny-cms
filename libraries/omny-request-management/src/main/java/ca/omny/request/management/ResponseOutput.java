package ca.omny.request.management;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.Cookie;

public class ResponseOutput {

    transient StringWriter sw = new StringWriter();
    transient PrintWriter writer = new PrintWriter(sw);
    Map<String,String> headers = new HashMap<String,String>();
    Map<String,Cookie> cookies = new HashMap<String,Cookie>();
    int status = 0;
    String body;

    public String getBody() {
        return body;
    }
    
    public void writeBody() {
        body = sw.toString();
    }
    
    public void addCookie(Cookie cookie) {
        cookies.put(cookie.getName(), cookie);
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

    public Map<String, Cookie> getCookies() {
        return cookies;
    }

    public int getStatus() {
        return status;
    }
}
