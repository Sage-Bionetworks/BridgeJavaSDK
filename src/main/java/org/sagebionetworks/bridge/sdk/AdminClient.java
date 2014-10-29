package org.sagebionetworks.bridge.sdk;

import java.util.List;

import org.joda.time.DateTime;
import org.sagebionetworks.bridge.sdk.models.SignUpCredentials;
import org.sagebionetworks.bridge.sdk.models.StudyConsent;

public interface AdminClient {

    public List<StudyConsent> getAllConsentDocuments();

    public StudyConsent getMostRecentlyActivatedConsentDocument();

    public StudyConsent getConsentDocument(DateTime createdOn);

    public void addConsentDocument(StudyConsent consent);

    public void activateConsentDocument(DateTime createdOn);

    public boolean createUser(SignUpCredentials signUp, List<String> roles, boolean consent);

    public boolean deleteUser(String email);

}
