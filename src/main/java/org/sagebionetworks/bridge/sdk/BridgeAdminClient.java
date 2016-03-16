package org.sagebionetworks.bridge.sdk;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.http.HttpResponse;

import org.sagebionetworks.bridge.sdk.models.ResourceList;
import org.sagebionetworks.bridge.sdk.models.holders.GuidCreatedOnVersionHolder;
import org.sagebionetworks.bridge.sdk.models.holders.SimpleVersionHolder;
import org.sagebionetworks.bridge.sdk.models.holders.VersionHolder;
import org.sagebionetworks.bridge.sdk.models.studies.Study;
import org.sagebionetworks.bridge.sdk.models.subpopulations.SubpopulationGuid;
import org.sagebionetworks.bridge.sdk.models.users.SignUpByAdmin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

final class BridgeAdminClient extends BaseApiCaller implements AdminClient {
    
    private static final TypeReference<ResourceListImpl<Study>> STUDY_RESOURCE_LIST = 
            new TypeReference<ResourceListImpl<Study>>() {};
            
    private static final TypeReference<List<String>> STRING_LIST = 
            new TypeReference<List<String>>() {};
            
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
    public void deleteUser(String email) {
        session.checkSignedIn();
        checkArgument(isNotBlank(email));

        Map<String, String> queryParams = new HashMap<String, String>();
        queryParams.put("email", email);

        delete(config.getUsersApi() + toQueryString(queryParams));
    }

    @Override
    public Study getStudy(String identifier) {
        session.checkSignedIn();
        return get(config.getStudyApi(identifier), Study.class);
    }

    @Override
    public ResourceList<Study> getAllStudies() {
        session.checkSignedIn();
        return get(config.getStudiesApi(), STUDY_RESOURCE_LIST);
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
    public void deleteSurveyPermanently(GuidCreatedOnVersionHolder keys) {
        session.checkSignedIn();
        delete(config.getDeleteSurveyPermanentlyApi(keys.getGuid(), keys.getCreatedOn()));
    }

    @Override
    public List<String> getCacheItemKeys() {
        session.checkSignedIn();
        return get(config.getCacheApi(), STRING_LIST);
    }

    @Override
    public void deleteCacheKey(String key) {
        session.checkSignedIn();
        delete(config.getCacheApi(key));
    }
    
    @Override
    public void deleteSubpopulationPermanently(SubpopulationGuid subpopGuid) {
        session.checkSignedIn();
        delete(config.getSubpopulation(subpopGuid.getGuid())+"?physical=true");
    }
    
}
