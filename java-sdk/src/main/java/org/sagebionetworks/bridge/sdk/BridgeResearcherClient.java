package org.sagebionetworks.bridge.sdk;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;

import org.sagebionetworks.bridge.sdk.models.PagedResourceList;
import org.sagebionetworks.bridge.sdk.models.accounts.AccountSummary;
import org.sagebionetworks.bridge.sdk.models.accounts.StudyParticipant;
import org.sagebionetworks.bridge.sdk.models.holders.IdentifierHolder;
import org.sagebionetworks.bridge.sdk.models.studies.Study;

class BridgeResearcherClient extends BaseApiCaller implements ResearcherClient {

    private static final Logger logger = LoggerFactory.getLogger(BridgeResearcherClient.class);
    
    private static final TypeReference<PagedResourceList<AccountSummary>> ACCOUNT_SUMMARY_PAGED_RESOURCE_LIST = 
            new TypeReference<PagedResourceList<AccountSummary>>() {};
                    
    BridgeResearcherClient(BridgeSession session) {
        super(session);
    }

    @Override
    public Study getStudy() {
        session.checkSignedIn();
        return get(config.getCurrentStudyApi(), Study.class);
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
    public StudyParticipant getStudyParticipant(String id) {
        session.checkSignedIn();
        checkArgument(isNotBlank(id), CANNOT_BE_BLANK, "id");

        return get(config.getParticipantApi(id), StudyParticipant.class);
    }
    
    @Override
    public IdentifierHolder createStudyParticipant(StudyParticipant participant) {
        session.checkSignedIn();
        checkNotNull(participant);
        
        return post(config.getParticipantsApi(), participant, IdentifierHolder.class);
    }
    
    @Override
    public void updateStudyParticipant(StudyParticipant participant) {
        session.checkSignedIn();
        checkNotNull(participant);
        checkArgument(isNotBlank(participant.getId()), CANNOT_BE_BLANK, "id");
        
        post(config.getParticipantApi(participant.getId()), participant);

        // User is signed out if they edit their own StudyParticipant through this API.
        if (session.getStudyParticipant().getId().equals(participant.getId())) {
            logger.warn("Client edited self through researcher participant API, session has been signed out. Sign back in to update your session.");
            session.removeSession();
        }
    }
    
    @Override
    public void requestResetPassword(String id) {
        session.checkSignedIn();
        checkArgument(isNotBlank(id), CANNOT_BE_BLANK, "id");

        post(config.getParticipantRequestResetPasswordApi(id));
    }
}
