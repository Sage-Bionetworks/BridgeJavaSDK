package org.sagebionetworks.bridge.sdk;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
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
        try {
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            if (request.getContentType().equals("text/plain")) {
                builder.addBinaryBody("text", new File(fileName), ContentType.DEFAULT_TEXT, fileName);
            } else if (request.getContentType().equals("application/zip")) {
                builder.addBinaryBody("zip", new FileInputStream(fileName), ContentType.create("application/zip"),
                        fileName);
            }
            String url = session.getUrl().toString();
            s3Put(url, builder.build(), request);
        } catch (FileNotFoundException e) {
            throw new BridgeSDKException(e);
        }
    }

    void close(UploadSession session) {
        String url = config.getUploadCompleteApi(session.getId());
        post(url);
    }


}
