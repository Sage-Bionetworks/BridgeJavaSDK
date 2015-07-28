package org.sagebionetworks.bridge.sdk;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.joda.time.DateTime;
import org.sagebionetworks.bridge.sdk.models.ResourceList;
import org.sagebionetworks.bridge.sdk.models.holders.GuidCreatedOnVersionHolder;
import org.sagebionetworks.bridge.sdk.models.holders.SimpleVersionHolder;
import org.sagebionetworks.bridge.sdk.models.holders.VersionHolder;
import org.sagebionetworks.bridge.sdk.models.studies.Study;
import org.sagebionetworks.bridge.sdk.models.users.SignUpByAdmin;

import com.fasterxml.jackson.core.type.TypeReference;

final class BridgeAdminClient extends BaseApiCaller implements AdminClient {

    BridgeAdminClient(BridgeSession session) {
        super(session);
    }

    @Override
    public boolean createUser(SignUpByAdmin signUp) {
        session.checkSignedIn();

        HttpResponse response = post(config.getUsersApi(), signUp);
        return response.getStatusLine().getStatusCode() == 201;
    }

    @Override
    public boolean deleteUser(String email) {
        session.checkSignedIn();
        checkArgument(isNotBlank(email));

        Map<String, String> queryParams = new HashMap<String, String>();
        queryParams.put("email", email);

        HttpResponse response = delete(config.getUsersApi() + toQueryString(queryParams));
        return response.getStatusLine().getStatusCode() == 200;
    }

    @Override
    public Study getStudy(String identifier) {
        session.checkSignedIn();
        return get(config.getStudyApi(identifier), Study.class);
    }

    @Override
    public ResourceList<Study> getAllStudies() {
        session.checkSignedIn();
        return get(config.getStudiesApi(), new TypeReference<ResourceListImpl<Study>>() {});
    }

    @Override
    public VersionHolder createStudy(Study study) {
        session.checkSignedIn();
        VersionHolder holder = post(config.getStudiesApi(), study, SimpleVersionHolder.class);
        study.setVersion(holder.getVersion());
        return holder;
    }

    @Override
    public VersionHolder updateStudy(Study study) {
        session.checkSignedIn();
        VersionHolder holder = post(config.getStudyApi(study.getIdentifier()), study, SimpleVersionHolder.class);
        study.setVersion(holder.getVersion());
        return holder;
    }

    @Override
    public void deleteStudy(String identifier) {
        session.checkSignedIn();
        delete(config.getStudyApi(identifier));
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

    @Override
    public ResourceList<String> getCacheItemKeys() {
        session.checkSignedIn();
        return get(config.getCacheApi(), new TypeReference<ResourceListImpl<String>>() {});
    }

    @Override
    public void deleteCacheKey(String key) {
        session.checkSignedIn();
        delete(config.getCacheKeyApi(key));
    }
}
