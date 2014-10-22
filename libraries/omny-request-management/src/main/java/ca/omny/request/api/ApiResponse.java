package ca.omny.request.api;

public class ApiResponse {
    Object response;
    int statusCode;
    boolean encodeAsJson;
    String contentType;
    
    public ApiResponse(String response, int statusCode) {
        this.response = response;
        this.statusCode = statusCode;
        this.encodeAsJson = false;
    }

    public ApiResponse(Object response, int statusCode) {
        this.response = response;
        this.statusCode = statusCode;
        this.encodeAsJson = !(response instanceof byte[]);
    }

    public boolean shouldEncodeAsJson() {
        return encodeAsJson;
    }

    public void setEncodeAsJson(boolean encodeAsJson) {
        this.encodeAsJson = encodeAsJson;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Object getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        this.response = response;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
    
    
}
