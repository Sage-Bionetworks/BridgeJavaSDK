package org.sagebionetworks.bridge.sdk;

import java.nio.charset.Charset;
import java.nio.file.Path;

import org.apache.http.HttpResponse;
import org.sagebionetworks.bridge.sdk.models.UploadRequest;
import org.sagebionetworks.bridge.sdk.models.UploadSession;

import com.fasterxml.jackson.core.JsonProcessingException;

public class UploadApiCaller extends BaseApiCaller {

    private UploadApiCaller(Session session) {
        super(session);
    };

    static UploadApiCaller valueOf(Session session) {
        return new UploadApiCaller(session);
    }

    static UploadApiCaller valueOf() {
        return new UploadApiCaller(null);
    }

    UploadSession requestUploadSession(UploadRequest request) {
        try {
            String url = config.getUploadApi();
            String json = mapper.writeValueAsString(request);
            HttpResponse response = put(url, json);
            return getResponseBodyAsType(response, UploadSession.class);
        } catch (JsonProcessingException e) {
            throw new BridgeSDKException("Could not process UploadRequest. Are you sure it is correct? "
                    + request.toString(), e);
        }
    }

    void upload(UploadSession session, UploadRequest request, Path path, Charset cs) {

    }

    void close(UploadSession session) {

    }
}
