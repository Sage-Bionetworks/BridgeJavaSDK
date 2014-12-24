package org.sagebionetworks.bridge.sdk;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

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

    BridgeAdminClient(@Nonnull BridgeSession session, @Nonnull ClientProvider clientProvider) {
        super(session, clientProvider);
    }

    @Override
    public boolean createUser(SignUpCredentials signUp, Set<String> roles, boolean consent) {
        session.checkSignedIn();
        checkArgument(isNotBlank(signUp.getUsername()));
        checkArgument(isNotBlank(signUp.getPassword()));
        checkArgument(isNotBlank(signUp.getEmail()));

        HttpResponse response = post(clientProvider.getConfig().getUserManagementApi(), new AdminSignUpCredentials(signUp, roles, consent));
        return response.getStatusLine().getStatusCode() == 201;
    }
    @Override
    public boolean deleteUser(String email) {
        session.checkSignedIn();
        checkArgument(isNotBlank(email));

        Map<String,String> queryParams = new HashMap<>();
        queryParams.put("email", email);

        HttpResponse response = delete(clientProvider.getConfig().getUserManagementApi() + toQueryString(queryParams));
        return response.getStatusLine().getStatusCode() == 200;
    }
    @Override
    public Study getStudy(String identifier) {
        session.checkSignedIn();
        return get(clientProvider.getConfig().getAdminStudyApi(identifier), Study.class);
    }
    @Override
    public ResourceList<Study> getAllStudies() {
        session.checkSignedIn();
        return get(clientProvider.getConfig().getAdminStudiesApi(), new TypeReference<ResourceListImpl<Study>>() {});
    }
    @Override
    public VersionHolder createStudy(Study study) {
        session.checkSignedIn();
        return post(clientProvider.getConfig().getAdminStudiesApi(), study, SimpleVersionHolder.class);
    }
    @Override
    public VersionHolder updateStudy(Study study) {
        session.checkSignedIn();
        return post(clientProvider.getConfig().getAdminStudyApi(study.getIdentifier()), study,
                SimpleVersionHolder.class);
    }
    @Override
    public void deleteStudy(String identifier) {
        session.checkSignedIn();
        delete(clientProvider.getConfig().getAdminStudyApi(identifier));
    }

    @Override
    public void deleteSurvey(String guid, DateTime createdOn) {
        session.checkSignedIn();
        delete(clientProvider.getConfig().getSurveyApi(guid, createdOn));
    }

    @Override
    public void deleteSurvey(GuidCreatedOnVersionHolder keys) {
        session.checkSignedIn();
        delete(clientProvider.getConfig().getSurveyApi(keys.getGuid(), keys.getCreatedOn()));
    }
}
