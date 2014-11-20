package org.sagebionetworks.bridge.sdk;

import org.sagebionetworks.bridge.sdk.models.ResourceList;
import org.sagebionetworks.bridge.sdk.models.holders.VersionHolder;
import org.sagebionetworks.bridge.sdk.models.studies.Study;

import com.fasterxml.jackson.core.type.TypeReference;

class StudyApiCaller extends BaseApiCaller {

    private StudyApiCaller(Session session) {
        super(session);
    }

    static StudyApiCaller valueOf(Session session) {
        return new StudyApiCaller(session);
    }
    
    Study getStudyForResearcher() {
        return get(config.getResearcherStudyApi(), Study.class);
    }
    VersionHolder updateStudyForResearcher(Study study) {
        return post(config.getResearcherStudyApi(), study, SimpleVersionHolder.class);
    }
    VersionHolder updateStudyForAdmin(Study study) {
        return post(config.getAdminStudyApi(study.getIdentifier()), study, SimpleVersionHolder.class);
    }
    Study getStudyForAdmin(String identifier) {
        return get(config.getAdminStudyApi(identifier), Study.class);
    }
    ResourceList<Study> getAllStudies() {
        return get(config.getAdminStudiesApi(), new TypeReference<ResourceListImpl<Study>>() {});
    }
    VersionHolder createStudy(Study study) {
        return post(config.getAdminStudiesApi(), study, SimpleVersionHolder.class);
    }
    void deleteStudy(String identifier) {
        delete(config.getAdminStudyApi(identifier));
    }
    
}
