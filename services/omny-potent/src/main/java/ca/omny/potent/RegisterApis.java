package ca.omny.potent;

public class RegisterApis {
    static {
        PowerServlet.addProxyService(new PassThroughProxy());
    }
}
