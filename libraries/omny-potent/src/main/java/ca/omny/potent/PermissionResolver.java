package ca.omny.potent;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import ca.omny.extension.proxy.AccessRule;
import ca.omny.extension.proxy.Permission;
import ca.omny.potent.models.SiteConfiguration;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public class PermissionResolver {

    public void injectParameters(String hostname, String route, Map<String, String> queryStringParameters, AccessRule rule) {
        if(rule.getPermissions()==null) {
            return;
        }
        HashMap<String,String> parameters = new HashMap<String, String>();
        parameters.putAll(queryStringParameters);
        parameters.put("hostname", hostname);
        parameters.put("route", route);
        MustacheFactory mf = new DefaultMustacheFactory();
        
        for(Permission permission: rule.getPermissions()) {
            Mustache template = mf.compile(new StringReader(permission.getPath()),"test");
            StringWriter writer = new StringWriter();
            template.execute(writer, parameters);
            String injected = writer.toString();
            permission.setResolvedPath(this.replaceWildcards(route, injected));
        }
    }
    
    public String replaceWildcards(String route, String pattern) {
        StringBuilder result = new StringBuilder();
        String[] routeParts = route.split("/");
        String[] patternParts = pattern.split("/");
        for(int i=0; i<patternParts.length; i++) {
            result.append("/");
            if(patternParts[i].equals("*")) {
                result.append(routeParts[i]);
            } else {
                result.append(patternParts[i]);
            }
        }
        for(int i=patternParts.length; i<routeParts.length; i++) {
            result.append("/").append(routeParts[i]);
        }
        return result.toString();
    }
    
     public AccessRule getMostRelevantAccessRule(String route, String method, SiteConfiguration configuration) {
        AccessRule mostRelevant = configuration.getDefaultRule();
        for(AccessRule rule: configuration.getRules()) {
            if(rule.getMethod().toLowerCase().equals(method.toLowerCase())||rule.getMethod().equals("*")) {
                if(matchesPattern(route, rule)&&moreSpecific(rule, mostRelevant)) {
                    mostRelevant = rule;
                }
            }
        }
        
        return mostRelevant;
    }
    
    public boolean matchesPattern(String route, AccessRule rule) {
        String[] ruleParts = rule.getPattern().split("/");
        String[] routeParts = route.split("/");
        
        if(ruleParts.length>routeParts.length) {
            return this.isSameWithWildCard(ruleParts, routeParts);
        }
        
        if(routeParts.length>ruleParts.length&&!isVariable(ruleParts[ruleParts.length-1])) {
            return false;
        }
        
        for(int i=0; i<ruleParts.length; i++) {
            if(!routeParts[i].equals(ruleParts[i])&&!isVariable(ruleParts[i])) {
                return false;
            }
        }
        return true;
    }
    
    private boolean isSameWithWildCard(String[] ruleParts, String[] routeParts) {
        if(routeParts.length!=ruleParts.length-1) {
            return false;
        }
        
        if(!isVariable(ruleParts[ruleParts.length-1])) {
            return false;
        }
        
        for(int i=0; i<routeParts.length; i++) {
            if(!routeParts[i].equals(ruleParts[i])) {
                return false;
            }
        }
        
        return true;
    }
    
    public boolean moreSpecific(AccessRule newRule, AccessRule baseRule) {
        if(baseRule.getMethod().equals("*")&&!newRule.getMethod().equals("*")) {
            return true;
        }
        String[] newParts = newRule.getPattern().split("/");
        String[] baseParts = baseRule.getPattern().split("/");
        for(int i=0; i<newParts.length&&i<baseParts.length; i++) {
            if(isVariable(newParts[i])) {
                if(!isVariable(baseParts[i])) {
                    return false;
                }
            } else if(isVariable(baseParts[i])) {
                return true;
            }
        }
        return newParts.length>baseParts.length;
    }
    
    public boolean isVariable(String input) {
        return input.startsWith("{{") || input.equals("*");
    }
    
}
