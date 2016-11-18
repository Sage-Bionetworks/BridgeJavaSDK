package org.sagebionetworks.bridge.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doReturn;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import org.sagebionetworks.bridge.rest.exceptions.BadRequestException;
import org.sagebionetworks.bridge.rest.exceptions.BridgeSDKException;
import org.sagebionetworks.bridge.rest.exceptions.ConcurrentModificationException;
import org.sagebionetworks.bridge.rest.exceptions.ConsentRequiredException;
import org.sagebionetworks.bridge.rest.exceptions.EntityAlreadyExistsException;
import org.sagebionetworks.bridge.rest.exceptions.EntityNotFoundException;
import org.sagebionetworks.bridge.rest.exceptions.InvalidEntityException;
import org.sagebionetworks.bridge.rest.exceptions.NotAuthenticatedException;
import org.sagebionetworks.bridge.rest.exceptions.PublishedSurveyException;
import org.sagebionetworks.bridge.rest.exceptions.UnauthorizedException;
import org.sagebionetworks.bridge.rest.exceptions.UnsupportedVersionException;

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

    @Before
    public void setup() throws Exception {
        doReturn(request).when(chain).request();
        doReturn(response).when(chain).proceed(request);
        doReturn(request).when(response).request();
        doReturn(httpUrl).when(request).url();
        //noinspection ResultOfMethodCallIgnored
        doReturn("http://someurl/").when(httpUrl).toString();
        doReturn(body).when(response).body();
    }

    @Test
    public void ok200() throws Exception {
        doReturn(200).when(response).code();
        ErrorResponseInterceptor interceptor = new ErrorResponseInterceptor();
        Response retval = interceptor.intercept(chain);
        assertSame(response, retval);
    }

    @Test
    public void publishedSurvey400() throws Exception {
        doReturn(400).when(response).code();
        doReturn("{\"message\":\"A published survey cannot be updated or deleted (only closed).\"}").when(body)
                .string();

        try {
            ErrorResponseInterceptor interceptor = new ErrorResponseInterceptor();
            interceptor.intercept(chain);
            fail("Should have thrown exception");
        } catch(PublishedSurveyException e) {
            assertEquals(400, e.getStatusCode());
            assertEquals("A published survey cannot be updated or deleted (only closed).", e.getMessage());
        }
    }

    @Test
    public void badRequest400() throws Exception {
        doReturn(400).when(response).code();
        doReturn("{\"message\":\"Bad request\"}").when(body).string();

        try {
            ErrorResponseInterceptor interceptor = new ErrorResponseInterceptor();
            interceptor.intercept(chain);
            fail("Should have thrown exception");
        } catch(BadRequestException e) {
            assertEquals(400, e.getStatusCode());
            assertEquals("Bad request", e.getMessage());
        }
    }

    @Test
    public void unrecognizedUrl400() throws Exception {
        // Play sends responses like these if you call an unrecognized URL.
        doReturn(400).when(response).code();
        doReturn("<html><body>Play returns HTML instead of JSON</body></html>").when(body).string();
        doReturn("Play Framework does not recognize this URL").when(response).message();

        try {
            ErrorResponseInterceptor interceptor = new ErrorResponseInterceptor();
            interceptor.intercept(chain);
            fail("Should have thrown exception");
        } catch(BadRequestException e) {
            assertEquals(400, e.getStatusCode());
            assertEquals("Play Framework does not recognize this URL", e.getMessage());
        }
    }

    @Test
    public void notAuthenticated401() throws Exception {
        doReturn(401).when(response).code();
        doReturn("{\"message\":\"Not signed in.\"}").when(body).string();

        try {
            ErrorResponseInterceptor interceptor = new ErrorResponseInterceptor();
            interceptor.intercept(chain);
            fail("Should have thrown exception");
        } catch(NotAuthenticatedException e) {
            assertEquals(401, e.getStatusCode());
            assertEquals("Not signed in.", e.getMessage());
        }
    }

    @Test
    public void unauthorized403() throws Exception {
        doReturn(403).when(response).code();
        doReturn("{\"message\":\"Caller does not have permission to access this service.\"}").when(body).string();

        try {
            ErrorResponseInterceptor interceptor = new ErrorResponseInterceptor();
            interceptor.intercept(chain);
            fail("Should have thrown exception");
        } catch(UnauthorizedException e) {
            assertEquals(403, e.getStatusCode());
            assertEquals("Caller does not have permission to access this service.", e.getMessage());
        }
    }

    @Test
    public void interceptEntityNotFoundException() throws IOException {
        doReturn(404).when(response).code();
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
        
        doReturn(400).when(response).code();
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

    @Test
    public void entityAlreadyExists409() throws Exception {
        doReturn(409).when(response).code();
        doReturn("{\"message\":\"Survey already exists\"}").when(body).string();

        try {
            ErrorResponseInterceptor interceptor = new ErrorResponseInterceptor();
            interceptor.intercept(chain);
            fail("Should have thrown exception");
        } catch(EntityAlreadyExistsException e) {
            assertEquals(409, e.getStatusCode());
            assertEquals("Survey already exists", e.getMessage());
        }
    }

    @Test
    public void concurrentModification409() throws Exception {
        doReturn(409).when(response).code();
        doReturn("{\"message\":\"Survey has the wrong version number\"}").when(body).string();

        try {
            ErrorResponseInterceptor interceptor = new ErrorResponseInterceptor();
            interceptor.intercept(chain);
            fail("Should have thrown exception");
        } catch(ConcurrentModificationException e) {
            assertEquals(409, e.getStatusCode());
            assertEquals("Survey has the wrong version number", e.getMessage());
        }
    }

    @Test
    public void unsupportedVersion410() throws Exception {
        doReturn(410).when(response).code();
        doReturn("{\"message\":\"This app version is not supported. Please update.\"}").when(body).string();

        try {
            ErrorResponseInterceptor interceptor = new ErrorResponseInterceptor();
            interceptor.intercept(chain);
            fail("Should have thrown exception");
        } catch(UnsupportedVersionException e) {
            assertEquals(410, e.getStatusCode());
            assertEquals("This app version is not supported. Please update.", e.getMessage());
        }
    }

    // branch coverage: This is not actually possible, but we should test for it to make sure our code can handle it.
    @Test
    public void consentRequired412NoSession() throws Exception {
        doReturn(412).when(response).code();
        doReturn("Missing session info!").when(body).string();
        doReturn("Missing session info!").when(response).message();

        try {
            ErrorResponseInterceptor interceptor = new ErrorResponseInterceptor();
            interceptor.intercept(chain);
            fail("Should have thrown exception");
        } catch(ConsentRequiredException e) {
            assertEquals(412, e.getStatusCode());
            assertEquals("Consent required.", e.getMessage());
        }
    }

    @Test
    public void serverError500() throws Exception {
        doReturn(500).when(response).code();
        doReturn("{\"message\":\"Something went terribly wrong!\"}").when(body).string();

        try {
            ErrorResponseInterceptor interceptor = new ErrorResponseInterceptor();
            interceptor.intercept(chain);
            fail("Should have thrown exception");
        } catch(BridgeSDKException e) {
            assertEquals(500, e.getStatusCode());
            assertEquals("Something went terribly wrong!", e.getMessage());
        }
    }

    @Test
    public void serviceUnavailable503() throws Exception {
        // This might happen if the load balancer is overloaded.
        doReturn(503).when(response).code();
        doReturn("503 Service Unavailable").when(body).string();
        doReturn("Load balancer error message").when(response).message();

        try {
            ErrorResponseInterceptor interceptor = new ErrorResponseInterceptor();
            interceptor.intercept(chain);
            fail("Should have thrown exception");
        } catch(BridgeSDKException e) {
            assertEquals(503, e.getStatusCode());
            assertEquals("Load balancer error message", e.getMessage());
        }
    }

    // branch coverage: Not sure if this is possible, but we should test for it.
    @Test
    public void unknownErrorInvalidBodyNoMessage() throws Exception {
        // This might happen if the load balancer is overloaded.
        doReturn(500).when(response).code();
        doReturn("Invalid response body").when(body).string();
        doReturn(null).when(response).message();

        try {
            ErrorResponseInterceptor interceptor = new ErrorResponseInterceptor();
            interceptor.intercept(chain);
            fail("Should have thrown exception");
        } catch(BridgeSDKException e) {
            assertEquals(500, e.getStatusCode());
            assertEquals("There has been an error on the server", e.getMessage());
        }
    }
}
