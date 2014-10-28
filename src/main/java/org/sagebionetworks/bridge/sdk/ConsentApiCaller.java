package org.sagebionetworks.bridge.sdk;


class ConsentApiCaller extends BaseApiCaller {

    private ConsentApiCaller(Session session) {
        super(session);
    }

    static ConsentApiCaller valueOf(Session session) {
        return new ConsentApiCaller(session);
    }

    void suspendDataSharing() {
        String url = config.getConsentSuspendApi();
        post(url);
    }

    void resumeDataSharing() {
        String url = config.getConsentResumeApi();
        post(url);
    }

}
