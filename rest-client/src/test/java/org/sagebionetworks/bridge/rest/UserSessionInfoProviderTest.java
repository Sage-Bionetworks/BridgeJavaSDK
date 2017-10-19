package org.sagebionetworks.bridge.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.sagebionetworks.bridge.rest.api.AuthenticationApi;
import org.sagebionetworks.bridge.rest.exceptions.AuthenticationFailedException;
import org.sagebionetworks.bridge.rest.exceptions.ConsentRequiredException;
import org.sagebionetworks.bridge.rest.exceptions.EntityNotFoundException;
import org.sagebionetworks.bridge.rest.model.ReauthenticateRequest;
import org.sagebionetworks.bridge.rest.model.SignIn;
import org.sagebionetworks.bridge.rest.model.UserSessionInfo;

import retrofit2.Call;
import retrofit2.Response;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ AuthenticationApi.class, Call.class, Response.class })
public class UserSessionInfoProviderTest {

    @Mock
    AuthenticationApi authenticationApi;
    
    @Mock
    Call<UserSessionInfo> call;
    
    @Mock
    Call<UserSessionInfo> call2;
    
    @Mock
    Response<UserSessionInfo> response;
    
    @Captor
    ArgumentCaptor<ReauthenticateRequest> reauthenticateRequestCaptor;

    @Captor
    ArgumentCaptor<SignIn> signInCaptor;
    
    SignIn signIn;
    
    UserSessionInfo userSessionInfo;
    
    UserSessionInfoProvider provider;
    
    @Before
    public void before() {
        userSessionInfo = new UserSessionInfo()
                .email("email@email.com")
                .sessionToken("sessionToken");
        
        signIn = new SignIn().email("email@email.com").password("password").study("test-study");
        
        provider = new UserSessionInfoProvider(authenticationApi, signIn);
    }
    
    @Test
    public void onceItHasSessionItDoesntAuthenticate() throws Exception {
        provider.setSession(userSessionInfo);
        
        assertEquals(userSessionInfo, provider.getSession());
        assertEquals(userSessionInfo, provider.retrieveSession());
    }
    
    @Test
    public void getSessionReturnsNullIfNotSet() {
        assertNull(provider.getSession());
    }
    
    @Test
    public void retrieveSessionWithouSessionSignsInAgain() throws Exception {
        doReturn(call).when(authenticationApi).signIn(any(SignIn.class));
        doReturn(response).when(call).execute();
        doReturn(userSessionInfo).when(response).body();
        
        UserSessionInfo retrieved = provider.retrieveSession();
        assertEquals(userSessionInfo, retrieved);
        verify(authenticationApi).signIn(any(SignIn.class));
    }
    
    @Test
    public void reauthenticate() throws Exception {
        doReturn(call).when(authenticationApi).reauthenticate(any(ReauthenticateRequest.class));
        doReturn(response).when(call).execute();
        doReturn(userSessionInfo).when(response).body();
        
        userSessionInfo.setReauthToken("reauthToken");
        provider.setSession(userSessionInfo);
        
        provider.reauthenticate();
        
        verify(authenticationApi).reauthenticate(reauthenticateRequestCaptor.capture());
        ReauthenticateRequest reauthRequest = reauthenticateRequestCaptor.getValue();
        assertEquals("reauthToken", reauthRequest.getReauthToken());
        assertEquals(signIn.getStudy(), reauthRequest.getStudy());
        assertEquals(signIn.getEmail(), reauthRequest.getEmail());
    }
    
    @Test
    public void failureToReauthenticate404SignsIn() throws Exception {
        doReturn(call2).when(authenticationApi).reauthenticate(any(ReauthenticateRequest.class));
        doThrow(new EntityNotFoundException("message", "endpoint")).when(call2).execute();

        doReturn(call).when(authenticationApi).signIn(any(SignIn.class));
        doReturn(response).when(call).execute();
        doReturn(userSessionInfo).when(response).body();
        
        userSessionInfo.setReauthToken("reauthToken");
        provider.setSession(userSessionInfo);
        
        provider.reauthenticate();
        
        assertEquals(userSessionInfo, provider.getSession());
        verify(authenticationApi, times(1)).reauthenticate(any(ReauthenticateRequest.class));
        verify(authenticationApi, times(1)).signIn(signInCaptor.capture());
    }
    
    @Test
    public void failureToReauthenticate401SignsIn() throws Exception {
        doReturn(call2).when(authenticationApi).reauthenticate(any(ReauthenticateRequest.class));
        doThrow(new AuthenticationFailedException("", "endpoint")).when(call2).execute();

        doReturn(call).when(authenticationApi).signIn(any(SignIn.class));
        doReturn(response).when(call).execute();
        doReturn(userSessionInfo).when(response).body();
        
        userSessionInfo.setReauthToken("reauthToken");
        provider.setSession(userSessionInfo);
        
        provider.reauthenticate();
        
        assertEquals(userSessionInfo, provider.getSession());
        verify(authenticationApi, times(1)).reauthenticate(any(ReauthenticateRequest.class));
        verify(authenticationApi, times(1)).signIn(signInCaptor.capture());
    }
    
    @Test
    public void consentRequiredOnReauthenticate() throws Exception {
        doReturn(call2).when(authenticationApi).reauthenticate(any(ReauthenticateRequest.class));
        doThrow(new ConsentRequiredException("message", "endpoint", userSessionInfo)).when(call2).execute();

        doReturn(call).when(authenticationApi).signIn(any(SignIn.class));
        doReturn(response).when(call).execute();
        doReturn(userSessionInfo).when(response).body();
        
        userSessionInfo.setReauthToken("reauthToken");
        provider.setSession(userSessionInfo);
        
        provider.reauthenticate();
        
        assertEquals(userSessionInfo, provider.getSession());
        verify(authenticationApi).reauthenticate(any(ReauthenticateRequest.class));
    }
    
    @Test
    public void consentRequiredOnSignsIn() throws Exception {
        doReturn(call2).when(authenticationApi).signIn(any(SignIn.class));
        doThrow(new ConsentRequiredException("message", "endpoint", userSessionInfo)).when(call2).execute();

        provider.reauthenticate();
        
        assertEquals(userSessionInfo, provider.getSession());
        verify(authenticationApi).signIn(any(SignIn.class));
    }    
}
