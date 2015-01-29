package ca.omny.all.configuration;

import java.util.Collection;
import java.util.Map;

public class DatabaseConfigurationValues {
    String adminEmail;
    Map<String,String> domainAliases;
    Collection<String> mappedDomains;
    GoogleCredentials googleCredentials;
    boolean updateDefaultDbValues;

    public String getAdminEmail() {
        return adminEmail;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }

    public Map<String, String> getDomainAliases() {
        return domainAliases;
    }

    public void setDomainAliases(Map<String, String> domainAliases) {
        this.domainAliases = domainAliases;
    }

    public Collection<String> getMappedDomains() {
        return mappedDomains;
    }

    public void setMappedDomains(Collection<String> mappedDomains) {
        this.mappedDomains = mappedDomains;
    }

    public GoogleCredentials getGoogleCredentials() {
        return googleCredentials;
    }

    public void setGoogleCredentials(GoogleCredentials googleCredentials) {
        this.googleCredentials = googleCredentials;
    }

    public boolean shouldUpdateDefaultDbValues() {
        return updateDefaultDbValues;
    }

    public void setUpdateDefaultDbValues(boolean updateDefaultDbValues) {
        this.updateDefaultDbValues = updateDefaultDbValues;
    }
}
