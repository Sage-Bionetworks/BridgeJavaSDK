package org.sagebionetworks.bridge.sdk;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.List;

import org.sagebionetworks.bridge.sdk.models.ResourceList;
import org.sagebionetworks.bridge.sdk.models.accounts.StudyParticipant;
import org.sagebionetworks.bridge.sdk.models.holders.GuidCreatedOnVersionHolder;
import org.sagebionetworks.bridge.sdk.models.holders.SimpleVersionHolder;
import org.sagebionetworks.bridge.sdk.models.holders.VersionHolder;
import org.sagebionetworks.bridge.sdk.models.studies.Study;
import org.sagebionetworks.bridge.sdk.models.subpopulations.SubpopulationGuid;
import org.sagebionetworks.bridge.sdk.utils.BridgeUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.node.ObjectNode;

public final class AdminClient extends StudyStaffClient {
    
    private static final TypeReference<ResourceList<Study>> STUDY_RESOURCE_LIST = 
            new TypeReference<ResourceList<Study>>() {};
            
    private static final TypeReference<List<String>> STRING_LIST = 
            new TypeReference<List<String>>() {};
            
    AdminClient(BridgeSession session) {
        super(session);
    }
    
    /**
     * Create a user.
     *
     * @param participant
     *          Study participant or administrator to create for this study.
     * @param consentUser
     *          Should this user be consented to all consents (useful for testing).
     * @return the ID of this newly created user
     */
    public String createUser(StudyParticipant participant, boolean consentUser) {
        session.checkSignedIn();

        ObjectNode node = (ObjectNode)BridgeUtils.getMapper().valueToTree(participant);
        node.put("consent", Boolean.toString(consentUser));
        
        UserSession userSession = post(config.getUsersApi(), node, UserSession.class);
        
        return userSession.getStudyParticipant().getId();
    }
    
    /**
     * Delete a user.
     *
     * @param email
     *            Email address identifying the user to delete.
     */
    public void deleteUser(String email) {
        session.checkSignedIn();
        checkArgument(isNotBlank(email));

        delete(config.getUserApi(email));
    }

    /**
     * Get a study.
     * 
     * @param identifier
     *          The study's identifier
     * @return
     *          The study
     */
    public Study getStudy(String identifier) {
        session.checkSignedIn();
        return get(config.getStudyApi(identifier), Study.class);
    }

    /**
     * Get all studies.
     * @return
     *      A list of all studies on the server
     */
    public ResourceList<Study> getAllStudies() {
        session.checkSignedIn();
        return get(config.getStudiesApi(), STUDY_RESOURCE_LIST);
    }

    /**
     * Create a new study
     * @param study
     *      The study to create
     * @return
     *      An object with the version of the newly created study
     */
    public VersionHolder createStudy(Study study) {
        session.checkSignedIn();
        VersionHolder holder = post(config.getStudiesApi(), study, SimpleVersionHolder.class);
        study.setVersion(holder.getVersion());
        return holder;
    }

    /**
     * Update an existing study
     * @param study
     *      The study to create
     * @return
     *      An object with the updated version of the study 
     */
    public VersionHolder updateStudy(Study study) {
        session.checkSignedIn();
        VersionHolder holder = post(config.getStudyApi(study.getIdentifier()), study, SimpleVersionHolder.class);
        study.setVersion(holder.getVersion());
        return holder;
    }

    /**
     * Delete a study
     * @param identifier
     *      The identifier of the study to delete
     */
    public void deleteStudy(String identifier) {
        session.checkSignedIn();
        delete(config.getStudyApi(identifier));
    }

    /**
     * Delete a survey permanently. This delete will occur whether or not the survey version is in use 
     * or not; developers should use the deleteSurvey() method on the DeveloperClient.
     * @param keys
     *      The keys of a survey (guid and creaedOn timestamp) to delete physically on the server.
     */
    public void deleteSurveyPermanently(GuidCreatedOnVersionHolder keys) {
        session.checkSignedIn();
        delete(config.getDeleteSurveyPermanentlyApi(keys.getGuid(), keys.getCreatedOn()));
    }

    /**
     * Get a list of all the string keys under which items are cached on Bridge. This does not include
     * users sessions.
     * @return
     *      A list of the strings under which objects (like surveys or studies) have been cached
     */
    public List<String> getCacheItemKeys() {
        session.checkSignedIn();
        return get(config.getCacheApi(), STRING_LIST);
    }

    /**
     * Remove a specific item from the cache.
     * @param key
     *      The key of the item to remove from the cache.
     */
    public void deleteCacheKey(String key) {
        session.checkSignedIn();
        delete(config.getCacheApi(key));
    }
    
    /**
     * Delete a subpopulation from the database. Only used for integration tests.
     * 
     * @see org.sagebionetworks.bridge.sdk.DeveloperClient#deleteSubpopulation
     * 
     * @param subpopGuid
     *      the subpopulation to be deleted
     */
    public void deleteSubpopulationPermanently(SubpopulationGuid subpopGuid) {
        session.checkSignedIn();
        delete(config.getSubpopulation(subpopGuid.getGuid())+"?physical=true");
    }
    
}
