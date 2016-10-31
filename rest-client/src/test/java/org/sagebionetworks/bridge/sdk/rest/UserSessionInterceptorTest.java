package org.sagebionetworks.bridge.sdk.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import org.sagebionetworks.bridge.sdk.Tests;
import org.sagebionetworks.bridge.sdk.rest.model.SignIn;
import org.sagebionetworks.bridge.sdk.rest.model.UserSessionInfo;

import com.google.common.collect.Lists;

import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor.Chain;
import okio.Buffer;
import okhttp3.MediaType;
import okhttp3.Protocol;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Request.class, Response.class, Chain.class, HttpUrl.class, ResponseBody.class,
    Response.Builder.class, Buffer.class})
public class UserSessionInterceptorTest {
    
    private SignIn signIn;
    private UserSessionInfo userSessionInfo;
    
    @Spy
    UserSessionInterceptor interceptor = new UserSessionInterceptor();
    
    @Mock
    Chain chain;
    
    @Mock
    Request request;
    
    @Mock
    HttpUrl httpUrl;
    
    @Mock
    Response response;
    
    @Mock
    Request.Builder requestBuilder;
    
    @Mock
    Buffer buffer;
    
    @Mock
    ResponseBody responseBody;
    
    @Mock
    RequestBody requestBody;
    
    @Before
    public void before() {
        signIn = new SignIn().study("studyId").email("email@email.com").password("password");
        
        userSessionInfo = new UserSessionInfo();
        userSessionInfo.setEmail("email@email.com");
        userSessionInfo.setSessionToken("sessionToken");
    }
    
    @Test
    public void sessionCRUD() {
        interceptor.addSession(signIn, userSessionInfo);
        
        UserSessionInfo retrieved = interceptor.getSession(signIn);
        assertEquals(userSessionInfo, retrieved);
        
        interceptor.removeSession("sessionToken");
        assertNull(interceptor.getSession(signIn));
    }
    
    @Test
    public void interceptsSignOut() throws IOException {
        interceptor.addSession(signIn, userSessionInfo);
        doReturn(request).when(chain).request();
        doReturn(httpUrl).when(request).url();
        doReturn(Lists.newArrayList("v3","auth","signOut")).when(httpUrl).pathSegments();
        doReturn(response).when(chain).proceed(request);
        doReturn(200).when(response).code();
        doReturn("sessionToken").when(request).header("Bridge-Session");
        
        interceptor.intercept(chain);
        
        assertNull(interceptor.getSession(signIn));
    }
    
    @Test
    public void interceptsSignIn() throws IOException {
        doReturn(response).when(chain).proceed(request);
        doReturn(request).when(chain).request();
        
        // Mock request
        doReturn(httpUrl).when(request).url();
        doReturn(Lists.newArrayList("v3","auth","signIn")).when(httpUrl).pathSegments();
        doReturn(requestBuilder).when(request).newBuilder();
        doReturn(request).when(requestBuilder).build();
        doReturn(requestBody).when(request).body();
        doReturn(Tests.unescapeJson("{'study':'studyId','email':'email@email.com','password':'P4ssword'}")).when(buffer).readUtf8();
        // Just assume creating a buffer works.
        doReturn(buffer).when(interceptor).createBuffer();
        
        // Mock response
        doReturn(responseBody).when(response).body();
        doReturn(MediaType.parse("application/json")).when(responseBody).contentType();
        doReturn(Tests.unescapeJson("{'email':'emailInSession@email.com'}")).when(responseBody).string();
        // Just assume copying works
        doReturn(response).when(interceptor).copyResponse(any(Response.class), any(String.class));
        
        interceptor.intercept(chain);
        
        SignIn signIn = new SignIn().study("studyId").email("email@email.com").password("P4ssword");
        
        UserSessionInfo info = interceptor.getSession(signIn);
        assertEquals("emailInSession@email.com", info.getEmail());
    }
    
    @Test
    public void createsBuffer() {
        assertTrue(interceptor.createBuffer() instanceof Buffer);
    }
    
    @Test
    public void copiesResponse() throws IOException {
        Response response = new Response.Builder()
                .code(200)
                .body(responseBody)
                .protocol(Protocol.HTTP_2)
                .request(request).build();
        doReturn(MediaType.parse("application/json")).when(responseBody).contentType();
        
        Response copy = interceptor.copyResponse(response, "{}");
        
        assertNotSame(copy, response);
        assertEquals(200, copy.code());
    }
    
}
