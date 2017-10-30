package org.sagebionetworks.bridge.rest;

import static org.sagebionetworks.bridge.rest.HeaderInterceptor.ACCEPT_LANGUAGE;
import static org.sagebionetworks.bridge.rest.HeaderInterceptor.BRIDGE_SESSION;
import static org.sagebionetworks.bridge.rest.HeaderInterceptor.USER_AGENT;

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
class LoggingInterceptor implements Interceptor {
    private static final Logger logger = LoggerFactory.getLogger(LoggingInterceptor.class);

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (logger.isDebugEnabled()) {
            // Build one logging statement that is all the headers, content of one request, this is much easier to
            // follow in the logs.
            StringBuilder sb = new StringBuilder();
            sb.append(request.method()+" "+request.url().toString());
            if (request.header(USER_AGENT) != null) {
                sb.append("\n    "+USER_AGENT + ": " + request.header(USER_AGENT));
            }
            if (request.header(ACCEPT_LANGUAGE) != null) {
                sb.append("\n    "+ACCEPT_LANGUAGE + ": " + request.header(ACCEPT_LANGUAGE));
            }
            if (request.header(BRIDGE_SESSION) != null) {
                sb.append("\n    "+BRIDGE_SESSION + ": " + request.header(BRIDGE_SESSION));
            } else {
                sb.append("\n    "+BRIDGE_SESSION + ": <NONE>");
            }
            if ("POST".equals(request.method())) {
                String body = requestBodyToString(request);
                if (body.length() > 0) {
                    sb.append("\n    "+redactPasswords(body));
                }
            }
            logger.debug(sb.toString());
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
