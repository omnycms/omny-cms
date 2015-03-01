package ca.omny.extension.proxy;

import javax.servlet.http.HttpServletRequest;

public interface IRemoteUrlProvider extends IdentifiableProvider {
    String getRemoteUrl(String route, HttpServletRequest req);
}
