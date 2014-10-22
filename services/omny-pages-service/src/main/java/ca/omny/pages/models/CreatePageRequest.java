package ca.omny.pages.models;

public class CreatePageRequest {
    String name;
    String title;
    Sample fromSample;
    Page pageDetails;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Page getPageDetails() {
        return pageDetails;
    }

    public void setPageDetails(Page pageDetails) {
        this.pageDetails = pageDetails;
    }

    public Sample getFromSample() {
        return fromSample;
    }

    public void setFromSample(Sample fromSample) {
        this.fromSample = fromSample;
    }
    
}
