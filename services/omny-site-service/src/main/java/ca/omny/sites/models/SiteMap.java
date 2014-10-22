package ca.omny.sites.models;

import java.util.Collection;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "urlset")
@XmlAccessorType(XmlAccessType.FIELD)
public class SiteMap {
    
    @XmlElement(name="url")
    Collection<SiteMapEntry> sitemapEntries;

    public Collection<SiteMapEntry> getSitemapEntries() {
        return sitemapEntries;
    }

    public void setSitemapEntries(Collection<SiteMapEntry> sitemapEntries) {
        this.sitemapEntries = sitemapEntries;
    }
}
