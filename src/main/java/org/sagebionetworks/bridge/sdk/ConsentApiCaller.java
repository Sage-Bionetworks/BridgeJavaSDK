package org.sagebionetworks.bridge.sdk;


class ConsentApiCaller extends BaseApiCaller {

    private final String CONSENT = provider.getConfig().getConsentApi();
    private final String SUSPEND = CONSENT + "/dataSharing/suspend";
    private final String RESUME = CONSENT + "/dataSharing/resume";

    private ConsentApiCaller(ClientProvider provider) {
        super(provider);
    }

    static ConsentApiCaller valueOf(ClientProvider provider) {
        return new ConsentApiCaller(provider);
    }

    void suspendDataSharing() {
        assert provider.isSignedIn();

        post(SUSPEND);
    }

    void resumeDataSharing() {
        assert provider.isSignedIn();

        post(RESUME);
    }

}
