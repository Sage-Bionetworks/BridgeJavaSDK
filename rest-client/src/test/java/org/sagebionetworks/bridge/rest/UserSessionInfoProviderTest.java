package org.sagebionetworks.bridge.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.Arrays;

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

    @Mock
    UserSessionInfoProvider.UserSessionInfoChangeListener changeListener;
    
    @Captor
    ArgumentCaptor<SignIn> reauthenticateRequestCaptor;

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
        
        provider = new UserSessionInfoProvider(authenticationApi, signIn.getStudy(), signIn.getEmail(),
                signIn.getPhone(), signIn.getPassword(), null, Arrays.asList(changeListener));
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
    public void retrieveSessionWithoutSessionSignsInAgain() throws Exception {
        doReturn(call).when(authenticationApi).signInV4(any(SignIn.class));
        doReturn(response).when(call).execute();
        doReturn(userSessionInfo).when(response).body();
        
        UserSessionInfo retrieved = provider.retrieveSession();
        assertEquals(userSessionInfo, retrieved);
        verify(authenticationApi).signInV4(any(SignIn.class));
    }
    
    @Test
    public void reauthenticate() throws Exception {
        doReturn(call).when(authenticationApi).reauthenticate(any(SignIn.class));
        doReturn(response).when(call).execute();
        doReturn(userSessionInfo).when(response).body();
        
        userSessionInfo.setReauthToken("reauthToken");
        provider.setSession(userSessionInfo);

        provider.reauthenticate();

        verify(authenticationApi).reauthenticate(reauthenticateRequestCaptor.capture());

        SignIn reauthRequest = reauthenticateRequestCaptor.getValue();
        assertEquals("reauthToken", reauthRequest.getReauthToken());
        assertEquals(signIn.getStudy(), reauthRequest.getStudy());
        assertEquals(signIn.getEmail(), reauthRequest.getEmail());
    }

    @Test
    public void setSessionCallsChangeListener() {
        provider.setSession(userSessionInfo);
        verify(changeListener).onChange(userSessionInfo);
    }
    
    @Test
    public void failureToReauthenticate404SignsIn() throws Exception {
        doReturn(call2).when(authenticationApi).reauthenticate(any(SignIn.class));
        doThrow(new EntityNotFoundException("message", "endpoint")).when(call2).execute();

        doReturn(call).when(authenticationApi).signInV4(any(SignIn.class));
        doReturn(response).when(call).execute();
        doReturn(userSessionInfo).when(response).body();
        
        userSessionInfo.setReauthToken("reauthToken");
        provider.setSession(userSessionInfo);
        
        provider.reauthenticate();
        
        assertEquals(userSessionInfo, provider.getSession());
        verify(authenticationApi, times(1)).reauthenticate(any(SignIn.class));
        verify(authenticationApi, times(1)).signInV4(signInCaptor.capture());
    }
    
    @Test
    public void failureToReauthenticate401SignsIn() throws Exception {
        doReturn(call2).when(authenticationApi).reauthenticate(any(SignIn.class));
        doThrow(new AuthenticationFailedException("", "endpoint")).when(call2).execute();

        doReturn(call).when(authenticationApi).signInV4(any(SignIn.class));
        doReturn(response).when(call).execute();
        doReturn(userSessionInfo).when(response).body();
        
        userSessionInfo.setReauthToken("reauthToken");
        provider.setSession(userSessionInfo);
        
        provider.reauthenticate();
        
        assertEquals(userSessionInfo, provider.getSession());
        verify(authenticationApi, times(1)).reauthenticate(any(SignIn.class));
        verify(authenticationApi, times(1)).signInV4(signInCaptor.capture());
    }
    
    @Test
    public void consentRequiredOnReauthenticate() throws Exception {
        doReturn(call2).when(authenticationApi).reauthenticate(any(SignIn.class));
        doThrow(new ConsentRequiredException("message", "endpoint", userSessionInfo)).when(call2).execute();

        doReturn(call).when(authenticationApi).signIn(any(SignIn.class));
        doReturn(response).when(call).execute();
        doReturn(userSessionInfo).when(response).body();
        
        userSessionInfo.setReauthToken("reauthToken");
        provider.setSession(userSessionInfo);
        
        provider.reauthenticate();
        
        assertEquals(userSessionInfo, provider.getSession());
        verify(authenticationApi).reauthenticate(any(SignIn.class));
    }

    @Test
    public void reauthenticationFailsWithNoCredentialsAllowingSignIn() throws IOException {

        signIn = new SignIn().email("email@email.com").study("test-study");

        provider = new UserSessionInfoProvider(authenticationApi, signIn.getStudy(), signIn.getEmail(),
                signIn.getPhone(), signIn.getPassword(), null, Arrays.asList(changeListener));

        doReturn(call).when(authenticationApi).reauthenticate(any(SignIn.class));
        AuthenticationFailedException e1 = new AuthenticationFailedException("message", "endpoint");
        doThrow(e1).when(call).execute();

        userSessionInfo.setReauthToken("reauthToken");
        provider.setSession(userSessionInfo);

        try {
            provider.reauthenticate();
            fail("This should have thrown an exception");
        } catch(AuthenticationFailedException e) {
            assertEquals(e1, e);
            // Not clear if the framework will loop on this, however.
        }

        verify(authenticationApi, times(1)).reauthenticate(any(SignIn.class));
    }
    
    @Test
    public void everythingGoesWrongOnAuthentication() throws Exception {
        // Neither call works. 
        doReturn(call).when(authenticationApi).reauthenticate(any(SignIn.class));
        AuthenticationFailedException e1 = new AuthenticationFailedException("message", "endpoint");
        doThrow(e1).when(call).execute();

        doReturn(call2).when(authenticationApi).signInV4(any(SignIn.class));
        AuthenticationFailedException e2 = new AuthenticationFailedException("message", "endpoint");
        doThrow(e2).when(call2).execute();

        doReturn(userSessionInfo).when(response).body();
        
        userSessionInfo.setReauthToken("reauthToken");
        provider.setSession(userSessionInfo);
        
        try {
            provider.reauthenticate();
            fail("This should have thrown an exception");
        } catch(AuthenticationFailedException e) {
            assertEquals(e1, e);
            assertEquals(e2, e.getCause());
            // Not clear if the framework will loop on this, however.
        }
        verify(authenticationApi, times(1)).reauthenticate(any(SignIn.class));
        verify(authenticationApi, times(1)).signInV4(any(SignIn.class));
    }
    
    @Test
    public void consentRequiredOnSignsIn() throws Exception {
        doReturn(call2).when(authenticationApi).signInV4(any(SignIn.class));
        doThrow(new ConsentRequiredException("message", "endpoint", userSessionInfo)).when(call2).execute();

        provider.reauthenticate();
        
        assertEquals(userSessionInfo, provider.getSession());
        verify(authenticationApi).signInV4(any(SignIn.class));
    }

    @Test
    public void mergeReauthToken_A() {
        String reauthToken = "reauthToken";
        String sessionToken = "sessionToken";
        UserSessionInfo userSessionInfo = new UserSessionInfo()
                .sessionToken(sessionToken)
                .reauthToken(reauthToken);


        String newSessionToken = "newSessionToken";
        UserSessionInfo newUserSessionInfo = new UserSessionInfo()
                .sessionToken(newSessionToken);


        UserSessionInfoProvider.mergeReauthToken(userSessionInfo, newUserSessionInfo);

        assertEquals(sessionToken, userSessionInfo.getSessionToken()); // check old session token
        assertEquals(newSessionToken, newUserSessionInfo.getSessionToken()); // check this isn't the old session
        assertEquals(reauthToken, newUserSessionInfo.getReauthToken()); // check new session has old reauth token

        String newReauthToken = "newReauthToken";
        String evenNewerSessionToken = "evenNewerSessionToken";
        UserSessionInfo evenNewerSession = new UserSessionInfo()
                .sessionToken(evenNewerSessionToken)
                .reauthToken(newReauthToken);

        UserSessionInfoProvider.mergeReauthToken(newUserSessionInfo, evenNewerSession);

        assertEquals(evenNewerSessionToken, evenNewerSession.getSessionToken());
        assertEquals(newReauthToken, evenNewerSession.getReauthToken()); // check we wrote a new reauth token
    }
}
