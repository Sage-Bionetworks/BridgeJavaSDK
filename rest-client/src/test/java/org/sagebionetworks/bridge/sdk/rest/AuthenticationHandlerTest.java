package org.sagebionetworks.bridge.sdk.rest;

import java.util.Properties;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.sagebionetworks.bridge.sdk.Config;
import org.sagebionetworks.bridge.sdk.rest.api.AuthenticationApi;
import org.sagebionetworks.bridge.sdk.rest.api.UploadsApi;
import org.sagebionetworks.bridge.sdk.rest.model.SignIn;
import org.sagebionetworks.bridge.sdk.rest.model.UploadRequest;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by liujoshua on 9/8/16.
 */
public class AuthenticationHandlerTest {
    private static final String STUDY = "api";
    private static final String BASE_URL = "https://webservices-develop.sagebridge.org/";

    private AuthenticationHandler authenticationHandler;
    private Retrofit bridgeAuthenticatedRetrofit;

    @Before
    public void before() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();

        Properties properties = Config.instance.properties;

        String adminEmail = properties.getProperty(Config.Props.ADMIN_EMAIL.getPropertyName());
        String adminPassword = properties.getProperty(Config.Props.ADMIN_PASSWORD.getPropertyName());

        SignIn credentials = new SignIn().study(STUDY).email(adminEmail).password(adminPassword);
        AuthenticationApi authenticationService = retrofit.create(AuthenticationApi.class);


        authenticationHandler = new AuthenticationHandler(credentials, new UserSessionInfoProvider(authenticationService));

        // AuthenticationHandler auth must be registered as both an interceptor and an authenticator
        OkHttpClient authedHttpClient =
                new OkHttpClient.Builder().authenticator(authenticationHandler).addInterceptor(authenticationHandler).build();

        bridgeAuthenticatedRetrofit = new Retrofit.Builder().baseUrl(BASE_URL).client(authedHttpClient)
                .addConverterFactory(GsonConverterFactory.create()).build();
    }

    @Test
    public void testReauthOnLogout() throws Exception {
        UploadRequest dummyUploadRequest =

        new UploadRequest().contentLength(1).contentMd5("hash").name("name").contentType("type");

        UploadsApi svc = bridgeAuthenticatedRetrofit.create(UploadsApi.class);
        svc.v3UploadsPost(dummyUploadRequest).execute();

        String authToken = authenticationHandler.getSessionToken();
        Assert.assertNotNull(authToken);
        Assert.assertTrue(authToken.length() > 0);

        // expires token
        AuthenticationApi authenticatedAuthService = bridgeAuthenticatedRetrofit.create(AuthenticationApi.class);
        authenticatedAuthService.v3AuthSignOutPost(new Object()).execute();

        svc.v3UploadsPost(dummyUploadRequest).execute();

        // verify new session token is now being used
        String newAuthToken = authenticationHandler.getSessionToken();
        Assert.assertNotNull(newAuthToken);
        Assert.assertTrue(newAuthToken.length() > 0);
        Assert.assertNotEquals(authToken, newAuthToken);
    }

}