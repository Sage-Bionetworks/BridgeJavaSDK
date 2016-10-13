package org.sagebionetworks.bridge.sdk.rest;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import org.sagebionetworks.bridge.sdk.rest.api.AuthenticationApi;
import org.sagebionetworks.bridge.sdk.rest.model.SignIn;

import java.lang.ref.WeakReference;
import java.util.Map;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by liujoshua on 10/11/16.
 */
public class ApiClientProvider {

  private final OkHttpClient unauthenticatedOkHttpClient;
  private final Retrofit.Builder retrofitBuilder;
  private final UserSessionInfoProvider userSessionInfoProvider;
  private final Map<SignIn, WeakReference<Retrofit>> authenticatedRetrofits;
  private final Map<SignIn, Map<Class, WeakReference>> authenticatedClients;

  public ApiClientProvider(String baseUrl, String userAgent) {
    this(baseUrl, userAgent, null);
  }

  // allow unit tests to inject a UserSessionInfoProvider
  ApiClientProvider(
      String baseUrl, String userAgent, UserSessionInfoProvider userSessionInfoProvider
  ) {
    authenticatedRetrofits = Maps.newHashMap();
    authenticatedClients = Maps.newHashMap();

    HeaderHandler headerHandler = new HeaderHandler(userAgent);

    unauthenticatedOkHttpClient = new OkHttpClient.Builder().addInterceptor(headerHandler).build();

    retrofitBuilder = new Retrofit.Builder().baseUrl(baseUrl)
                                            .client(unauthenticatedOkHttpClient)
                                            .addConverterFactory(GsonConverterFactory.create());

    this.userSessionInfoProvider = userSessionInfoProvider != null ? userSessionInfoProvider
        : new UserSessionInfoProvider(getClient(AuthenticationApi.class));
  }

  /**
   * Creates an unauthenticated client.
   * @param service
   *     Class representing the service
   * @return service client
   */
  public <T> T getClient(Class<T> service) {
    return getClientImpl(service, null);
  }

  /**
   * @param service
   *     Class representing the service
   * @param signIn
   *     credentials for the user, or null for an unauthenticated client
   * @return service client that is authenticated with the user's credentials
   */
  public <T> T getClient(Class<T> service, SignIn signIn) {
    Preconditions.checkNotNull(signIn);

    return getClientImpl(service, signIn);
  }

  private <T> T getClientImpl(Class<T> service, SignIn signIn) {
    Map<Class, WeakReference> userClients = authenticatedClients.get(signIn);
    if (userClients == null) {
      userClients = Maps.newHashMap();
      authenticatedClients.put(signIn, userClients);
    }

    T authenticateClient = null;
    WeakReference clientReference = userClients.get(service);

    if (clientReference != null) {
      authenticateClient = (T) clientReference.get();
    }

    if (authenticateClient == null) {
      authenticateClient = getAuthenticatedRetrofit(signIn).create(service);
      userClients.put(service, new WeakReference(authenticateClient));
    }

    return authenticateClient;
  }

  Retrofit getAuthenticatedRetrofit(SignIn signIn) {
    Retrofit authenticatedRetrofit = null;

    WeakReference<Retrofit> authenticatedRetrofitReference = authenticatedRetrofits.get(signIn);
    if (authenticatedRetrofitReference != null) {
      authenticatedRetrofit = authenticatedRetrofitReference.get();
    }

    if (authenticatedRetrofit == null) {
      OkHttpClient.Builder builder = unauthenticatedOkHttpClient.newBuilder();

      if (signIn != null) {
        AuthenticationHandler authenticationHandler = new AuthenticationHandler(signIn,
                                                                                userSessionInfoProvider
        );
        builder.addInterceptor(authenticationHandler).authenticator(authenticationHandler);
      }

      authenticatedRetrofit = retrofitBuilder.client(builder.build()).build();

      authenticatedRetrofits.put(signIn, new WeakReference<>(authenticatedRetrofit));
    }

    return authenticatedRetrofit;
  }
}
