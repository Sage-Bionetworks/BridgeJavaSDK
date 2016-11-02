package org.sagebionetworks.bridge.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doReturn;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import org.sagebionetworks.bridge.rest.exceptions.EntityNotFoundException;
import org.sagebionetworks.bridge.rest.exceptions.InvalidEntityException;

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
    
    @Test
    public void interceptInvalidEntityException() throws Exception {
        String json = Tests.unescapeJson("{'errors':{'field2':['error message 3','error message 4'],"+
                "'field1':['error message 1','error message 2']},'statusCode':400,'endpoint':"+
                "'http://endpoint/','message':'General error message'}");
        
        doReturn(request).when(chain).request();
        doReturn(response).when(chain).proceed(request);
        doReturn(400).when(response).code();
        doReturn(request).when(response).request();
        doReturn(httpUrl).when(request).url();
        doReturn("http://someurl/").when(httpUrl).toString();
        doReturn(body).when(response).body();
        doReturn(json).when(body).string();
        try {
            ErrorResponseInterceptor interceptor = new ErrorResponseInterceptor();
            interceptor.intercept(chain);
            fail("Should have thrown exception");
        } catch(InvalidEntityException e) {
            assertEquals(400, e.getStatusCode());
            assertEquals("General error message", e.getMessage());
            
            Map<String,List<String>> errors = e.getErrors();
            assertEquals("error message 1", errors.get("field1").get(0));
            assertEquals("error message 2", errors.get("field1").get(1));
            assertEquals("error message 3", errors.get("field2").get(0));
            assertEquals("error message 4", errors.get("field2").get(1));
        }
        
    }
}
