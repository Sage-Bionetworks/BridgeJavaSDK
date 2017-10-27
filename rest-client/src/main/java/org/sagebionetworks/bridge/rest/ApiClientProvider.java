package org.sagebionetworks.bridge.rest;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.sagebionetworks.bridge.rest.api.AuthenticationApi;
import org.sagebionetworks.bridge.rest.model.SignIn;

import com.google.common.collect.Maps;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Base class for creating clients that are correctly configured to communicate with the
 * Bridge server. This class has been designed so that different authentication credentials
 * can be used for different clients retrieved from this provider, but note that the core 
 * features of the client (user agent, accepted languages) are configured once for all clients. 
 */
public class ApiClientProvider {
    
    private static final Interceptor WARNING_INTERCEPTOR = new WarningHeaderInterceptor();
    private static final Interceptor ERROR_INTERCEPTOR = new ErrorResponseInterceptor();
    private static final Interceptor LOGGING_INTERCEPTOR = new LoggingInterceptor();
    
    private final String baseUrl;
    private final String userAgent;
    private final String acceptLanguage;
    private final Map<SignIn,UserSessionInfoProvider> sessionProviders;

    public ApiClientProvider(String baseUrl, String userAgent, String acceptLanguage) {
        this.baseUrl = baseUrl;
        this.userAgent = userAgent;
        this.acceptLanguage = acceptLanguage;
        this.sessionProviders = Maps.newHashMap();
    }

    // To build the ClientManager on this class, we need to have access to the session that is persisted
    // by the HTTP interceptors.
    public UserSessionInfoProvider getUserSessionInfoProvider(SignIn signIn) {
        return sessionProviders.get(signIn);
    }
    
    /**
     * Create an unauthenticated client (this client cannot authenticate automatically, and is only used for 
     * public APIs not requiring a server user to access). 
     * @param <T>
     *         One of the Api classes in the org.sagebionetworks.bridge.rest.api package.
     * @param service
     *         Class representing the service
     * @return service client
     */
    public <T> T getClient(Class<T> service) {
        checkNotNull(service);
        
        Retrofit client = getRetrofit(getHttpClientBuilder().build());
        return client.create(service);
    }

    /**
     * @param <T>
     *         One of the Api classes in the org.sagebionetworks.bridge.rest.api package.
     * @param service
     *         Class representing the service
     * @param signIn
     *         credentials for the user, or null for an unauthenticated client
     * @return service client that is authenticated with the user's credentials
     */
    public <T> T getClient(Class<T> service, SignIn signIn) {
        checkNotNull(service);
        checkNotNull(signIn);

        SignIn internalSignIn = RestUtils.makeInternalCopy(signIn);
        
        // Do not allow more than one session provider to be created for a signIn.
        UserSessionInfoProvider sessionProvider = null;
        synchronized(this) {
            sessionProvider = sessionProviders.get(internalSignIn);
            if (sessionProvider == null) {
                sessionProvider = new UserSessionInfoProvider(getClient(AuthenticationApi.class), internalSignIn);
                sessionProviders.put(internalSignIn, sessionProvider);
            }
        }
        
        UserSessionInterceptor sessionInterceptor = new UserSessionInterceptor(sessionProvider);
        AuthenticationHandler authenticationHandler = new AuthenticationHandler(sessionProvider);

        OkHttpClient.Builder httpClientBuilder = getHttpClientBuilder(sessionInterceptor, authenticationHandler);
        httpClientBuilder.authenticator(authenticationHandler);
        Retrofit retrofit = getRetrofit(httpClientBuilder.build());
        T authenticatingClient = retrofit.create(service);

        return authenticatingClient;
    }
    
    OkHttpClient.Builder getHttpClientBuilder(Interceptor... interceptors) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
            .connectTimeout(2, TimeUnit.MINUTES)
            .readTimeout(2, TimeUnit.MINUTES)
            .writeTimeout(2, TimeUnit.MINUTES);
        for (Interceptor interceptor : interceptors) {
            builder.addInterceptor(interceptor);
        }
        return builder
            .addInterceptor(new HeaderInterceptor(userAgent, acceptLanguage))
            .addInterceptor(WARNING_INTERCEPTOR)
            .addInterceptor(ERROR_INTERCEPTOR)
            .addInterceptor(LOGGING_INTERCEPTOR);
    }
    
    // allow test access to retrofit builder
    Retrofit getRetrofit(OkHttpClient client) {
        return getRetrofitBuilder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(RestUtils.GSON))
            .build();
    }

    Retrofit.Builder getRetrofitBuilder() {
        return new Retrofit.Builder();
    }
}
