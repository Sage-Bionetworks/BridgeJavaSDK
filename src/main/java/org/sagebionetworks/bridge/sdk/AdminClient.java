package org.sagebionetworks.bridge.sdk;

import java.util.List;

import org.sagebionetworks.bridge.sdk.models.ResourceList;
import org.sagebionetworks.bridge.sdk.models.holders.VersionHolder;
import org.sagebionetworks.bridge.sdk.models.studies.Study;
import org.sagebionetworks.bridge.sdk.models.users.SignUpCredentials;

public interface AdminClient {

    /**
     * Create a user.
     *
     * @param signUp
     *            Credentials used to sign up the user into Bridge.
     * @param roles
     *            Roles assigned to the user ("studykey_researcher", "studykey_admin", or null for participants).
     * @param consent
     *            Whether the user should be automatically consented upon sign up.
     * @return true if success, false if failure.
     */
    public boolean createUser(SignUpCredentials signUp, List<String> roles, boolean consent);

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

}
