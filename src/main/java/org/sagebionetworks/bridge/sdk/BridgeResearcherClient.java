package org.sagebionetworks.bridge.sdk;

class BridgeResearcherClient extends BaseApiCaller implements ResearcherClient {

    BridgeResearcherClient(BridgeSession session) {
        super(session);
    }
    @Override
    public void sendStudyParticipantsRoster() {
        session.checkSignedIn();
        
        post(config.getEmailParticipantRosterApi());
    }

}
