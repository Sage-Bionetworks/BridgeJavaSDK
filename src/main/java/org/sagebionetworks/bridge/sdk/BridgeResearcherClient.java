package org.sagebionetworks.bridge.sdk;

import com.google.common.base.Strings;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

class BridgeResearcherClient extends BaseApiCaller implements ResearcherClient {

    BridgeResearcherClient(BridgeSession session) {
        super(session);
    }
    @Override
    public void sendStudyParticipantsRoster() {
        session.checkSignedIn();
        
        post(config.getEmailParticipantRosterApi());
    }

    @Override
    public void signOutUser(String email) {
        session.checkSignedIn();
        checkArgument(!Strings.isNullOrEmpty(email), CANNOT_BE_BLANK, "email");

        Map<String, String> queryParams = new HashMap<String, String>();
        queryParams.put("email", email);

        post(config.getUsersSignOutApi() + toQueryString(queryParams));
    }
}
