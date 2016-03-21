package org.sagebionetworks.bridge.sdk;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Strings;

import java.util.HashMap;
import java.util.Map;

import org.sagebionetworks.bridge.sdk.models.PagedResourceList;
import org.sagebionetworks.bridge.sdk.models.accounts.AccountSummary;
import org.sagebionetworks.bridge.sdk.models.accounts.StudyParticipant;

import static com.google.common.base.Preconditions.checkArgument;

class BridgeResearcherClient extends BaseApiCaller implements ResearcherClient {

    private static final TypeReference<PagedResourceList<AccountSummary>> ACCOUNT_SUMMARY_PAGED_RESOURCE_LIST = 
            new TypeReference<PagedResourceList<AccountSummary>>() {};

    BridgeResearcherClient(BridgeSession session) {
        super(session);
    }

    @Override
    public void signOutUser(String email) {
        session.checkSignedIn();
        checkArgument(!Strings.isNullOrEmpty(email), CANNOT_BE_BLANK, "email");

        Map<String, String> queryParams = new HashMap<String, String>();
        queryParams.put("email", email);

        post(config.getUsersSignOutApi() + toQueryString(queryParams));
    }
    
    @Override
    public PagedResourceList<AccountSummary> getPagedAccountSummaries(int offsetBy, int pageSize, String emailFilter) {
        session.checkSignedIn();
        
        return get(config.getParticipantsApi(offsetBy, pageSize, emailFilter), ACCOUNT_SUMMARY_PAGED_RESOURCE_LIST);
    }
    
    @Override
    public StudyParticipant getStudyParticipant(String email) {
        session.checkSignedIn();
        
        return get(config.getParticipant(email), StudyParticipant.class);
    }
}
