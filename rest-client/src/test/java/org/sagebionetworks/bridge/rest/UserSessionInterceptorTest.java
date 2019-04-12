package org.sagebionetworks.bridge.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.sagebionetworks.bridge.rest.exceptions.ConsentRequiredException;
import org.sagebionetworks.bridge.rest.model.UserSessionInfo;

import okhttp3.HttpUrl;
import okhttp3.Interceptor.Chain;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Request.class, Response.class, Chain.class, HttpUrl.class, ResponseBody.class,
        Response.Builder.class, Response.Builder.class})
public class UserSessionInterceptorTest {
    
    private static final MediaType JSON = MediaType.parse("application/json");

    @Mock
    UserSessionInfoProvider userSessionInfoProvider;
    
    @Mock
    Chain chain;
    
    @Mock
    Request request;
    
    @Mock
    Response response;
    
    @Mock
    ResponseBody responseBody;
    
    @Mock
    Response.Builder responseBuilder;
    
    @Mock
    HttpUrl url;
    
    @Captor
    ArgumentCaptor<UserSessionInfo> sessionCaptor;
    
    UserSessionInterceptor interceptor;
    
    @Before
    public void before() {
        interceptor = new UserSessionInterceptor(userSessionInfoProvider);
    }
    
    @Test
    public void returnsHttpSession() throws IOException {
        doReturn(request).when(chain).request();
        doReturn(response).when(chain).proceed(request);
        doReturn(request).when(response).request();
        doReturn(200).when(response).code();
        doReturn(JSON).when(responseBody).contentType();
        
        doReturn(url).when(request).url();
        doReturn("POST").when(request).method();
        doReturn("/v3/auth/signIn").when(url).toString();
        doReturn(responseBody).when(response).body();
        doReturn("{\"sessionToken\":\"asdf\"}").when(responseBody).string();
        
        doReturn(responseBuilder).when(response).newBuilder();
        doReturn(responseBuilder).when(responseBuilder).body(any(ResponseBody.class));
        
        interceptor.intercept(chain);
        
        verify(userSessionInfoProvider).setSession(sessionCaptor.capture());
        assertEquals("asdf", sessionCaptor.getValue().getSessionToken());
    }
    
    @Test
    public void capturesSessionInConsentRequiredException() throws IOException {
        UserSessionInfo session = new UserSessionInfo();
        doReturn(request).when(chain).request();
        doThrow(new ConsentRequiredException("Consent required", "", session)).when(chain).proceed(request);
        
        try {
            interceptor.intercept(chain);
            fail("Test should have thrown exception");
        } catch(ConsentRequiredException e) {
        }
        verify(userSessionInfoProvider).setSession(session);
    }

    @Test
    public void doesNothing() throws IOException {
        doReturn(request).when(chain).request();
        doReturn(response).when(chain).proceed(request);
        doReturn(request).when(response).request();
        doReturn(url).when(request).url();
        doReturn("/v3/activityevents").when(url).toString();
        doReturn(200).when(response).code();
        doReturn("GET").when(request).method();

        interceptor.intercept(chain);
        
        verify(userSessionInfoProvider, never()).setSession(any(UserSessionInfo.class));
    }
    
    @Test
    public void capturesSessionOfParticipantSelfUpdate() throws IOException {
        doReturn(request).when(chain).request();
        doReturn(response).when(chain).proceed(request);
        doReturn(request).when(response).request();
        doReturn(url).when(request).url();
        doReturn("/v3/participants/self").when(url).toString();
        doReturn(200).when(response).code();
        doReturn("POST").when(request).method();
        
        doReturn(responseBody).when(response).body();
        doReturn("{\"sessionToken\":\"asdf\"}").when(responseBody).string();
        
        doReturn(responseBuilder).when(response).newBuilder();
        doReturn(responseBuilder).when(responseBuilder).body(any(ResponseBody.class));

        interceptor.intercept(chain);
        
        verify(userSessionInfoProvider).setSession(any(UserSessionInfo.class));
    }
    
    @Test
    public void capturesSessionOfIdentifiersUpdate() throws IOException {
        doReturn(request).when(chain).request();
        doReturn(response).when(chain).proceed(request);
        doReturn(request).when(response).request();
        doReturn(url).when(request).url();
        doReturn("/v3/participants/self/identifiers").when(url).toString();
        doReturn(200).when(response).code();
        doReturn("POST").when(request).method();
        
        doReturn(responseBody).when(response).body();
        doReturn("{\"sessionToken\":\"asdf\"}").when(responseBody).string();
        
        doReturn(responseBuilder).when(response).newBuilder();
        doReturn(responseBuilder).when(responseBuilder).body(any(ResponseBody.class));

        interceptor.intercept(chain);
        
        verify(userSessionInfoProvider).setSession(any(UserSessionInfo.class));
    }
    
    @Test
    public void capturesSessionOfConsentCreation() throws IOException {
        doReturn(request).when(chain).request();
        doReturn(response).when(chain).proceed(request);
        doReturn(request).when(response).request();
        doReturn(url).when(request).url();
        doReturn("/v3/subpopulations/53956397-28a1-41e5-b3a5-1f71e3e3a1b7/consents/signature").when(url).toString();
        doReturn(201).when(response).code();
        doReturn("POST").when(request).method();
        
        doReturn(responseBody).when(response).body();
        doReturn("{\"sessionToken\":\"asdf\"}").when(responseBody).string();
        
        doReturn(responseBuilder).when(response).newBuilder();
        doReturn(responseBuilder).when(responseBuilder).body(any(ResponseBody.class));

        interceptor.intercept(chain);
        
        verify(userSessionInfoProvider).setSession(any(UserSessionInfo.class));
    }
    
    @Test
    public void capturesSessionOfConsentWithdrawal() throws IOException {
        doReturn(request).when(chain).request();
        doReturn(response).when(chain).proceed(request);
        doReturn(request).when(response).request();
        doReturn(url).when(request).url();
        doReturn("/v3/subpopulations/53956397-28a1-41e5-b3a5-1f71e3e3a1b7/consents/signature/withdraw").when(url).toString();
        doReturn(200).when(response).code();
        doReturn("POST").when(request).method();
        
        doReturn(responseBody).when(response).body();
        doReturn("{\"sessionToken\":\"asdf\"}").when(responseBody).string();
        
        doReturn(responseBuilder).when(response).newBuilder();
        doReturn(responseBuilder).when(responseBuilder).body(any(ResponseBody.class));

        interceptor.intercept(chain);
        
        verify(userSessionInfoProvider).setSession(any(UserSessionInfo.class));
    }

    @Test
    public void badStringCaseWorks() throws IOException {
        doReturn(request).when(chain).request();
        doReturn(response).when(chain).proceed(request);
        doReturn(request).when(response).request();
        doReturn(url).when(request).url();
        doReturn("/V3/PARTICIPANTS/SELF").when(url).toString();
        doReturn(200).when(response).code();
        doReturn("post").when(request).method();
        
        doReturn(responseBody).when(response).body();
        doReturn("{\"sessionToken\":\"asdf\"}").when(responseBody).string();
        
        doReturn(responseBuilder).when(response).newBuilder();
        doReturn(responseBuilder).when(responseBuilder).body(any(ResponseBody.class));

        interceptor.intercept(chain);
        
        verify(userSessionInfoProvider).setSession(any(UserSessionInfo.class));
    }
    
    @Test
    public void removesSession() throws IOException {
        doReturn(request).when(chain).request();
        doReturn(response).when(chain).proceed(request);
        doReturn(request).when(response).request();
        doReturn(url).when(request).url();
        doReturn("/v3/auth/signOut").when(url).toString();
        doReturn(200).when(response).code();
        doReturn("POST").when(request).method();
        
        UserSessionInfo session = new UserSessionInfo();
        userSessionInfoProvider.setSession(session);
        
        interceptor.intercept(chain);
        
        assertNull(userSessionInfoProvider.getSession());
    }
}
