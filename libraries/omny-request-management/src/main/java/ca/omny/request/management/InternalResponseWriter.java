package ca.omny.request.management;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public class InternalResponseWriter {
    public void write(ResponseOutput ro, HttpServletResponse r) {
        writeCoookies(ro, r);
        r.setStatus(r.getStatus());
        for(String header: ro.getHeaders().keySet()) {
            String value = ro.getHeaders().get(header);
            r.addHeader(header, value);
        }
        
        ro.writeBody();
        final String body = ro.getBody();
        writeBody(body, r);
    }

    private void writeBody(final String body, HttpServletResponse r) {
        if(body != null) {
            try {
                r.getWriter().write(body);
            } catch (IOException ex) {
                Logger.getLogger(InternalResponseWriter.class.getName()).log(Level.WARNING, null, ex);
            }
        }
    }
    
    private void writeCoookies(ResponseOutput ro, HttpServletResponse r) {
        Map<String,Cookie> cookies = ro.getCookies();
        if(cookies ==null) {
            return;
        }
        
        for(Cookie cookie: cookies.values()) {
            r.addCookie(cookie);
        }
    }
   
}
