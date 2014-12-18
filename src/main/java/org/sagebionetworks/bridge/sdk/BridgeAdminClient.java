package org.sagebionetworks.bridge.sdk;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.joda.time.DateTime;
import org.sagebionetworks.bridge.sdk.models.ResourceList;
import org.sagebionetworks.bridge.sdk.models.holders.GuidCreatedOnVersionHolder;
import org.sagebionetworks.bridge.sdk.models.holders.SimpleVersionHolder;
import org.sagebionetworks.bridge.sdk.models.holders.VersionHolder;
import org.sagebionetworks.bridge.sdk.models.studies.Study;
import org.sagebionetworks.bridge.sdk.models.users.SignUpCredentials;

import com.fasterxml.jackson.core.type.TypeReference;

final class BridgeAdminClient extends BaseApiCaller implements AdminClient {

    private BridgeAdminClient(BridgeSession session) {
        super(session);
    }

    static BridgeAdminClient valueOf(BridgeSession session) {
        return new BridgeAdminClient(session);
    }

    @Override
    public boolean createUser(SignUpCredentials signUp, Set<String> roles, boolean consent) {
        session.checkSignedIn();
        checkArgument(isNotBlank(signUp.getUsername()));
        checkArgument(isNotBlank(signUp.getPassword()));
        checkArgument(isNotBlank(signUp.getEmail()));

        HttpResponse response = post(config.getUserManagementApi(), new AdminSignUpCredentials(signUp, roles, consent));
        return response.getStatusLine().getStatusCode() == 201;
    }
    @Override
    public boolean deleteUser(String email) {
        session.checkSignedIn();
        checkArgument(isNotBlank(email));

        Map<String,String> queryParams = new HashMap<String,String>();
        queryParams.put("email", email);

        HttpResponse response = delete(config.getUserManagementApi() + toQueryString(queryParams));
        return response.getStatusLine().getStatusCode() == 200;
    }
    @Override
    public Study getStudy(String identifier) {
        session.checkSignedIn();
        return get(config.getAdminStudyApi(identifier), Study.class);
    }
    @Override
    public ResourceList<Study> getAllStudies() {
        session.checkSignedIn();
        return get(config.getAdminStudiesApi(), new TypeReference<ResourceListImpl<Study>>() {});
    }
    @Override
    public VersionHolder createStudy(Study study) {
        session.checkSignedIn();
        return post(config.getAdminStudiesApi(), study, SimpleVersionHolder.class);
    }
    @Override
    public VersionHolder updateStudy(Study study) {
        session.checkSignedIn();
        return post(config.getAdminStudyApi(study.getIdentifier()), study, SimpleVersionHolder.class);
    }
    @Override
    public void deleteStudy(String identifier) {
        session.checkSignedIn();
        delete(config.getAdminStudyApi(identifier));
    }

    @Override
    public void deleteSurvey(String guid, DateTime createdOn) {
        session.checkSignedIn();
        delete(config.getSurveyApi(guid, createdOn));
    }

    @Override
    public void deleteSurvey(GuidCreatedOnVersionHolder keys) {
        session.checkSignedIn();
        delete(config.getSurveyApi(keys.getGuid(), keys.getCreatedOn()));
    }
}
