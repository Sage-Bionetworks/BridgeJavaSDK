package org.sagebionetworks.bridge.sdk;

import org.sagebionetworks.bridge.sdk.models.studies.Study;

public interface StudyStaffClient {

    /**
     * Get the study this study staff user is associated to. Any system user can retrieve information 
     * about the study's configuration, although only developers can update a study.
     * 
     * @return study
     */
    Study getStudy();

}
