package org.sagebionetworks.bridge.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.spy;

import java.util.Arrays;
import java.util.Collections;

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
    public void interceptRequiringAuthAddsHeaderSignOut() throws Exception {
        interceptRequiringAuthAddsHeader("/v3/auth/signOut");
    }
    
    @Test
    public void interceptRequiringAuthAddsHeaderChangeStudy() throws Exception {
        interceptRequiringAuthAddsHeader("/v3/auth/study");
    }
    
    @Test
    public void interceptRequiringAuthAddsHeaderAdminChangeStudy() throws Exception {
        interceptRequiringAuthAddsHeader("/v3/admin/study");
    }
    
    private void interceptRequiringAuthAddsHeader(String path) throws Exception {
        UserSessionInfo session = new UserSessionInfo();
        Tests.setVariableValueInObject(session, "sessionToken", "sessionToken");
        when(userSessionInfoProvider.retrieveSession()).thenReturn(session);
        
        when(chain.request()).thenReturn(request);
        when(chain.proceed(request)).thenReturn(response);
        when(url.toString()).thenReturn(path);
        when(request.url()).thenReturn(url);
        when(request.newBuilder()).thenReturn(builder);
        when(builder.header(anyString(), anyString())).thenReturn(builder);
        when(builder.build()).thenReturn(request);

        authHandler.tryCount.set(5);

        authHandler.intercept(chain);

        assertEquals(0, authHandler.tryCount.get().intValue());

        verify(builder).header(HeaderInterceptor.BRIDGE_SESSION, "sessionToken");        
    }
    
    @Test
    public void interceptRequiringAuthAddsHeaderToChangeStudy() throws Exception {
        UserSessionInfo session = new UserSessionInfo();
        Tests.setVariableValueInObject(session, "sessionToken", "sessionToken");
        when(userSessionInfoProvider.retrieveSession()).thenReturn(session);
        
        when(chain.request()).thenReturn(request);
        when(chain.proceed(request)).thenReturn(response);
        when(url.toString()).thenReturn("/v3/auth/admin/study");
        when(request.url()).thenReturn(url);
        when(request.newBuilder()).thenReturn(builder);
        when(builder.header(anyString(), anyString())).thenReturn(builder);
        when(builder.build()).thenReturn(request);

        authHandler.intercept(chain);

        verify(builder).header(HeaderInterceptor.BRIDGE_SESSION, "sessionToken");
    }

    
    @Test
    public void interceptNoAuthDoesNotAddHeader() throws Exception {
        UserSessionInfo session = new UserSessionInfo();
        Tests.setVariableValueInObject(session, "sessionToken", "sessionToken");
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
        UserSessionInfo session = new UserSessionInfo();
        Tests.setVariableValueInObject(session, "sessionToken", "sessionToken");
        when(userSessionInfoProvider.retrieveSession()).thenReturn(session);
        
        when(response.request()).thenReturn(request);
        when(url.toString()).thenReturn("/v3/schedules");
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
    public void after401YouDoNotNeedSessionTokenToSignOut() throws Exception {
        UserSessionInfo session = new UserSessionInfo();
        Tests.setVariableValueInObject(session, "sessionToken", "sessionToken");
        when(userSessionInfoProvider.retrieveSession()).thenReturn(session);
        
        when(response.request()).thenReturn(request);
        when(url.toString()).thenReturn("/v3/auth/signOut");
        when(request.url()).thenReturn(url);
        when(request.newBuilder()).thenReturn(builder);
        when(builder.header(anyString(), anyString())).thenReturn(builder);
        when(builder.build()).thenReturn(request);
        
        Request request = authHandler.authenticate(route, response);
        
        assertNull(request);
    }
    
    @Test
    public void authenticateNoAuthDoesNotAddHeader() throws Exception {
        UserSessionInfo session = new UserSessionInfo();
        Tests.setVariableValueInObject(session, "sessionToken", "sessionToken");
        when(userSessionInfoProvider.retrieveSession()).thenReturn(session);
        
        when(response.request()).thenReturn(request);
        when(url.toString()).thenReturn("/v3/auth/emailSignIn");
        when(request.url()).thenReturn(url);
        
        Request request = authHandler.authenticate(route, response);
        
        assertNull(request);
        verify(userSessionInfoProvider, never()).reauthenticate();
        verify(builder, never()).header(HeaderInterceptor.BRIDGE_SESSION, "sessionToken");
    }

    @Test
    public void authenticateDoesNotCallReauthIfSessionHasChanged() throws Exception {
        UserSessionInfo session = new UserSessionInfo();
        Tests.setVariableValueInObject(session, "sessionToken", "sessionToken");
        when(userSessionInfoProvider.retrieveSession()).thenReturn(session);
        when(userSessionInfoProvider.getSession()).thenReturn(session);


        when(response.request()).thenReturn(request);
        when(url.toString()).thenReturn("/v3/schedules");
        when(request.url()).thenReturn(url);
        when(request.newBuilder()).thenReturn(builder);
        when(builder.header(anyString(), anyString())).thenReturn(builder);
        when(builder.build()).thenReturn(request);

        AuthenticationHandler spyAuthHandler = spy(authHandler);
        doReturn(true).when(spyAuthHandler).doesSessionHaveTokenAndIsItDifferentFromRequest(request, session);

        Request request = spyAuthHandler.authenticate(route, response);

        assertNotNull(request);
        verify(userSessionInfoProvider, never()).reauthenticate();
        verify(builder).header(HeaderInterceptor.BRIDGE_SESSION, "sessionToken");
    }

    @Test
    public void isRequestUsingOldSession_SameSessionToken() {
        Request request = mock(Request.class);
        when(request.headers(HeaderInterceptor.BRIDGE_SESSION)).thenReturn(Arrays.asList("sessionToken"));
        UserSessionInfo session = mock(UserSessionInfo.class);
        when(session.getSessionToken()).thenReturn("sessionToken");

        assertFalse(authHandler.doesSessionHaveTokenAndIsItDifferentFromRequest(request,session));
    }

    @Test
    public void isRequestUsingOldSession_NoSessionToken() {
        Request request = mock(Request.class);
        when(request.headers(HeaderInterceptor.BRIDGE_SESSION)).thenReturn(Arrays.asList("sessionToken"));
        UserSessionInfo session = mock(UserSessionInfo.class);

        assertFalse(authHandler.doesSessionHaveTokenAndIsItDifferentFromRequest(request,null));
        assertFalse(authHandler.doesSessionHaveTokenAndIsItDifferentFromRequest(request,session));

    }

    @Test
    public void isRequestUsingOldSession_NullHeaders() {
        Request request = mock(Request.class);
        when(request.headers(HeaderInterceptor.BRIDGE_SESSION)).thenReturn(null);
        UserSessionInfo session = mock(UserSessionInfo.class);
        when(session.getSessionToken()).thenReturn("newSessionToken");

        assertTrue(authHandler.doesSessionHaveTokenAndIsItDifferentFromRequest(request,session));
    }

    @Test
    public void isRequestUsingOldSession_NoTokenInHeaders() {
        Request request = mock(Request.class);
        when(request.headers(HeaderInterceptor.BRIDGE_SESSION)).thenReturn(Collections.<String>emptyList());
        UserSessionInfo session = mock(UserSessionInfo.class);
        when(session.getSessionToken()).thenReturn("newSessionToken");

        assertTrue(authHandler.doesSessionHaveTokenAndIsItDifferentFromRequest(request,session));
    }

    @Test
    public void isRequestUsingOldSession_DifferentSessionToken() {
        Request request = mock(Request.class);
        when(request.headers(HeaderInterceptor.BRIDGE_SESSION)).thenReturn(Arrays.asList("sessionToken"));
        UserSessionInfo session = mock(UserSessionInfo.class);
        when(session.getSessionToken()).thenReturn("newSessionToken");

        assertTrue(authHandler.doesSessionHaveTokenAndIsItDifferentFromRequest(request,session));
    }
}
