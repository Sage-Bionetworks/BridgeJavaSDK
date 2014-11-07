package org.sagebionetworks.bridge.sdk;

import java.io.File;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
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
            HttpResponse response = post(url, json);
            return getResponseBodyAsType(response, UploadSession.class);
        } catch (JsonProcessingException e) {
            throw new BridgeSDKException("Could not process UploadRequest. Are you sure it is correct? "
                    + request.toString(), e);
        }
    }

    void upload(UploadSession session, UploadRequest request, String fileName) {
        HttpEntity entity = new FileEntity(new File(fileName), ContentType.create(request.getContentType()));
        String url = session.getUrl().toString();
        s3Put(url, entity, request);
    }

    void close(UploadSession session) {
        String url = config.getUploadCompleteApi(session.getId());
        post(url);
    }


}
