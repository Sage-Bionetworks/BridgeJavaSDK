package org.sagebionetworks.bridge.sdk.rest;

import java.util.Properties;

import okhttp3.OkHttpClient;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import org.sagebionetworks.bridge.sdk.Config;
import org.sagebionetworks.bridge.sdk.rest.api.AuthenticationApi;
import org.sagebionetworks.bridge.sdk.rest.api.UploadsApi;
import org.sagebionetworks.bridge.sdk.rest.model.EmptyPayload;
import org.sagebionetworks.bridge.sdk.rest.model.SignIn;
import org.sagebionetworks.bridge.sdk.rest.model.UploadRequest;


/**
 * Created by liujoshua on 9/8/16.
 */
public class AuthenticationHandlerIntegrationTest {
    private static final String STUDY = "api";
    private static final String BASE_URL = "https://webservices-develop.sagebridge.org/";

    private AuthenticationHandler authenticationHandler;
    private ApiClientProvider provider;
    private SignIn signIn;

    @Before
    public void before() {
        provider = new ApiClientProvider(BASE_URL, "user-agent-string");

        Properties properties = Config.instance.properties;

        String adminEmail = properties.getProperty(Config.Props.ADMIN_EMAIL.getPropertyName());
        String adminPassword = properties
                .getProperty(Config.Props.ADMIN_PASSWORD.getPropertyName());

        signIn = new SignIn().study(STUDY).email(adminEmail).password(adminPassword);

        authenticationHandler = new AuthenticationHandler(signIn,
                new UserSessionInfoProvider(provider.getAuthenticatedRetrofit(null))
        );

        provider.createAuthenticatedRetrofit(signIn, authenticationHandler);


    }

    @Test
    public void testReauthOnLogout() throws Exception {
        UploadRequest dummyUploadRequest = new UploadRequest().contentLength(1).contentMd5("hash")
                .name("name").contentType("type");

        UploadsApi svc = provider.getClient(UploadsApi.class, signIn);
        svc.requestUploadSession(dummyUploadRequest).execute();

        String authToken = authenticationHandler.getSessionToken();
        Assert.assertNotNull(authToken);
        Assert.assertTrue(authToken.length() > 0);

        // expires token
        AuthenticationApi authenticatedAuthService = provider.getClient(AuthenticationApi.class, signIn);
        authenticatedAuthService.signOut(new EmptyPayload()).execute();

        svc.requestUploadSession(dummyUploadRequest).execute();

        // verify new session token is now being used
        String newAuthToken = authenticationHandler.getSessionToken();
        Assert.assertNotNull(newAuthToken);
        Assert.assertTrue(newAuthToken.length() > 0);
        Assert.assertNotEquals(authToken, newAuthToken);
    }

}