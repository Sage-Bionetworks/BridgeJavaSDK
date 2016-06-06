package org.sagebionetworks.bridge.sdk;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.sagebionetworks.bridge.sdk.models.ResourceList;
import org.sagebionetworks.bridge.sdk.models.accounts.StudyParticipant;
import org.sagebionetworks.bridge.sdk.models.holders.GuidCreatedOnVersionHolder;
import org.sagebionetworks.bridge.sdk.models.holders.SimpleVersionHolder;
import org.sagebionetworks.bridge.sdk.models.holders.VersionHolder;
import org.sagebionetworks.bridge.sdk.models.studies.Study;
import org.sagebionetworks.bridge.sdk.models.subpopulations.SubpopulationGuid;
import org.sagebionetworks.bridge.sdk.utils.Utilities;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

final class BridgeAdminClient extends BridgeStudyStaffClient implements AdminClient {
    
    private static final TypeReference<ResourceList<Study>> STUDY_RESOURCE_LIST = 
            new TypeReference<ResourceList<Study>>() {};
            
    private static final TypeReference<List<String>> STRING_LIST = 
            new TypeReference<List<String>>() {};
            
    BridgeAdminClient(BridgeSession session) {
        super(session);
    }
    
    @Override
    public String createUser(StudyParticipant participant, boolean consentUser) {
        session.checkSignedIn();

        ObjectNode node = (ObjectNode)Utilities.getMapper().valueToTree(participant);
        node.put("consent", Boolean.toString(consentUser));
        
        UserSession userSession = post(config.getUsersApi(), node, UserSession.class);
        
        return userSession.getStudyParticipant().getId();
    }

    @Override
    public void deleteUser(String id) {
        session.checkSignedIn();
        checkArgument(isNotBlank(id));

        delete(config.getUserApi(id));
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
