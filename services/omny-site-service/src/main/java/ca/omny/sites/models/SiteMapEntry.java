package ca.omny.sites.models;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class SiteMapEntry {
    @XmlElement(name = "loc")
    String loc;
    
    @XmlElement(name = "changefreq")
    String changefreq = "hourly";

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }
}
