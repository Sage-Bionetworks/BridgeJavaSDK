package org.sagebionetworks.bridge.sdk;

import java.nio.charset.Charset;
import java.nio.file.Path;

import org.sagebionetworks.bridge.sdk.models.UploadRequest;
import org.sagebionetworks.bridge.sdk.models.UploadSession;

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
        return null;
    }

    void upload(UploadSession session, UploadRequest request, Path path, Charset cs) {

    }

    void close(UploadSession session) {

    }
}
