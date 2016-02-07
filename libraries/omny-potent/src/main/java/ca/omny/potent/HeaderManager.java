package ca.omny.potent;

import ca.omny.request.RequestResponseManager;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

public class HeaderManager {

    public static final String USER_HEADER = "X-UserId";
    public static final String HOST_HEADER = "X-Origin";
    public static final String CONTENT_LENGTH_HEADER = "Content-Length";
    public static final String[] AUTO_HEADERS = {USER_HEADER, HOST_HEADER, CONTENT_LENGTH_HEADER, "Transfer-encoding"};

    public Map<String, String> getSendableHeaders(String host, String uid, RequestResponseManager request) {
        HashMap<String, String> requestHeaders = new HashMap<>();

        for(String headerName: request.getRequest().getHeaders().keySet()) {
            String headerValue = request.getRequest().getHeader(headerName);
            requestHeaders.put(headerName, headerValue);
        }

        Map<String, String> sendableHeaders = getSendableHeaders(requestHeaders);
        addMainHeaders(host, uid, sendableHeaders);
        return sendableHeaders;
    }

    public Map<String, String> getSendableHeaders(Map<String, String> headers) {
        HashMap<String, String> sendableHeaders = new HashMap<>();
        for(String key: headers.keySet()) {
            if(!shouldNotSetHeader(key)) {
                sendableHeaders.put(key, headers.get(key));
            }
        }
        return sendableHeaders;
    }
    
    private boolean shouldNotSetHeader(String headerName) {
        headerName = headerName.toLowerCase();
        for(String s: AUTO_HEADERS) {
            if(s.toLowerCase().equals(headerName)) {
                return true;
            }
        }
        return false;    
    }

    public void addMainHeaders(String hostHeader, String uid, Map<String, String> headers) {
        headers.put(HOST_HEADER, hostHeader);
        headers.put(USER_HEADER, uid);
    }
}
