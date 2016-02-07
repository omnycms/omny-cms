package ca.omny.extension.proxy;

import ca.omny.request.RequestResponseManager;

public interface IRemoteUrlProvider extends IdentifiableProvider {
    String getRemoteUrl(String route, RequestResponseManager req);
}
