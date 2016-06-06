package org.sagebionetworks.bridge.sdk;

import org.sagebionetworks.bridge.sdk.models.studies.Study;

class StudyStaffClient extends BaseApiCaller {
    
    StudyStaffClient(BridgeSession session) {
        super(session);
    }
    
    public Study getStudy() {
        session.checkSignedIn();
        return get(config.getCurrentStudyApi(), Study.class);
    }
}
