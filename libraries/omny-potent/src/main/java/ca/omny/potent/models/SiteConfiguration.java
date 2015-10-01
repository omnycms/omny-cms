package ca.omny.potent.models;

import ca.omny.extension.proxy.AccessRule;
import java.util.Collection;

public class SiteConfiguration {
    AccessRule defaultRule;
    Collection<AccessRule> rules;

    public AccessRule getDefaultRule() {
        return defaultRule;
    }

    public void setDefaultRule(AccessRule defaultRule) {
        this.defaultRule = defaultRule;
    }

    public Collection<AccessRule> getRules() {
        return rules;
    }

    public void setRules(Collection<AccessRule> rules) {
        this.rules = rules;
    }
}
