package ca.omny.cache.content;

public class ContentCache {
    private String content;
    private String variableName;

    public ContentCache() {
        
    }
    
    public ContentCache(String content, String variableName) {
        this.content = content;
        this.variableName = variableName;
    }

    public String getContent() {
        return content;
    }

    public String getVariableName() {
        return variableName;
    }
    
}
