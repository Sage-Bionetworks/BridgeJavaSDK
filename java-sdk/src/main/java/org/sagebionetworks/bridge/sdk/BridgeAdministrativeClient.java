package org.sagebionetworks.bridge.sdk;

import org.sagebionetworks.bridge.sdk.models.studies.Study;

public class BridgeAdministrativeClient extends BaseApiCaller implements AdministrativeClient {
    
    BridgeAdministrativeClient(BridgeSession session) {
        super(session);
    }
    
    @Override
    public Study getStudy() {
        session.checkSignedIn();
        return get(config.getCurrentStudyApi(), Study.class);
    }
}
