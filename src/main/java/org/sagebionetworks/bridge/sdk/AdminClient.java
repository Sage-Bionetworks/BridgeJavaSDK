package org.sagebionetworks.bridge.sdk;

import org.sagebionetworks.bridge.sdk.models.ResourceList;
import org.sagebionetworks.bridge.sdk.models.holders.GuidCreatedOnVersionHolder;
import org.sagebionetworks.bridge.sdk.models.holders.VersionHolder;
import org.sagebionetworks.bridge.sdk.models.studies.Study;
import org.sagebionetworks.bridge.sdk.models.users.SignUpByAdmin;

public interface AdminClient {

    /**
     * Create a user.
     *
     * @param signUp
     *            Credentials used to sign up the user into Bridge.
     * @return true if success, false if failure.
     */
    public boolean createUser(SignUpByAdmin signUp);

    /**
     * Delete a user.
     *
     * @param email
     *            Email address identifying the user to delete.
     * @return true if success, false if failure.
     */
    public boolean deleteUser(String email);

    public Study getStudy(String identifier);

    public ResourceList<Study> getAllStudies();

    public VersionHolder createStudy(Study study);

    public VersionHolder updateStudy(Study study);

    public void deleteStudy(String identifier);

    /**
     * Delete a survey permanently. This delete will occur whether or not the survey version is in use 
     * or not; developers should use the deleteSurvey() method on the DeveloperClient.
     * @param keys
     */
    public void deleteSurveyPermanently(GuidCreatedOnVersionHolder keys);
    
    public ResourceList<String> getCacheItemKeys();
    
    public void deleteCacheKey(String key);

}
