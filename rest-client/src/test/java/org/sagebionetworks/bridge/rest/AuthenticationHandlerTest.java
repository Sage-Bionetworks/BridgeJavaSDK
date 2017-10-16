package org.sagebionetworks.bridge.rest;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.sagebionetworks.bridge.rest.model.UserSessionInfo;

import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.Interceptor.Chain;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ UserSessionInfoProvider.class, Chain.class, Request.class, Response.class, HttpUrl.class,
        Builder.class, Route.class })
public class AuthenticationHandlerTest {

    @Mock
    UserSessionInfoProvider userSessionInfoProvider;

    @Mock
    Chain chain;

    @Mock
    Request request;

    @Mock
    Response response;
    
    @Mock
    Route route;

    @Mock
    HttpUrl url;

    @Mock
    Builder builder;

    private AuthenticationHandler authHandler;

    @Before
    public void before() {
        authHandler = new AuthenticationHandler(userSessionInfoProvider);
    }

    @Test
    public void interceptRequiringAuthAddsHeader() throws Exception {
        UserSessionInfo session = new UserSessionInfo().sessionToken("sessionToken");
        when(userSessionInfoProvider.retrieveSession()).thenReturn(session);
        
        when(chain.request()).thenReturn(request);
        when(chain.proceed(request)).thenReturn(response);
        when(url.toString()).thenReturn("/v3/auth/signOut");
        when(request.url()).thenReturn(url);
        when(request.newBuilder()).thenReturn(builder);
        when(builder.header(anyString(), anyString())).thenReturn(builder);
        when(builder.build()).thenReturn(request);
        
        authHandler.intercept(chain);

        verify(builder).header(HeaderInterceptor.BRIDGE_SESSION, "sessionToken");
    }
    
    @Test
    public void interceptNoAuthDoesNotAddHeader() throws Exception {
        UserSessionInfo session = new UserSessionInfo().sessionToken("sessionToken");
        when(userSessionInfoProvider.retrieveSession()).thenReturn(session);
        
        when(chain.request()).thenReturn(request);
        when(chain.proceed(request)).thenReturn(response);
        when(url.toString()).thenReturn("/v3/auth/emailSignIn");
        when(request.url()).thenReturn(url);
        
        authHandler.intercept(chain);

        verify(builder, never()).header(HeaderInterceptor.BRIDGE_SESSION, "sessionToken");
    }    

    @Test
    public void authenticateRequiringAuthAddsHeader() throws Exception {
        UserSessionInfo session = new UserSessionInfo().sessionToken("sessionToken");
        when(userSessionInfoProvider.retrieveSession()).thenReturn(session);
        
        when(response.request()).thenReturn(request);
        when(url.toString()).thenReturn("/v3/auth/signOut");
        when(request.url()).thenReturn(url);
        when(request.newBuilder()).thenReturn(builder);
        when(builder.header(anyString(), anyString())).thenReturn(builder);
        when(builder.build()).thenReturn(request);
        
        Request request = authHandler.authenticate(route, response);
        
        assertNotNull(request);
        verify(userSessionInfoProvider).reauthenticate();
        verify(builder).header(HeaderInterceptor.BRIDGE_SESSION, "sessionToken");
    }
    
    @Test
    public void authenticateNoAuthDoesNotAddHeader() throws Exception {
        UserSessionInfo session = new UserSessionInfo().sessionToken("sessionToken");
        when(userSessionInfoProvider.retrieveSession()).thenReturn(session);
        
        when(response.request()).thenReturn(request);
        when(url.toString()).thenReturn("/v3/auth/emailSignIn");
        when(request.url()).thenReturn(url);
        
        Request request = authHandler.authenticate(route, response);
        
        assertNull(request);
        verify(userSessionInfoProvider, never()).reauthenticate();
        verify(builder, never()).header(HeaderInterceptor.BRIDGE_SESSION, "sessionToken");
    }    
}
