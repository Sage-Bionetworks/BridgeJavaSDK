package org.sagebionetworks.bridge.sdk.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doReturn;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import org.sagebionetworks.bridge.sdk.rest.exceptions.EntityNotFoundException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor.Chain;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Request.class, Response.class, Chain.class, HttpUrl.class, ResponseBody.class})
public class ErrorResponseInterceptorTest {

    @Mock
    Chain chain;
    
    @Mock
    Request request;
    
    @Mock
    Response response;
    
    @Mock
    HttpUrl httpUrl;
    
    @Mock
    ResponseBody body;
    
    @Test
    public void interceptEntityNotFoundException() throws IOException {
        doReturn(request).when(chain).request();
        doReturn(response).when(chain).proceed(request);
        doReturn(404).when(response).code();
        doReturn(request).when(response).request();
        doReturn(httpUrl).when(request).url();
        doReturn("http://someurl/").when(httpUrl).toString();
        doReturn(body).when(response).body();
        doReturn("{\"message\":\"User not found.\"}").when(body).string();
        
        try {
            ErrorResponseInterceptor interceptor = new ErrorResponseInterceptor();
            interceptor.intercept(chain);
            fail("Should have thrown exception");
        } catch(EntityNotFoundException e) {
            assertEquals(404, e.getStatusCode());
            assertEquals("User not found.", e.getMessage());
        }
    }
}
