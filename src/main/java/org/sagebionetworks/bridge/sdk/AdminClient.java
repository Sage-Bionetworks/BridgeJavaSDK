package org.sagebionetworks.bridge.sdk;

import org.sagebionetworks.bridge.sdk.models.ResourceList;
import org.sagebionetworks.bridge.sdk.models.holders.GuidCreatedOnVersionHolder;
import org.sagebionetworks.bridge.sdk.models.holders.VersionHolder;
import org.sagebionetworks.bridge.sdk.models.studies.Study;
import org.sagebionetworks.bridge.sdk.models.subpopulations.SubpopulationGuid;
import org.sagebionetworks.bridge.sdk.models.users.SignUpByAdmin;

import java.util.List;

public interface AdminClient {

    /**
     * Create a user.
     *
     * @param signUp
     *            Credentials used to sign up the user into Bridge.
     * @return true if success, false if failure.
     */
    boolean createUser(SignUpByAdmin signUp);

    /**
     * Delete a user.
     *
     * @param email
     *            Email address identifying the user to delete.
     */
    void deleteUser(String email);

    Study getStudy(String identifier);

    ResourceList<Study> getAllStudies();

    VersionHolder createStudy(Study study);

    VersionHolder updateStudy(Study study);

    void deleteStudy(String identifier);

    /**
     * Delete a survey permanently. This delete will occur whether or not the survey version is in use 
     * or not; developers should use the deleteSurvey() method on the DeveloperClient.
     * @param keys
     */
    void deleteSurveyPermanently(GuidCreatedOnVersionHolder keys);
    
    List<String> getCacheItemKeys();
    
    void deleteCacheKey(String key);
    
    /**
     * Delete a subpopulation from the database. Only used for integration tests.
     * 
     * @see org.sagebionetworks.bridge.sdk.DeveloperClient#deleteSubpopulation
     * @param subpopGuid the subpopulation to be deleted
     */
    void deleteSubpopulationPermanently(SubpopulationGuid subpopGuid);

}
