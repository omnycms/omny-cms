package ca.omny.request.management;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class HttpServletRequestToInternalRequest {
    public RequestInput getInternalInput(HttpServletRequest request) throws IOException {
        RequestInput input = new RequestInput();
        input.setQueryString(request.getQueryString());
        input.setContent(request.getInputStream());
        input.setSecure(request.isSecure());
        HashMap<String,String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        
        while(headerNames.hasMoreElements()) {
            String next = headerNames.nextElement();
            headers.put(next, request.getHeader(next));
        }
        
        input.setHeaders(headers);
        
        HashMap<String,String> cookies = new HashMap<>();
        final Cookie[] inputCookies = request.getCookies();
        if(inputCookies !=null && inputCookies.length>0) {
            for(Cookie cookie: inputCookies) {
                cookies.put(cookie.getName(), cookie.getValue());
            }
        }
        input.setCookies(cookies);
        
        input.setMethod(request.getMethod());
        input.setUri(request.getRequestURI());
        input.setQueryString(request.getQueryString());
        
        return input;
    }
}
