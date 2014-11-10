package org.sagebionetworks.bridge.sdk;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
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

    void upload(UploadSession session, UploadRequest request, String filename) {
        HttpEntity entity = null;
        try {
            byte[] b = Files.readAllBytes(Paths.get(filename));
            entity = new ByteArrayEntity(b, ContentType.create(request.getContentType()));
        } catch (FileNotFoundException e) {
            throw new BridgeSDKException(e);
        } catch (IOException e) {
            throw new BridgeSDKException(e);
        }
        String url = session.getUrl().toString();
        s3Put(url, entity, request);
    }

    void close(UploadSession session) {
        String url = config.getUploadCompleteApi(session.getId());
        post(url);
    }


}
