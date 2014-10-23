package org.sagebionetworks.bridge.sdk;


class ConsentApiCaller extends BaseApiCaller {

    private ConsentApiCaller(ClientProvider provider) {
        super(provider);
    }

    static ConsentApiCaller valueOf(ClientProvider provider) {
        return new ConsentApiCaller(provider);
    }

    void suspendDataSharing() {
        String url = provider.getConfig().getConsentSuspendApi();
        post(url);
    }

    void resumeDataSharing() {
        String url = provider.getConfig().getConsentResumeApi();
        post(url);
    }

}
