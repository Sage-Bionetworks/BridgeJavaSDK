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

  public ApiClientProvider(String baseUrl, String userAgent) {
    this(baseUrl, userAgent, null);
  }

  // allow unit tests to inject a UserSessionInfoProvider
  ApiClientProvider(
      String baseUrl, String userAgent, UserSessionInfoProvider userSessionInfoProvider
  ) {
    HeaderHandler headerHandler = new HeaderHandler(userAgent);

    unauthenticatedOkHttpClient = new OkHttpClient.Builder().addInterceptor(headerHandler).build();

    retrofitBuilder = new Retrofit.Builder().baseUrl(baseUrl)
                                            .client(unauthenticatedOkHttpClient)
                                            .addConverterFactory(GsonConverterFactory.create());


    this.userSessionInfoProvider = userSessionInfoProvider != null ? userSessionInfoProvider
        : new UserSessionInfoProvider(getClient(AuthenticationApi.class, null));

    authenticatedRetrofits = Maps.newHashMap();
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
    Preconditions.checkNotNull(service);

    OkHttpClient.Builder builder = unauthenticatedOkHttpClient.newBuilder();
    if (signIn != null) {
      AuthenticationHandler authenticationHandler = new AuthenticationHandler(signIn,
                                                                              userSessionInfoProvider
      );
      builder.addInterceptor(authenticationHandler).authenticator(authenticationHandler);
    }

    return retrofitBuilder.client(builder.build()).build().create(service);
  }

  Retrofit getAuthenticatedRetrofit(SignIn signIn) {
    Retrofit authenticatedRetrofit = null;

    WeakReference<Retrofit> authenticatedRetrofitReference = authenticatedRetrofits.get(signIn);
    if (authenticatedRetrofitReference != null) {
      authenticatedRetrofit = authenticatedRetrofitReference.get();
    }

    if (authenticatedRetrofit == null) {
      AuthenticationHandler authenticationHandler = new AuthenticationHandler(signIn,
                                                                              userSessionInfoProvider
      );
      OkHttpClient okHttpClient = unauthenticatedOkHttpClient.newBuilder()
                                                             .addInterceptor(authenticationHandler)
                                                             .authenticator(authenticationHandler)
                                                             .build();

      authenticatedRetrofit = retrofitBuilder.client(okHttpClient).build();

      authenticatedRetrofits.put(signIn, new WeakReference<>(authenticatedRetrofit));
    }

    return authenticatedRetrofit;
  }
}
