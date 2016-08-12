package org.sagebionetworks.bridge.sdk;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import org.joda.time.DateTime;

import org.sagebionetworks.bridge.sdk.models.DateTimeRangeResourceList;
import org.sagebionetworks.bridge.sdk.models.ResourceList;
import org.sagebionetworks.bridge.sdk.models.holders.SimpleVersionHolder;
import org.sagebionetworks.bridge.sdk.models.holders.VersionHolder;
import org.sagebionetworks.bridge.sdk.models.studies.Study;
import org.sagebionetworks.bridge.sdk.models.upload.Upload;

import com.fasterxml.jackson.core.type.TypeReference;

public class StudyClient extends BaseApiCaller {
    
    private static final TypeReference<ResourceList<Study>> STUDY_RESOURCE_LIST = 
            new TypeReference<ResourceList<Study>>() {};
            
    private static final TypeReference<DateTimeRangeResourceList<Upload>> UPLOAD_PAGED_RESOURCE_LIST =
            new TypeReference<DateTimeRangeResourceList<Upload>>() {};
            
    StudyClient(BridgeSession session) {
        super(session);
    }
    
    /**
     * Get the study of the current user.
     */
    public Study getCurrentStudy() {
        session.checkSignedIn();
        return get(config.getCurrentStudyApi(), Study.class);
    }
    
    /**
     * Update information about the study of the current user. 
     */
    public VersionHolder updateCurrentStudy(Study study) {
        session.checkSignedIn();
        checkNotNull(study, CANNOT_BE_NULL, "study");
        checkNotNull(isNotBlank(study.getIdentifier()), CANNOT_BE_BLANK, "study identifier");
        
        VersionHolder holder = post(config.getCurrentStudyApi(), study, SimpleVersionHolder.class);
        study.setVersion(holder.getVersion());
        return holder;
    }
    

    public Study getStudy(String identifier) {
        session.checkSignedIn();
        return get(config.getStudyApi(identifier), Study.class);
    }

    public ResourceList<Study> getAllStudies() {
        session.checkSignedIn();
        return get(config.getStudiesApi(), STUDY_RESOURCE_LIST);
    }

    public VersionHolder createStudy(Study study) {
        session.checkSignedIn();
        VersionHolder holder = post(config.getStudiesApi(), study, SimpleVersionHolder.class);
        study.setVersion(holder.getVersion());
        return holder;
    }

    public VersionHolder updateStudy(Study study) {
        session.checkSignedIn();
        VersionHolder holder = post(config.getStudyApi(study.getIdentifier()), study, SimpleVersionHolder.class);
        study.setVersion(holder.getVersion());
        return holder;
    }

    public void deleteStudy(String identifier) {
        session.checkSignedIn();
        delete(config.getStudyApi(identifier));
    }
    
    /**
     * Get the uploads for this study (for up to two days, at any point in time). This upload object is immutable 
     * information about the status of the upload, from the initial request to whether or not it is successfully uploaded 
     * and validated.
     * @param startTime
     *      An optional start time for the search query (if null, defaults to a day before the end time) 
     * @param endTime
     *      An optional end time for the search query (if null, defaults to the time of the request)
     * @return
     */
    public DateTimeRangeResourceList<Upload> getUploads(DateTime startTime, DateTime endTime) {
        session.checkSignedIn();

        return get(config.getCurrentStudyUploadsApi(startTime, endTime), UPLOAD_PAGED_RESOURCE_LIST);
    }    
}
