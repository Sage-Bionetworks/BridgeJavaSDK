package org.sagebionetworks.bridge.sdk;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import com.fasterxml.jackson.core.type.TypeReference;

import org.sagebionetworks.bridge.sdk.models.PagedResourceList;
import org.sagebionetworks.bridge.sdk.models.accounts.AccountSummary;
import org.sagebionetworks.bridge.sdk.models.accounts.StudyParticipant;

class BridgeResearcherClient extends BaseApiCaller implements ResearcherClient {

    private static final TypeReference<PagedResourceList<AccountSummary>> ACCOUNT_SUMMARY_PAGED_RESOURCE_LIST = 
            new TypeReference<PagedResourceList<AccountSummary>>() {};
                    
    BridgeResearcherClient(BridgeSession session) {
        super(session);
    }

    @Override
    public void signOutUser(String email) {
        session.checkSignedIn();

        post(config.getUsersSignOutApi(email));
    }
    
    @Override
    public PagedResourceList<AccountSummary> getPagedAccountSummaries(int offsetBy, int pageSize, String emailFilter) {
        session.checkSignedIn();
        
        return get(config.getParticipantsApi(offsetBy, pageSize, emailFilter), ACCOUNT_SUMMARY_PAGED_RESOURCE_LIST);
    }
    
    @Override
    public StudyParticipant getStudyParticipant(String email) {
        session.checkSignedIn();
        checkArgument(isNotBlank(email), CANNOT_BE_BLANK, "email");

        return get(config.getParticipantApi(email), StudyParticipant.class);
    }
    
    @Override
    public void createStudyParticipant(StudyParticipant participant) {
        session.checkSignedIn();
        checkNotNull(participant);
        
        post(config.getParticipantsApi(), participant);
    }
    
    @Override
    public void updateStudyParticipant(StudyParticipant participant) {
        session.checkSignedIn();
        checkNotNull(participant);
        checkArgument(isNotBlank(participant.getEmail()), CANNOT_BE_BLANK, "email");
        
        post(config.getParticipantApi(participant.getEmail()), participant);
    }
}
