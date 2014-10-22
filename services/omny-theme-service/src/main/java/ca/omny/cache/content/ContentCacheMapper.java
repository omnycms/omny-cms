package ca.omny.cache.content;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Al
 */
public class ContentCacheMapper {
        
    public List<ContentCache> getCachedForm(String input) {
        LinkedList<ContentCache> list = new LinkedList<ContentCache>();
        int currentIndex=0;
        int start = input.indexOf("{{");
        int end =0;
        while(start>=0) {
            end= input.indexOf("}}",start);
            String variable = input.substring(start+2,end);
            ContentCache cache = null;
            if(start>currentIndex) {
                 cache = new ContentCache(input.substring(currentIndex,start),variable);
            } else {
                cache = new ContentCache(null,variable);
            }
            list.add(cache);
            currentIndex = end+2;
            start = input.indexOf("{{",end);
        }
        if(currentIndex<input.length()) {
            ContentCache cache = new ContentCache(input.substring(currentIndex),null);
            list.add(cache);
        }
        return list;
    }
    
    
}
