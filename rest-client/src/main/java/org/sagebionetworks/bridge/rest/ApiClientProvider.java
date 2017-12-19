package org.sagebionetworks.bridge.rest;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import org.sagebionetworks.bridge.rest.api.AuthenticationApi;
import org.sagebionetworks.bridge.rest.model.SignIn;
import org.sagebionetworks.bridge.rest.model.UserSessionInfo;

import com.google.common.base.Strings;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
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

    private static final int NUM_USERS_TO_CACHE = 5;
    private static final int NUM_SERVICES_PER_USER_TO_CACHE = 15;

    private final String baseUrl;
    private final String userAgent;
    private final String acceptLanguage;
    @Deprecated
    private final Map<SignIn,UserSessionInfoProvider> signInSessionProviders;
    private final ConcurrentMap<String, UserSessionInfoProvider> emailSessionProviders;
    private final RetrofitFactory retrofitFactory;

    private final LoadingCache<UserSessionInfoProvider, Retrofit> authenticatedRetrofits;
    private final LoadingCache<UserSessionInfoProvider, LoadingCache<Class<?>, ?>> authenticatedServices =
            CacheBuilder
                    .newBuilder()
                    .maximumSize(NUM_USERS_TO_CACHE)
                    .build(new ServiceFactoryProvider());

    /**
     * Creates Retrofit instances.
     */
    public class RetrofitFactory extends CacheLoader<UserSessionInfoProvider, Retrofit> {

        @Override public Retrofit load(UserSessionInfoProvider sessionProvider) {

            OkHttpClient.Builder httpClientBuilder;
            if (sessionProvider == null) {
                httpClientBuilder = getHttpClientBuilder();
            } else {
                UserSessionInterceptor sessionInterceptor = new UserSessionInterceptor(sessionProvider);
                AuthenticationHandler authenticationHandler = new AuthenticationHandler(sessionProvider);

                httpClientBuilder = getHttpClientBuilder(sessionInterceptor, authenticationHandler);
                httpClientBuilder.authenticator(authenticationHandler);
            }

            return getRetrofit(httpClientBuilder.build());
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

    /**
     * Providers a ServiceFactory for a UserSessionInfoProvider.
     */
    public class ServiceFactoryProvider extends CacheLoader<UserSessionInfoProvider, LoadingCache<Class<?>, ?>> {

        @Override
        public LoadingCache<Class<?>, ?> load(UserSessionInfoProvider sessionInfoProvider) {
            return CacheBuilder
                    .newBuilder()
                    .maximumSize(NUM_SERVICES_PER_USER_TO_CACHE)
                    .build(new ServiceFactory(sessionInfoProvider));
        }
    }

    /**
     * Instantiates instances of a service by its class name.
     */
    public class ServiceFactory extends CacheLoader<Class, Object> {
        private final UserSessionInfoProvider sessionProvider;

        ServiceFactory(UserSessionInfoProvider sessionProvider) {
            this.sessionProvider = sessionProvider;
        }

        @Override
        public Object load(Class serviceClass) {
            Retrofit retrofit = authenticatedRetrofits.getUnchecked(sessionProvider);
            return retrofit.create(serviceClass);
        }
    }

    public ApiClientProvider(String baseUrl, String userAgent, String acceptLanguage) {
        this.baseUrl = baseUrl;
        this.userAgent = userAgent;
        this.acceptLanguage = acceptLanguage;
        this.signInSessionProviders = Maps.newHashMap();
        this.emailSessionProviders = Maps.newConcurrentMap();
        this.retrofitFactory = new RetrofitFactory();
        this.authenticatedRetrofits = CacheBuilder.newBuilder()
                .maximumSize(NUM_USERS_TO_CACHE)
                .build(retrofitFactory);
    }

    // To build the ClientManager on this class, we need to have access to the session that is persisted
    // by the HTTP interceptors.
    public UserSessionInfoProvider getUserSessionInfoProvider(SignIn signIn) {
        return signInSessionProviders.get(signIn);
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

        Retrofit client = retrofitFactory.load(null);
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

        if (Strings.isNullOrEmpty(signIn.getPassword())) {
            return getClient(service, signIn.getEmail());
        }

        SignIn internalSignIn = RestUtils.makeInternalCopy(signIn);
        
        // Do not allow more than one session provider to be created for a signIn.
        UserSessionInfoProvider sessionProvider = null;
        synchronized(this) {
            sessionProvider = signInSessionProviders.get(internalSignIn);
            if (sessionProvider == null) {
                sessionProvider = new UserSessionInfoProvider(getClient(AuthenticationApi.class), internalSignIn);
                emailSessionProviders.put(internalSignIn.getEmail(), sessionProvider);
                signInSessionProviders.put(internalSignIn, sessionProvider);
            }
            return getClient(sessionProvider, service);
        }
    }

    /**
     *
     * @param service Class representing the service
     * @param email email for the user, or null for an unauthenticated client
     * @param <T> One of the Api classes in the org.sagebionetworks.bridge.rest.api package.
     * @return service client that is authenticated with the user's credentials
     */
    public <T> T getClient(Class<T> service, String email) {
        checkNotNull(service);
        checkNotNull(email);

        return getClient(emailSessionProviders.get(email), service);
    }

    public void setEmailUserSessionInfo(String studyId, String email, UserSessionInfo session) {
        emailSessionProviders.putIfAbsent(email,
                new UserSessionInfoProvider(getClient(AuthenticationApi.class), studyId, session));
        emailSessionProviders.get(email).setSession(session);
    }

    private <T> T getClient(UserSessionInfoProvider sessionProvider, Class<T> serviceClass) {
        if (sessionProvider == null) {
            return getClient(serviceClass);
        }
        return (T) authenticatedServices.getUnchecked(sessionProvider).getUnchecked(serviceClass);
    }
}
