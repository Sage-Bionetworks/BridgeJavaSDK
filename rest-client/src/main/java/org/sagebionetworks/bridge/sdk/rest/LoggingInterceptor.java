package org.sagebionetworks.bridge.sdk.rest;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

/**
 * Log HTTP request and response information for development (debugging level must be 
 * set to DEBUG).
 */
public class LoggingInterceptor implements Interceptor {

    private static final Logger logger = LoggerFactory.getLogger(LoggingInterceptor.class);
    
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (logger.isDebugEnabled()) {
            if ("POST".equals(request.method())) {
                logger.debug(request.method() + " " + request.url().toString() + "\n    "
                        + redactPasswords(requestBodyToString(request)));
            } else {
                logger.debug(request.method()+" "+request.url().toString());
            }
            if (request.header("User-Agent") != null) {
                logger.debug("User-Agent: " + request.header("User-Agent"));    
            }
            if (request.header("Accept-Language") != null) {
                logger.debug("Accept-Language: " + request.header("Accept-Language"));    
            }
        }
        Response response = chain.proceed(request);
        if (logger.isDebugEnabled()) {
            ResponseBody body = response.body();
            String bodyString = response.body().string();
            
            Response newResponse = response.newBuilder()
                    .body(ResponseBody.create(body.contentType(), bodyString.getBytes())).build();

            logger.debug(response.code()+" RESPONSE: "+redactPasswords(bodyString));
            return newResponse;
        }
        return response;
    }

    private String requestBodyToString(final Request request) throws IOException {
        Request copy = request.newBuilder().build();
        Buffer buffer = new Buffer();
        copy.body().writeTo(buffer);
        return buffer.readUtf8();
    }
    
    private String redactPasswords(String string) {
        return string.replaceAll("password\":\"([^\"]*)\"", "password\":\"[REDACTED]\"");
    }
}
