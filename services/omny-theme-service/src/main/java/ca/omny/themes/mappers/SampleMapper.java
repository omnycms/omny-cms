package ca.omny.themes.mappers;

import ca.omny.storage.StorageSystem;
import ca.omny.themes.models.Sample;
import java.util.Collection;
import java.util.LinkedList;

public class SampleMapper {
    
    public Collection<Sample> getSamples(String theme, String hostname, StorageSystem storage) {
        Collection<String> sampleNames = storage.listFiles("themes/current/"+theme+"/sample-pages", false, hostname);
        Collection<Sample> samples = new LinkedList<Sample>();
        for(String sampleName: sampleNames) {
            Sample sample = new Sample();
            sample.setSampleName(sampleName);
            sample.setTheme(theme);
            samples.add(sample);
        }
        return samples;
    }

    public void getSamples(String theme) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
