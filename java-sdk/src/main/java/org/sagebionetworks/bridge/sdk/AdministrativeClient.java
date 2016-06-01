package org.sagebionetworks.bridge.sdk;

import org.sagebionetworks.bridge.sdk.models.studies.Study;

public interface AdministrativeClient {

    /**
     * Get the study this administrative user is associated to. Only developers can update this 
     * study object.
     * 
     * @return study
     */
    Study getStudy();

}
