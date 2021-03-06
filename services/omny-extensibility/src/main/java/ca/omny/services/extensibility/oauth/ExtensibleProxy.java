package ca.omny.services.extensibility.oauth;

import ca.omny.extension.proxy.IOmnyProxyService;
import ca.omny.request.RequestResponseManager;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.google.gson.Gson;
import ca.omny.services.extensibility.oauth.models.Credentials;
import ca.omny.services.extensibility.oauth.models.ServiceCredentials;
import ca.omny.services.extensibility.oauth.models.UserCredentials;
import ca.omny.services.extensibility.mappers.OauthServiceCredentialMapper;
import ca.omny.services.extensibility.mappers.OauthUserCredentialMapper;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.SignatureType;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

public class ExtensibleProxy implements IOmnyProxyService {
    
    OauthServiceCredentialMapper serviceCredentialMapper = new OauthServiceCredentialMapper();
    OauthUserCredentialMapper userCredentialMapper = new OauthUserCredentialMapper();

    @Override
    public void proxyRequest(RequestResponseManager requestResponseManager, Map cofiguration) throws MalformedURLException, IOException {
        String route = getProxyRoute(requestResponseManager);
        String[] parts = route.substring(1).split("/");
        String organization = parts[2];
        String serviceName = parts[3];
        String configName = parts[4];
        
        ServiceCredentials serviceCredentials = serviceCredentialMapper.getServiceCredentials(organization, serviceName, requestResponseManager.getDatabaseQuerier());

        UserCredentials userCredentials = userCredentialMapper.getCredentials(requestResponseManager.getRequestHostname(), organization, serviceName, configName, requestResponseManager.getDatabaseQuerier());

        SignatureType signatureType = SignatureType.QueryString;
        if (serviceCredentials.getSignatureType() != null
                && serviceCredentials.getSignatureType().toLowerCase().equals("header")) {
            signatureType = SignatureType.Header;
        }

        ServiceBuilder builder = new ServiceBuilder()
                .provider(serviceCredentials.getApiConfig())
                .apiKey(serviceCredentials.getClientId())
                .apiSecret(serviceCredentials.getSecret());

        OAuthService service = builder.build();
        Map parameterMap = new HashMap();

        Credentials credentials = new Credentials();
        credentials.setServiceCredentials(serviceCredentials);
        credentials.setUserCredentials(userCredentials);
        Token accessToken = new Token(this.getRefreshedAccessToken(service, credentials), serviceCredentials.getSecret());

        Verb verb;
        switch (requestResponseManager.getRequest().getMethod().toLowerCase()) {
            case "post":
                verb = Verb.POST;
                break;
            case "put":
                verb = Verb.PUT;
                break;
            case "delete":
                verb = Verb.DELETE;
                break;
            default:
                verb = Verb.GET;
                break;
        }
        String relative = this.getRelativeUrl(parts);
        if (route.endsWith("/")) {
            relative += "/";
        }
        String requestUrl = serviceCredentials.getApiPrefix() + relative + "?" + getSafeQueryString(requestResponseManager);

        OAuthRequest request = new OAuthRequest(verb, requestUrl);
        if (signatureType == SignatureType.Header) {
            request.addHeader("Authorization", "Bearer " + userCredentials.getAccessToken());
        } else {
            service.signRequest(accessToken, request);
        }
        Response response = request.send();

        for (String header : response.getHeaders().keySet()) {
            if (isLegitHeader(header)) {
                requestResponseManager.getResponse().setHeader(header, response.getHeaders().get(header));
            }
        }

        requestResponseManager.getResponse().setStatus(response.getCode());
        String body = response.getBody();
        requestResponseManager.getResponse().getWriter().print(body);

    }

    public boolean isLegitHeader(String header) {
        if (header == null) {
            return false;
        }

        return header.equals("Content-Type");
    }

    public static String getSafeQueryString(RequestResponseManager req) {
        Map<String, String> queryStringParameters = req.getRequest().getQueryStringParameters();
        StringBuilder sb = new StringBuilder();
        boolean started = false;
        for (String parameter : queryStringParameters.keySet()) {
            if (!parameter.equals("access_token")) {
                if (started) {
                    sb.append("&");
                } else {
                    started = true;
                }
                try {
                    sb.append(parameter + "=" + URLEncoder.encode(queryStringParameters.get(parameter), "UTF-8"));
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(ExtensibleProxy.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return sb.toString();
    }

    public String getRelativeUrl(String[] parts) {
        StringBuilder sb = new StringBuilder();
        for (int i = 4; i < parts.length; i++) {
            sb.append("/");
            sb.append(parts[i]);
        }
        return sb.toString();
    }

    public String getRefreshedAccessToken(OAuthService service, Credentials credentials) {
        if (credentials.getUserCredentials().getRefreshToken() == null) {
            return credentials.getUserCredentials().getAccessToken();
        }

        //OAuthRequest request = new OAuthRequest(Verb.POST, "https://accounts.google.com/o/oauth2/token");
        Verb verb;
        switch (credentials.getServiceCredentials().getRefreshWebMethod().toLowerCase()) {
            case "post":
                verb = Verb.POST;
                break;
            case "put":
                verb = Verb.PUT;
                break;
            default:
                verb = Verb.GET;
                break;
        }
        OAuthRequest request = new OAuthRequest(verb, credentials.getServiceCredentials().getRefreshTokenUrl());
        Gson gson = new Gson();

        String parameterString = gson.toJson(credentials.getServiceCredentials().getRefreshParameters());
        MustacheFactory mf = new DefaultMustacheFactory();

        Mustache template = mf.compile(new StringReader(parameterString), "test");
        StringWriter writer = new StringWriter();
        template.execute(writer, credentials);
        String result = writer.toString();
        Map parameters = gson.fromJson(result, Map.class);
        for (Object key : parameters.keySet()) {
            if (verb == Verb.GET) {
                request.addQuerystringParameter(key.toString(), parameters.get(key).toString());
            } else {
                request.addBodyParameter(key.toString(), parameters.get(key).toString());
            }
        }
        Response response = request.send();

        return gson.fromJson(response.getBody(), Map.class).get("access_token").toString();
    }

    @Override
    public String getRoutingPattern() {
        return "/api/external/*";
    }

    public static String getProxyRoute(RequestResponseManager request) {
        return request.getRequest().getUri();
    }

    @Override
    public String getId() {
        return "EXTENSIBILITY";
    }
}
