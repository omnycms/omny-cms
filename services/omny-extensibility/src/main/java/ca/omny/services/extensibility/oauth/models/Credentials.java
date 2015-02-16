package ca.omny.services.extensibility.oauth.models;

public class Credentials {
    ServiceCredentials serviceCredentials;
    UserCredentials userCredentials;

    public ServiceCredentials getServiceCredentials() {
        return serviceCredentials;
    }

    public void setServiceCredentials(ServiceCredentials serviceCredentials) {
        this.serviceCredentials = serviceCredentials;
    }

    public UserCredentials getUserCredentials() {
        return userCredentials;
    }

    public void setUserCredentials(UserCredentials userCredentials) {
        this.userCredentials = userCredentials;
    }
}
