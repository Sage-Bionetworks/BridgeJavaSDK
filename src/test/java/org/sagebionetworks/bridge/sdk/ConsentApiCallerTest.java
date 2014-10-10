package org.sagebionetworks.bridge.sdk;

import org.junit.Before;
import org.junit.Test;

public class ConsentApiCallerTest {

    ConsentApiCaller consentApi;

    @Before
    public void before() {
        ClientProvider provider = ClientProvider.valueOf();



        consentApi = ConsentApiCaller.valueOf(provider);
    }

    @Test
    public void canConsentToResearchAndToggleDataSharing() {

    }
}
