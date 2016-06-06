package org.sagebionetworks.bridge.sdk;

import org.sagebionetworks.bridge.sdk.models.studies.Study;

class BridgeStudyStaffClient extends BaseApiCaller implements StudyStaffClient {
    
    BridgeStudyStaffClient(BridgeSession session) {
        super(session);
    }
    
    @Override
    public Study getStudy() {
        session.checkSignedIn();
        return get(config.getCurrentStudyApi(), Study.class);
    }
}
